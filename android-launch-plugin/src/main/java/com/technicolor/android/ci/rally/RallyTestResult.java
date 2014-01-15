package com.technicolor.android.ci.rally;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.request.UpdateRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.response.UpdateResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RallyTestResult {
    private final RallyRestApi restApi;
    private final PrintStream log;
    private final String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
    private final SimpleDateFormat format = new SimpleDateFormat(pattern);

    public RallyTestResult(RallyRestApi restApi, PrintStream log) {
        this.restApi = restApi;
        this.log = log;
    }

    public JsonObject createTestCaseResult(String testCaseRef,String userRef) throws IOException, URISyntaxException {
        log.println("createTestCaseResult...");
        JsonObject newResult = new JsonObject();
        newResult.addProperty("Workspace", RallyClient.WORKSPACE_REF);
        newResult.addProperty("Verdict", "Fail");
        newResult.addProperty("Build", "Automation Build");
        newResult.addProperty("Tester", userRef);
        Date now = new Date();
        newResult.addProperty("Date", format.format(now));
        newResult.addProperty("CreationDate", format.format(now));
        newResult.addProperty("TestCase", testCaseRef);

        CreateRequest createRequest = new CreateRequest("testcaseresult", newResult);
        CreateResponse createResponse = restApi.create(createRequest);

        log.println("createTestCaseResult DONE：");
        JsonObject testCaseResultJson = createResponse.getObject();
        log.println(String.format("Created %s", testCaseResultJson.get("_ref").getAsString()));
        return testCaseResultJson;
    }

    public JsonObject updateTestCaseResult(String ref, JsonObject updateResult) throws IOException {
        log.println("updateTestCaseResult...");
        updateResult.addProperty("LastUpdateDate", format.format(new Date()));
        UpdateRequest updateRequest = new UpdateRequest(ref, updateResult);
        UpdateResponse createResponse = restApi.update(updateRequest);

        log.println("updateTestCaseResult DONE：");
        JsonObject testCaseResultJson = createResponse.getObject();
        log.println(String.format("Updated %s", testCaseResultJson.get("_ref").getAsString()));
        return testCaseResultJson;
    }
}
