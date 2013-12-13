package com.technicolor.android.ci;

import com.technicolor.android.ci.util.ParamTools;
import com.technicolor.android.ci.util.ResultParser;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.File;
import java.io.IOException;

/**
 * Run test and save the result in a XML file
 * <p/>
 * User: erichan
 * Date: 11/11/13
 * Time: 3:30 PM
 */
public class RunTestBuilder extends hudson.tasks.Builder {
    @Extension
    public static final RunTestDescriptor DESCRIPTOR = new RunTestDescriptor();
    public static final String LOG_PREFIX = "ResultBuilder::";
    private static final String VERIFY_VALUE = "Total Test status=";
    private static final String PROJECT = "project";
    private static final String FILENAME = "filename";
    private final String resultpath;

    @DataBoundConstructor
    public RunTestBuilder(String resultpath) {
        this.resultpath = resultpath;
    }

    @Override
    public Descriptor<Builder> getDescriptor() {
        return (RunTestDescriptor) super.getDescriptor();
    }

    public String getResultpath() {
        return resultpath;
    }

    /**
     * @param build
     * @param launcher
     * @param listener
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        listener.getLogger().println(LOG_PREFIX + "result builder plugin is working...");

        String cases = ParamTools.retrieve(build, "CASE_ID");
        String[] testCaseIds = cases.split(",");
        for (int i = 0; i < testCaseIds.length; i++) {
            runTest(build, testCaseIds[i], listener);
        }
        return true;
    }

    /*
    RunTest :
    Input : ats2 -f projectName/fileName -t className#methodName -n SN1 -n SN2 -n SN3(if no -n specified, will randomly select devices) -s atsdHost(default localhost) -p atsdPort(default 9999)
    Output format :
       succeed:
          ats2 -f MCBB/videoPerformance.py
          test result will be summarized in file /tmp/ats2-2013-11-21-11-41-29.html
          TEST_STATUS: test=videoPerformance.VideoPerformanceTest#testMOVcodecPerformance
          TEST_STATUS: type=Python
          TEST_STATUS: value=LOG=/tmp/testMOVcodecPerformance-2013-11-21-11-41-43.log
          TEST_STATUS: value=LOG=/tmp/testMOVcodecPerformance-2013-11-21-11-41-43_results.dat
          TEST_STATUS: value=LOG=/tmp/testMOVcodecPerformance-2013-11-21-11-41-43_total.txt
          TEST_STATUS: value=LOG=/tmp/testMOVcodecPerformance-2013-11-21-11-41-43_process.txt
          TEST_STATUS: value=LOG=/tmp/testMOVcodecPerformance-2013-11-21-11-41-43_results.xml
          TEST_STATUS: value=LOG=/tmp/testMOVcodecPerformance-2013-11-21-11-41-43_graph.png
          TEST_STATUS: value=key1=value1
          TEST_STATUS: value=key2=value2
          TEST_STATUS: result=PASS
          TEST_STATUS: elapsed=115.866

          Total test cases : 1
          Success : 1
          Failure : 0
          Error : 0
          NotRun : 0

     fail:
          ats2 -f MCBB/videoPerformance.py
          test result will be summarized in file /tmp/ats2-2013-11-21-11-48-13.html
          TEST_STATUS: test=videoPerformance.VideoPerformanceTest#testMOVcodecPerformance
          TEST_STATUS: type=Python
          TEST_STATUS: info=try to lock 1 devices from adb server failed
          TEST_STATUS: result=NOTRUN
          2013-11-21 11:48:13 ERROR    [Auto.AtsClient]  notify listener TestHtmlRecorder error: Traceback (most recent call last):
          File "/home/automation/GIT/automation/pylibs/atsc.py", line 61, in notifyListeners
    method(test, *args, **kwargs)
          File "/home/automation/GIT/automation/pylibs/test/listener.py", line 378, in testDone
    self.write(record)
          File "/home/automation/GIT/automation/tools/ats2", line 223, in write
    spend_time = record.mStopTime - record.mStartTime
          TypeError: unsupported operand type(s) for -: 'NoneType' and 'NoneType'


          Total test cases : 1
          Success : 0
          Failure : 0
          Error : 0
          NotRun : 1

    testcases: ats2 -f Telstra/mediaNetworkStreaming.py (13cases)
    */

    /**
     * execute test scripts on test station
     *
     * @param testCaseId
     * @param listener
     */
    private void runTest(AbstractBuild build, String testCaseId, BuildListener listener) {
        String caseParam = PluginConstant.CASE_ID + testCaseId;
        String hostIp = ParamTools.retrieve(build, caseParam + PluginConstant.HOST_IP);
        String projectName = ParamTools.retrieve(build, caseParam + PluginConstant.PROJECT_NAME);
        String fileName = ParamTools.retrieve(build, caseParam + PluginConstant.FILE_NAME) + ".py";
        String className = ParamTools.retrieve(build, caseParam + PluginConstant.CLASS_NAME);
        String methodName = ParamTools.retrieve(build, caseParam + PluginConstant.METHOD_NAME);

        String executeShell = " ats2 -f " + projectName + "/" + fileName + " -t " + className + "#" + methodName + " -s " + hostIp;
        listener.getLogger().println(LOG_PREFIX + "command=" + executeShell);


        listener.getLogger().println("workspace=" + build.getWorkspace());
        listener.getLogger().println("number=" + build.getNumber());

        int nameIndex = resultpath.lastIndexOf("/");
        String folder = resultpath.substring(0, nameIndex + 1) + build.getNumber();

        File f = new File(folder);
        if (!f.exists()) {
            f.mkdir();
        }
        String name = resultpath.substring(nameIndex + 1);
        String resultFile = folder + "/" + name;
        listener.getLogger().println(LOG_PREFIX + "RESULT_XML_FILE_PATH=" + resultFile);

        ResultParser parser = new ResultParser();
        boolean buildSuccess = parser.invoke(new String[]{executeShell, resultFile});

        if (buildSuccess) {
            ParamTools.store(build, VERIFY_VALUE, "success");
            listener.getLogger().println(LOG_PREFIX + VERIFY_VALUE + " success");
        } else {
            ParamTools.store(build, VERIFY_VALUE, "failed");
            listener.getLogger().println(LOG_PREFIX + VERIFY_VALUE + " failed");
        }
    }
}
