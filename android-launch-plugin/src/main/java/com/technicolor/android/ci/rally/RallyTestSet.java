package com.technicolor.android.ci.rally;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.io.PrintStream;

public class RallyTestSet {
    private final RallyRestApi restApi;
    private final PrintStream log;

    public RallyTestSet(RallyRestApi restApi, PrintStream log) {
        this.restApi = restApi;
        this.log = log;
    }
    public JsonArray getTestCasesByTestSetFormattedId(String testSetId) throws IOException {
        log.println("getTestCasesByTestSetFormattedId...");
        QueryRequest queryRequest = new QueryRequest("TestSet");
        queryRequest.setQueryFilter(new QueryFilter("FormattedID", "=", testSetId));
        queryRequest.setFetch(new Fetch("ObjectID"));
        queryRequest.setWorkspace(RallyClient.WORKSPACE_REF);
        queryRequest.setProject(RallyClient.PROJECT_REF);
        QueryResponse queryResponse = restApi.query(queryRequest);
        log.println("queryRequest URL =" + queryRequest.toUrl());

        String[] errorMessages = queryResponse.getErrors();
        if (errorMessages.length > 0) {
            log.println(errorMessages[0]);
        } else {
            JsonObject testSet = queryResponse.getResults().get(0).getAsJsonObject();
            String oId = testSet.get("ObjectID").getAsString();
            return getTestSetByTestSetId(oId);
        }
        log.println("getTestCasesByTestSetFormattedId No Result!");
        return null;
    }

    public JsonArray getTestSetByTestSetId(String testSetId) throws IOException {
        log.println("getTestSetByTestSetId...");
        final String query = "/TestSet/" + testSetId + "/TestCases";
        QueryRequest queryRequest = new QueryRequest(query);
        queryRequest.setPageSize(200);
        queryRequest.setFetch(new Fetch("Results", "TotalResultCount", "Errors"));
        QueryResponse queryResponse = restApi.query(queryRequest);
        log.println("queryRequest URL =" + queryRequest.toUrl());
        log.println("TotalResultCount=" + queryResponse.getTotalResultCount());
        JsonArray testCases = queryResponse.getResults();
        log.println(testCases);
        return testCases;
    }
}
