package com.technicolor.android.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/5/13
 * Time: 10:57 AM
 */
public class TestlinkXMLRPCClient {
    public static final String DEV_KEY_PARAM = "devKey";
    public static final String TEST_CASE_ID_PARAM = "testcaseid";
    public static final String TEST_SUITE_ID_PARAM = "testsuiteid";
    public static final String TEST_CASE_EXTERNAL_ID__PARAM = "testcaseexternalid";
    public static final String GET_TEST_CASE_METHOD = "tl.getTestCase";
    public static final String GET_TEST_SUITE_BY_ID_METHOD = "tl.getTestSuiteByID";
    public static final String NAME_KEY = "name";
    public static final String SUMMARY_KEY = "summary";
    public static final String PARENT_ID_KEY = "parent_id";
    public static final String TEST_SUITE_ID_KEY = "testsuite_id";
    private static final String DEV_KEY_VALUE = "7949d67e6341e13f819a98563f718b73";
    private static final String SERVER_URL = "http://10.11.60.10/testlink/lib/api/xmlrpc.php";

    public static void main(String[] args) {
        for (int i = 931; i <=943 ; i++) {
            String testCaseExtraId="t-"+i;
            String[] getTestScript=getTestScript(testCaseExtraId);
            System.out.println(getTestScript[0]);
        }
    }

    /**
     * get test script by test case id
     *
     * @param testCaseId
     * @return test script and test case summary
     */
    public static String[] getTestScript(int testCaseId) {
        try {
            XmlRpcClient rpcClient = buildClient();
            ArrayList<Object> params = new ArrayList<Object>();
            Hashtable<String, Object> executionData = new Hashtable<String, Object>();
            executionData.put(DEV_KEY_PARAM, DEV_KEY_VALUE);
            executionData.put(TEST_CASE_ID_PARAM, testCaseId);
            params.add(executionData);
            Object[] result = (Object[]) rpcClient.execute(GET_TEST_CASE_METHOD, params);
            for (int i = 0; i < result.length; i++) {
                Map<String, String> testCaseMap = (Map<String, String>) result[i];
                String caseName = testCaseMap.get(NAME_KEY);
                String summary = testCaseMap.get(SUMMARY_KEY);
                String suiteId = (String) testCaseMap.get(TEST_SUITE_ID_KEY);
                String fullPath = testCasePath(suiteId, rpcClient);
                String casePath = fullPath + "/" + caseName;
                return new String[]{casePath, summary};
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getTestScript(String testCaseExtraId) {
        try {
            XmlRpcClient rpcClient = buildClient();
            ArrayList<Object> params = new ArrayList<Object>();
            Hashtable<String, Object> executionData = new Hashtable<String, Object>();
            executionData.put(DEV_KEY_PARAM, DEV_KEY_VALUE);
            executionData.put(TEST_CASE_EXTERNAL_ID__PARAM, testCaseExtraId);
            params.add(executionData);
            Object[] result = (Object[]) rpcClient.execute(GET_TEST_CASE_METHOD, params);
            for (int i = 0; i < result.length; i++) {
                Map<String, String> testCaseMap = (Map<String, String>) result[i];
                String caseName = testCaseMap.get(NAME_KEY);
                String summary = testCaseMap.get(SUMMARY_KEY);
                String suiteId = (String) testCaseMap.get(TEST_SUITE_ID_KEY);
                String fullPath = testCasePath(suiteId, rpcClient);
                String casePath = fullPath + "/" + caseName;
                return new String[]{casePath, summary};
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String testCasePath(String suiteId, XmlRpcClient rpcClient) throws XmlRpcException {
        ArrayList<Object> suiteParam = new ArrayList<Object>();
        Hashtable<String, Object> suiteData = new Hashtable<String, Object>();
        suiteData.put(DEV_KEY_PARAM, DEV_KEY_VALUE);
        suiteData.put(TEST_SUITE_ID_PARAM, suiteId);
        suiteParam.add(suiteData);
        Object response = rpcClient.execute(GET_TEST_SUITE_BY_ID_METHOD, suiteParam);
        try {
            Map<String, String> suiteResult = (Map<String, String>) response;
            String parent_id = suiteResult.get(PARENT_ID_KEY);
            String name = suiteResult.get(NAME_KEY);
            if (suiteId != null) {
                String result = testCasePath(parent_id, rpcClient) + "/" + name;
                return result;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static XmlRpcClient buildClient() throws MalformedURLException {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(SERVER_URL));
        XmlRpcClient rpcClient = new XmlRpcClient();
        rpcClient.setConfig(config);
        return rpcClient;
    }
}