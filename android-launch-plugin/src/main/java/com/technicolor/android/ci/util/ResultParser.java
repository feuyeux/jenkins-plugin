package com.technicolor.android.ci.util;

import com.technicolor.android.ci.result.TestCase;
import com.technicolor.android.ci.result.TestSuite;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/13/13
 * Time: 2:46 PM
 */
public class ResultParser {
    TestSuite suite = null;
    private boolean buildSuccess=true;

    public boolean invoke(String[] args) {
        final String PASS = "PASS";
        List<TestCase> caseQueue = new ArrayList<>();
        try {
            Process proc = Runtime.getRuntime().exec(args[0]);
            BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (read.ready()) {
                String line = read.readLine();
                if (!line.isEmpty()) {
                    if (!matchTest(caseQueue, line)) {
                        if (!matchResult(PASS, caseQueue, line)) {
                            if (!matchTotal(suite, line)) {
                                if (!matchFailures(suite, line)) {
                                    matchSkip(suite, line);
                                }
                            }
                        }
                    }
                }
            }

            suite.setTestCaseList(caseQueue);
            JAXBContext jc = JAXBContext.newInstance(TestSuite.class);
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(suite, new File(args[1]));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return buildSuccess;
    }

    private  boolean matchSkip(TestSuite suite, String line) {
        String skippedReg = "NotRun : (\\d+)";
        Pattern pattern = Pattern.compile(skippedReg);
        Matcher matcher = pattern.matcher(line);
        boolean result = matcher.find();
        if (result) {
            String skip = matcher.group(1);
            suite.setSkipped(Integer.valueOf(skip));
        }
        return result;
    }

    private  boolean matchTotal(TestSuite suite, String line) {
        String testsReg = "Total test cases : (\\d+)";
        Pattern pattern = Pattern.compile(testsReg);
        Matcher matcher = pattern.matcher(line);
        boolean result = matcher.find();
        if (result) {
            String total = matcher.group(1);
            Integer tests = Integer.valueOf(total);
            suite.setTests(tests);
        }
        return result;
    }

    private  boolean matchFailures(TestSuite suite, String line) {
        String failuresReg = "Failure : (\\d+)";
        Pattern pattern = Pattern.compile(failuresReg);
        Matcher matcher = pattern.matcher(line);
        boolean result = matcher.find();
        if (result) {
            String failures = matcher.group(1);
            Integer fail = Integer.valueOf(failures);
            suite.setFailures(fail);
        }
        return result;
    }

    private  boolean matchResult(String PASS, List<TestCase> caseQueue, String line) {
        String resultReg = "result=(\\w+)";
        Pattern pattern = Pattern.compile(resultReg);
        Matcher matcher = pattern.matcher(line);
        boolean result = matcher.find();
        if (result) {
            String testResult = matcher.group(1);
            boolean failed = !PASS.equals(testResult);
            if (failed) {
                buildSuccess=false;
                caseQueue.get(caseQueue.size() - 1).setFailure("");
            }
        }
        return result;
    }

    private  boolean matchTest(List<TestCase> caseQueue, String line) {
        String testReg = "(\\w+)#(\\w+)";
        Pattern pattern = Pattern.compile(testReg);
        Matcher matcher = pattern.matcher(line);
        boolean result = matcher.find();
        if (result) {
            String suiteName = matcher.group(1);
            String caseName = matcher.group(2);
            if (suite == null) {
                suite = new TestSuite(suiteName);
            }
            TestCase testCase = new TestCase(caseName);
            caseQueue.add(testCase);
        }
        return result;
    }

    public boolean isBuildSuccess() {
        return buildSuccess;
    }

    public void setBuildSuccess(boolean buildSuccess) {
        this.buildSuccess = buildSuccess;
    }
}
