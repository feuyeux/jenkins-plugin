package com.technicolor.android.ci.rally;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.io.PrintStream;

public class RallyTestCase {
    private final RallyRestApi restApi;
    private final PrintStream log;

    public RallyTestCase(RallyRestApi restApi, PrintStream log) {
        this.restApi = restApi;
        this.log = log;
    }

    public JsonObject getCaseByObjectId(String testCaseId) {
        log.println("getCaseByObjectId...");
        final String query = "/testcase/" + testCaseId;
        GetRequest queryRequest = new GetRequest(query);
        GetResponse casesResponse = null;
        try {
            casesResponse = restApi.get(queryRequest);
            JsonObject caseJsonObject = casesResponse.getObject();
            loggingTestCase(caseJsonObject);
            log.println("getCaseByObjectId DONE!");
            return caseJsonObject;
        } catch (IOException e) {
            log.println(e);
            e.printStackTrace();
        }
        log.println("getCaseByObjectId No Result!");
        return null;
    }

    public JsonObject getCaseByFormattedId(String testCaseId) throws IOException {
        log.println("getCaseByFormattedId...");
        QueryRequest queryRequest = new QueryRequest("TestCase");
        queryRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testCaseId));
        queryRequest.setWorkspace(RallyClient.WORKSPACE_REF);
        queryRequest.setProject(RallyClient.PROJECT_REF);
        QueryResponse queryResponse = restApi.query(queryRequest);
        log.println("queryRequest URL =" + queryRequest.toUrl());

        String[] errorMessages = queryResponse.getErrors();
        if (errorMessages.length > 0) {
            log.println(errorMessages[0]);
        } else {
            for (JsonElement result : queryResponse.getResults()) {
                JsonObject caseJsonObject = result.getAsJsonObject();
                loggingTestCase(caseJsonObject);
                log.println("getCaseByFormattedId DONE!");
                return caseJsonObject;
            }
        }
        log.println("getCaseByObjectId No Result!");
        return null;
    }

    private void loggingTestCase(JsonObject caseJsonObject) {
        StringBuilder result = new StringBuilder();
        result.append("\n").append("Test Case FormattedID: ").append(caseJsonObject.get("FormattedID").getAsString());
        result.append("\n").append("Test Case Name: ").append(caseJsonObject.get("Name").getAsString());
        result.append("\n").append("Test Case Type: ").append(caseJsonObject.get("Type").getAsString());
        result.append("\n").append("Test Case URL: ").append(caseJsonObject.get("_ref").getAsString());
        result.append("\n").append("Test Case Creation Time: ").append(caseJsonObject.get("CreationDate").getAsString());
        result.append("\n").append("Test Case LastUpdate Time: ").append(caseJsonObject.get("LastUpdateDate").getAsString());
        result.append("\n").append("Test Case's Project: ").append(caseJsonObject.get("Project").getAsJsonObject().get("_refObjectName").getAsString());
        result.append("\n").append("Test Case's Workspace: ").append(caseJsonObject.get("Workspace").getAsJsonObject().get("_refObjectName").getAsString());
        result.append("\n");
        log.println(result.toString());
    }
}
