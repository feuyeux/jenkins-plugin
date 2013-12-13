package com.technicolor.android.ci;

import com.technicolor.android.ci.util.ParamTools;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Upgrade the build package to device
 * <p/>
 * User: erichan
 * Date: 11/22/13
 * Time: 10:15 AM
 */
public class UpgradeBuilder extends hudson.tasks.Builder {
    public static final String LOG_PREFIX = "UpgradeBuilder::";
    @Extension
    public static final UpgradeDescriptor DESCRIPTOR = new UpgradeDescriptor();
    /*Upgrade*/


    @DataBoundConstructor
    public UpgradeBuilder() {
    }

    @Override
    public Descriptor<Builder> getDescriptor() {
        return (UpgradeDescriptor) super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        handleUpgrade(build, listener);
        return true;
    }

    private void handleUpgrade(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
        listener.getLogger().println(LOG_PREFIX + "START UPGRADE...");
        /*
        UpgradeAPI :
        Input :upgrade -u buildURL -v newVersionNumber(optional) -n SN1 -n SN2 -n SN3(if no -n specified, all devices in the station will upgrade) -t upgradeTimeout(default 6mins) -s atsdHost(default localhost) -p atsdPort(default 9999)
        Output : print deviceSN upgrade succeeded. or deviceSN upgrade failed.

        upgrade-devices.py -u "http://10.11.71.32:8080/view/telstra2/job/telstra2b-usr-dailyBuild/lastSuccessfulBuild/artifact/402-user-4.0.4_3.42-FOTA.zip" -s 10.11.72.61

        */

        String cases = ParamTools.retrieve(build, PluginConstant.CASES);
        if (cases == null) {
            String reason = "ERROR: Not find Test Case Id.";
            listener.getLogger().println(LOG_PREFIX + reason);
            throw new InterruptedException(reason);
        }

        ConcurrentHashMap<String, String> upgradeTaskMap = new ConcurrentHashMap<>();

        String[] testCaseIds = cases.split(",");
        for (int i = 0; i < testCaseIds.length; i++) {
            listener.getLogger().println("----");
            String caseParam = PluginConstant.CASE_ID + testCaseIds[i];
            String deviceBuildVersion = ParamTools.retrieve(build, caseParam + ".ro.build.version.product");
            listener.getLogger().println(LOG_PREFIX + "caseParam=" + caseParam);
            String hostIp = ParamTools.retrieve(build, caseParam + PluginConstant.HOST_IP);

            /*for demo3*/
            String buildFile = ParamTools.retrieve(build, PluginConstant.CI_BUILD_FILE);
            String[] files = buildFile.split("~");
            String ci_build_file_names = null;
            listener.getLogger().println(LOG_PREFIX + "Test Station:" + hostIp + " current BuildVersion=" + deviceBuildVersion);
            if (deviceBuildVersion.contains("4.0.4_3.35")) {
                for (String file : files) {
                    if (file.contains("4.0.4_3.36")) {
                        ci_build_file_names = file;
                        break;
                    }
                }
            } else {
                for (String file : files) {
                    if (file.contains("4.0.4_3.35")) {
                        ci_build_file_names = file;
                        break;
                    }
                }
            }
            listener.getLogger().println(LOG_PREFIX + "HOST IP=" + hostIp);
            listener.getLogger().println(LOG_PREFIX + "buildFile [end] = " + ci_build_file_names);
            /*for demo3*/

            upgradeTaskMap.put(hostIp, ci_build_file_names);
        }
        Iterator<Map.Entry<String, String>> it = upgradeTaskMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String hostIp = entry.getKey();
            if (hostIp != null) {
                String executeShell = "upgrade-devices.py -u " + entry.getValue() + " -s " + hostIp;
                listener.getLogger().println(LOG_PREFIX + "command= " + executeShell);
                String upgradeResult = CmdExec.exec(listener, executeShell);
                listener.getLogger().println(LOG_PREFIX + "upgradeResult=" + upgradeResult);
            } else {

            }
        }
        listener.getLogger().println(LOG_PREFIX + "UPGRADE DONE.");
    }
}
