package com.technicolor.android.ci;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

public class RallyDevDescriptor extends BuildStepDescriptor<Builder> {
    public static final String PLUG_IN_NAME = "Tablet Android RallyDev REST Access";
    private String userName;
    private String password;
    private String proxyURL;
    private String proxyUser;
    private String proxyPassword;

    public RallyDevDescriptor() {
        super(RallyDevBuilder.class);
        load();
    }

    @Override
    public String getDisplayName() {
        return PLUG_IN_NAME;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        this.userName = formData.getString("userName");
        this.password = formData.getString("password");
        this.proxyURL = formData.getString("proxyURL");
        this.proxyUser = formData.getString("proxyUser");
        this.proxyPassword = formData.getString("proxyPassword");
        save();
        return true;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return Boolean.TRUE;
    }

    public String getUserName() {
        return userName;
    }
    public String getPassword() {
        return password;
    }
    public String getProxyURL() {
        return proxyURL;
    }
    public String getProxyUser() {
        return proxyUser;
    }
    public String getProxyPassword() {
        return proxyPassword;
    }
}
