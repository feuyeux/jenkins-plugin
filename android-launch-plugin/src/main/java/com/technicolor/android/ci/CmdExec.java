package com.technicolor.android.ci;

import hudson.model.BuildListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: lizs
 * Date: 11/21/13
 * Time: 3:33 PM
 */
public class CmdExec {
    public static String exec(BuildListener listener, String cmd) {
        String result = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                listener.getLogger().println(e.getStackTrace());
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            BufferedReader info = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            int s;
            while ((s = info.read()) != -1) {
                buffer.append((char) s);
            }

            BufferedReader errors = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            int es;
            while ((es = errors.read()) != -1) {
                buffer.append((char) es);
            }
            result = buffer.toString();
        } catch (Exception e) {
            result = e.getMessage();
            listener.getLogger().println(e.getStackTrace());
            e.printStackTrace();
        }
        return result;
    }

    public static String executeAndGetLastLine(BuildListener listener, String cmd) {
        String result = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                listener.getLogger().println(e.getStackTrace());
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            int s;

            String line = null;
            while (read.ready()) {
                line = read.readLine();
            }
            if (line == null) {
                return "NONE";
            }
            return line;
        } catch (Exception e) {
            e.printStackTrace();
            listener.getLogger().println(e.getStackTrace());
            result = "";
        }
        return result;
    }
}
