package com.technicolor.android.ci;

import com.technicolor.android.ci.util.ParamTools;
import com.technicolor.android.ci.util.PluginPropertiesUtil;
import com.technicolor.android.xmlrpc.TestlinkXMLRPCClient;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.ParametersAction;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.HashMap;

/**
 * Choose Station from the commit comments information
 */
public class AndroidBuilder extends hudson.tasks.Builder {
    @Extension
    public static final AndroidDescriptor DESCRIPTOR = new AndroidDescriptor();
    private static final String LOG_PREFIX = "AndroidBuilder::";
    /*Analysis*/
    /*TestLink*/
    private HashMap<String, JobParam> caseJobMap = new HashMap<>();

    @DataBoundConstructor
    public AndroidBuilder() {
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        try {
            //1 analysis commit
            analysisAndStoreComments(build, listener);

            //2 test link
            talk2TestLink(build, listener);

            //3 choose station
            handleStation(build, listener);
            listener.getLogger().println(build.getAction(ParametersAction.class).getParameters());
        } catch (Exception e) {
            e.printStackTrace();
            listener.getLogger().println(e);
            return false;
        }
        return true;
    }

    private void analysisAndStoreComments(AbstractBuild build, BuildListener listener) throws IOException {
        listener.getLogger().println(LOG_PREFIX + "START ANALYSIS COMMENTS...");
        String GERRIT_CHANGE_SUBJECT = ParamTools.retrieve(build, "GERRIT_CHANGE_SUBJECT");
        String[] comments = GERRIT_CHANGE_SUBJECT.split("--");

        //TODO testCaseIds?
        for (String comment : comments) {
            if (comment.matches(".*=.*")) {
                String[] newKeyValue = comment.split("=", 2);
                ParamTools.store(build, newKeyValue[0].trim(), newKeyValue[1].trim());
            }
        }
        listener.getLogger().println(LOG_PREFIX + "ANALYSIS COMMENTS DONE.");
    }

    private void talk2TestLink(AbstractBuild build, BuildListener listener) {
        listener.getLogger().println(LOG_PREFIX + "START TALK TO TEST LINK...");
        String[] testCaseIds = ParamTools.retrieve(build, PluginConstant.CASES).split(",");
        try {
            for (int i = 0; i < testCaseIds.length; i++) {
                String[] result = TestlinkXMLRPCClient.getTestScript(testCaseIds[i]);
                //prepareJobParam(testCaseIds[i], result[0]);
                String[] ps = result[0].split("/");
                String caseParam = PluginConstant.CASE_ID + testCaseIds[i];
                ParamTools.store(build, caseParam + PluginConstant.PROJECT_NAME, ps[1]);
                ParamTools.store(build, caseParam + PluginConstant.FILE_NAME, ps[2]);
                ParamTools.store(build, caseParam + PluginConstant.CLASS_NAME, ps[3]);
                ParamTools.store(build, caseParam + PluginConstant.METHOD_NAME, ps[4]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.getLogger().println(LOG_PREFIX + e.getMessage());
        }
        listener.getLogger().println(LOG_PREFIX + "TALK TO TEST LINK DONE.");
    }

    private StationParam handleStation(AbstractBuild build, BuildListener listener) throws IOException {
        listener.getLogger().println(LOG_PREFIX + "START HANDLE STATION...");
        String assignedStation = ParamTools.retrieve(build, "STATION");
        AndroidDescriptor descriptor = (AndroidDescriptor) getDescriptor();
        StationParam[] stations = descriptor.getStations();
        String[] testCaseIds = ParamTools.retrieve(build, PluginConstant.CASES).split(",");
        String properties = descriptor.getMappingFile();
        PluginPropertiesUtil util = new PluginPropertiesUtil(properties);
        try {
            for (int i = 0; i < testCaseIds.length; i++) {
                String stationUrl = null;
                if (assignedStation != null && assignedStation.length() > 0) {
                    stationUrl = assignedStation;
                } else {
                    String projectName = ParamTools.retrieve(build, PluginConstant.CASE_ID + testCaseIds[i] + PluginConstant.PROJECT_NAME);
                    String product = util.retrieve(projectName);
                    String regex = ".*u'ro.build.product': u'" + product + "'.*";
                    for (StationParam station : stations) {
                        listener.getLogger().println(LOG_PREFIX + "talking with:" + station);
                        String result = CmdExec.exec(listener, "getInfo -m getDeviceInfo");
                        listener.getLogger().println(LOG_PREFIX + "getInfo:" + result);
                        listener.getLogger().println(LOG_PREFIX + "regex:" + regex);

                        stationUrl = station.getUrl();
                        break;
                        /*
                        if (result.matches(regex)) {
                            stationUrl = station.getUrl();
                            break;
                        }
                        */
                    }
                }
                if (stationUrl != null) {
                    listener.getLogger().println(LOG_PREFIX + testCaseIds[i] + "choose station " + stationUrl);
                    ParamTools.store(build, PluginConstant.CASE_ID + testCaseIds[i] + PluginConstant.HOST_IP, stationUrl);
                } else {
                    listener.getLogger().println(LOG_PREFIX + testCaseIds[i] + "find no suitable station !");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.getLogger().println(LOG_PREFIX + e.getMessage());
        }

        listener.getLogger().println(LOG_PREFIX + "HANDLE STATION DONE.");
        return null;
    }

    @Override
    public Descriptor<hudson.tasks.Builder> getDescriptor() {
        return (AndroidDescriptor) super.getDescriptor();
    }
}

