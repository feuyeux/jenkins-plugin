package com.technicolor.android.ci;

import com.google.gson.JsonObject;
import com.rallydev.rest.util.Ref;
import com.technicolor.android.ci.rally.RallyClient;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

/**
 * Created by erichan on 1/7/14.
 */
public class RallyDevBuilder extends Builder {
    @Extension
    public static final RallyDevDescriptor DESCRIPTOR = new RallyDevDescriptor();
    private static final String LOG_PREFIX = "RallyDevBuilder::";
    private final String testSetId;
    private final String testCaseId;

    @DataBoundConstructor
    public RallyDevBuilder(String testSetId, String testCaseId) {
        this.testSetId = testSetId;
        this.testCaseId = testCaseId;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        final String userName = DESCRIPTOR.getUserName();
        final String password = DESCRIPTOR.getPassword();
        final String proxyURL = DESCRIPTOR.getProxyURL();
        final String proxyUser = DESCRIPTOR.getProxyUser();
        final String proxyPassword = DESCRIPTOR.getProxyPassword();

        PrintStream out = listener.getLogger();
        RallyClient rallyClient = null;
        try {
            rallyClient = new RallyClient(userName, password, proxyURL, proxyUser, proxyPassword, out);

            testCaseAndResult(rallyClient, out);
            //rallyClient.getTestCasesByTestSetFormattedId(testSetId);

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

    private void testCaseAndResult(RallyClient rallyClient, PrintStream out) throws IOException, URISyntaxException {
        /*retrieve test case*/
        JsonObject testCaseJson;
        if (testCaseId.indexOf("TC") > -1) {
            testCaseJson = rallyClient.getCaseByFormattedId(testCaseId);
        } else {
            testCaseJson = rallyClient.getCaseByObjectId(testCaseId);
        }
        out.println(testCaseJson + "\n");

        /*create test case result*/
        String testCaseRef = testCaseJson.get("_ref").getAsString();
        JsonObject testCaseResultJson = rallyClient.createTestCaseResult(testCaseRef);

        /*create defect*/
        String resultVerdict = testCaseResultJson.get("Verdict").getAsString();
        if (!"Pass".equals(resultVerdict)) {
            String testCaseResultRef = testCaseResultJson.get("_ref").getAsString();
            JsonObject defectJson = rallyClient.getDefectByTestCase(testCaseRef);
            if (defectJson == null) {
                defectJson = rallyClient.createDefect(testCaseRef, testCaseResultRef);
            }
            /*update test case result*/
            JsonObject testCaseResultJson1 = new JsonObject();
            testCaseResultJson1.addProperty("Notes", "Defect ID=" + defectJson.get("FormattedID").getAsString());
            String ref = Ref.getRelativeRef(testCaseResultRef);
            rallyClient.updateTestCaseResult(ref, testCaseResultJson1);
        }
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public String getTestSetId() {
        return testSetId;
    }

    @Override
    public Descriptor<hudson.tasks.Builder> getDescriptor() {
        return (RallyDevDescriptor) super.getDescriptor();
    }
}
