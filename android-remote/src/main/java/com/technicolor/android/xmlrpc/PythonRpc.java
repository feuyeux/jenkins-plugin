package com.technicolor.android.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcHttpTransportException;

import java.net.MalformedURLException;
import java.net.URL;

public class PythonRpc {

    public static final String TEST_STATION_RPC = "http://localhost:9527/RPC2";
    public static final String TEST_STATION_RPC1 = "http://10.11.72.61:9527/RPC2";

    public static void main(String[] args) throws MalformedURLException, XmlRpcHttpTransportException {
        System.out.println(testTestStation(""));
    }

    public static boolean isAlive(String stationIp) {
        return true;
    }

    public static Object getStatus(String stationIp) {
        return new Object();
    }

    public static String launch(String build_file_path) throws MalformedURLException {
        String result = null;

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(TEST_STATION_RPC));

        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        Object[] params = new Object[]{2013, 11};
        try {
            result = (String) client.execute("getMonth", params);
            result += (String) client.execute("hello", new Object[]{});
        } catch (XmlRpcException e11) {
            e11.printStackTrace();
        }
        System.out.println(build_file_path);
        return result;
    }


    public static String testTestStation(String build_file_path) throws MalformedURLException {
        String result = null;

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(TEST_STATION_RPC1));

        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);

        Object[] params = new Object[]{};
        try {
            result = (String) client.execute("getTestId", params);
            result += (String) client.execute("hello", new Object[]{});
        } catch (XmlRpcException e11) {
            e11.printStackTrace();
        }
        System.out.println(build_file_path);
        return result;
    }
}