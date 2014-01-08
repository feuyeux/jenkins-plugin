package com.technicolor.android.ci;

import com.google.gson.JsonObject;
import com.technicolor.android.ci.util.RallyClient;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by erichan on 1/7/14.
 */
public class RallyDevBuilder extends Builder {
    @Extension
    public static final RallyDevDescriptor DESCRIPTOR = new RallyDevDescriptor();
    public static final String RALLY_URL = "https://rally1.rallydev.com";
    private static final String LOG_PREFIX = "RallyDevBuilder::";
    private final String testCaseId;

    @DataBoundConstructor
    public RallyDevBuilder(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        PrintStream out = listener.getLogger();
        final String userName = DESCRIPTOR.getUserName();
        final String password = DESCRIPTOR.getPassword();
        final String proxyURL = DESCRIPTOR.getProxyURL();
        final String proxyUser = DESCRIPTOR.getProxyUser();
        final String proxyPassword = DESCRIPTOR.getProxyPassword();

        out.println("RallyDev User Name =" + userName);
        out.println("HTTP Proxy =" + proxyUser + "@" + proxyURL);
        out.println("RallyDev Test Case =" + getTestCaseId() + "\n");

        RallyClient rallyClient = null;
        try {
            rallyClient = new RallyClient(RALLY_URL, userName, password, proxyURL, proxyUser, proxyPassword, out);

            /*test case*/
            JsonObject testCaseJson;
            if (testCaseId.indexOf("TC") > -1) {
                testCaseJson = rallyClient.getCaseByFormattedId(testCaseId);
            } else {
                testCaseJson = rallyClient.getCaseByObjectId(testCaseId);
            }
            out.println(testCaseJson + "\n");

            /*test case result*/
            rallyClient.createTestCaseResult(testCaseJson);
        } catch (Exception e) {
            //out.println(e.getMessage());
            out.println(e);
        } finally {
            try {
                rallyClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    @Override
    public Descriptor<hudson.tasks.Builder> getDescriptor() {
        return (RallyDevDescriptor) super.getDescriptor();
    }
}
