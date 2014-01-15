package com.technicolor.android.ci.rally;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RallyDefect {
    private final RallyRestApi restApi;
    private final PrintStream log;
    private final String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
    private final SimpleDateFormat format = new SimpleDateFormat(pattern);

    public RallyDefect(RallyRestApi restApi, PrintStream log) {
        this.restApi = restApi;
        this.log = log;
    }

    public JsonObject createDefect(String testCaseRef, String testCaseResultRef, String userRef) throws IOException {
        log.println("createDefect...");

        JsonObject newDefect = new JsonObject();
        newDefect.addProperty("Workspace", RallyClient.WORKSPACE_REF);
        newDefect.addProperty("Project", RallyClient.PROJECT_REF);
        newDefect.addProperty("Name", "Defect_TEST");//
        newDefect.addProperty("TaskStatus", "DEFINED");
        newDefect.addProperty("TestCaseStatus", "NONE");
        newDefect.addProperty("Severity", "Major");//
        newDefect.addProperty("State", "Submitted");
        newDefect.addProperty("ScheduleState", "Defined");
        newDefect.addProperty("c_CustomerTicket", "No");
        newDefect.addProperty("Environment", "Test");
        newDefect.addProperty("FoundInBuild", "Automation Build");
        newDefect.addProperty("Priority", "Normal");
        newDefect.addProperty("SubmittedBy", userRef);
        newDefect.addProperty("TestCase", testCaseRef);
        newDefect.addProperty("TestCaseResult", testCaseResultRef);
        //newDefect.addProperty("Package", "Build: Jenkins");
        newDefect.addProperty("LastUpdateDate", format.format(new Date()));
        newDefect.addProperty("OpenedDate", format.format(new Date()));

        CreateRequest createRequest = new CreateRequest("defect", newDefect);
        CreateResponse createResponse = restApi.create(createRequest);
        log.println("createDefect DONE：");

        JsonObject defectJson = createResponse.getObject();
        log.println(String.format("Created %s", defectJson.get("_ref").getAsString()));
        return defectJson;
    }

    public JsonObject getDefectByTestCase(String testCaseRef) throws IOException {
        log.println("getDefectByResult...");
        QueryRequest queryRequest = new QueryRequest("Defect");
        queryRequest.setQueryFilter(new QueryFilter("TestCase", "=", testCaseRef));
        queryRequest.setWorkspace(RallyClient.WORKSPACE_REF);
        queryRequest.setProject(RallyClient.PROJECT_REF);
        queryRequest.setPageSize(10);
        QueryResponse queryResponse = restApi.query(queryRequest);
        log.println("queryRequest URL =" + queryRequest.toUrl());

        String[] errorMessages = queryResponse.getErrors();
        if (errorMessages.length > 0) {
            log.println(errorMessages[0]);
        } else {
            for (JsonElement result : queryResponse.getResults()) {
                JsonObject defectJsonObject = result.getAsJsonObject();
                log.println("getDefectByResult DONE!");
                return defectJsonObject;
            }
        }
        log.println("getDefectByResult No Result!");
        return null;
    }
}
