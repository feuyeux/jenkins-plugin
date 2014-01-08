package com.technicolor.android.ci.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.CreateResponse;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.response.Response;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author:feuyeux
 * <p/>
 * rest client: https://github.com/RallyTools/RallyRestToolkitForJava
 * rest client document: https://github.com/RallyTools/RallyRestToolkitForJava/wiki/User-Guide
 * rallydev document: https://rally1.rallydev.com/slm/doc/webservice
 */
public class RallyClient {
    private final PrintStream log;
    private final String workspaceRef = "/workspace/11627233416";//Tablet
    private final String projectRef = "/project/15180679176"; //Name=MCBB
    private RallyRestApi restApi;
    private String rally_url;
    private String userName;
    private String password;
    private String proxyURL;
    private String proxyUser;
    private String proxyPassword;

    public RallyClient(String rally_url, String userName, String password, String proxyURL, String proxyUser, String proxyPassword, PrintStream log) throws URISyntaxException {
        this.rally_url = rally_url;
        this.userName = userName;
        this.password = password;
        this.proxyURL = proxyURL;
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
        getRestApi();
        this.log = log;
    }

    private void getRestApi() throws URISyntaxException {
        restApi = new RallyRestApi(new URI(rally_url), userName, password);
        restApi.setProxy(new URI(proxyURL), proxyUser, proxyPassword);
        restApi.setWsapiVersion("v2.0");
        restApi.setApplicationName("Tablet Jenkins plugin");
    }

    public void close() throws IOException {
        restApi.close();
    }

    public JsonObject getCaseByObjectId(String testCaseId) {
        log.println("getCaseByObjectId...");
        final String query = "/testcase/" + testCaseId;
        GetRequest queryRequest = new GetRequest(query);
        GetResponse casesResponse = null;
        try {
            casesResponse = restApi.get(queryRequest);
            JsonObject caseJsonObject = casesResponse.getObject();
            logging(caseJsonObject);
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
        queryRequest.setWorkspace(workspaceRef);
        queryRequest.setProject(projectRef);
        QueryResponse queryResponse = restApi.query(queryRequest);
        log.println("queryRequest URL =" + queryRequest.toUrl());

        String[] errorMessages = queryResponse.getErrors();
        if (errorMessages.length > 0) {
            log.println(errorMessages[0]);
        } else {
            for (JsonElement result : queryResponse.getResults()) {
                JsonObject caseJsonObject = result.getAsJsonObject();
                logging(caseJsonObject);
                log.println("getCaseByFormattedId DONE!");
                return caseJsonObject;
            }
        }
        log.println("getCaseByObjectId No Result!");
        return null;
    }

    public void createTestCaseResult(JsonObject testCaseJsonObject) throws IOException, URISyntaxException {
        log.println("createTestCaseResult...");

        String testCaseRef = testCaseJsonObject.get("_ref").getAsString();

        QueryRequest userRequest = new QueryRequest("user");
        userRequest.setFetch(new Fetch("UserName", "Subscription", "DisplayName"));
        userRequest.setQueryFilter(new QueryFilter("UserName", "=", "lu.han@technicolor.com"));
        QueryResponse userQueryResponse = restApi.query(userRequest);
        JsonArray userQueryResults = userQueryResponse.getResults();
        JsonElement userQueryElement = userQueryResults.get(0);
        JsonObject userQueryObject = userQueryElement.getAsJsonObject();
        String userRef = userQueryObject.get("_ref").getAsString();

        close();
        getRestApi();

        Date now = new Date();
        String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        JsonObject newResult = new JsonObject();
        newResult.addProperty("Verdict", "Pass");
        newResult.addProperty("Build", "2014.01.08.1234567");
        newResult.addProperty("Tester", userRef);
        newResult.addProperty("Date", format.format(now));
        newResult.addProperty("CreationDate", format.format(now));
        newResult.addProperty("TestCase", testCaseRef);
        newResult.addProperty("Workspace", workspaceRef);

        CreateRequest createRequest = new CreateRequest("testcaseresult", newResult);
        CreateResponse createResponse = restApi.create(createRequest);

        log.println("createTestCaseResult DONE：");
        log.println(String.format("Created %s", createResponse.getObject().get("_ref").getAsString()));
    }

    private void logging(JsonObject caseJsonObject) {
        StringBuilder result = new StringBuilder();
        result.append("\n").append("Test Case FormattedID: ").append(caseJsonObject.get("FormattedID").getAsString());
        result.append("\n").append("Test Case Name: ").append(caseJsonObject.get("Name").getAsString());
        result.append("\n").append("Test Case Type: ").append(caseJsonObject.get("Type").getAsString());
        result.append("\n").append("Test Case URL: ").append(caseJsonObject.get("_ref").getAsString());
        result.append("\n").append("Test Case Creation Time: ").append(caseJsonObject.get("CreationDate").getAsString());
        result.append("\n").append("Test Case LastUpdate Time: ").append(caseJsonObject.get("LastUpdateDate").getAsString());
        result.append("\n").append("Test Case's Project: ").append(caseJsonObject.get("Project").getAsJsonObject().get("_refObjectName").getAsString());
        result.append("\n").append("Test Case's Workspace: ").append(caseJsonObject.get("Workspace").getAsJsonObject().get("_refObjectName").getAsString());
        log.println(result.toString());
    }

    private void printWarningsOrErrors(Response response, StringBuilder result) {
        if (response.wasSuccessful()) {
            result.append("\nSuccess.");
            String[] warningList;
            warningList = response.getWarnings();
            for (int i = 0; i < warningList.length; i++) {
                result.append("\twarning:\n" + warningList[i]);
            }
        } else {
            String[] errorList;
            errorList = response.getErrors();
            if (errorList.length > 0) {
                result.append("\nError.");

            }
            for (int i = 0; i < errorList.length; i++) {
                result.append("\terror:\n" + errorList[i]);
            }
        }
    }
}
