package com.technicolor.android.ci.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/19/13
 * Time: 1:29 PM
 */
public class PluginPropertiesUtil {
    private String properties;

    public PluginPropertiesUtil(String properties) throws IOException {
        this.properties = properties;
        File file = new File(properties);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    public boolean store(String key, String value) {
        Properties p = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(properties)) {
            p.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(properties)) {
            p.setProperty(key, value);
            p.store(fileOutputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String retrieve(String key) {
        Properties p = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(properties)) {
            p.load(fileInputStream);
            return p.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
