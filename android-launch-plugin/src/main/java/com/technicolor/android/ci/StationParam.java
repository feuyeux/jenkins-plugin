package com.technicolor.android.ci;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 10/31/13
 * Time: 11:09 AM
 */
public class StationParam implements Serializable {
    private String url;
    private String name;

    public StationParam() {
    }

    @DataBoundConstructor
    public StationParam(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url + ":" + name;
    }
}
