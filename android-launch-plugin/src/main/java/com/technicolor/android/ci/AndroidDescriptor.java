package com.technicolor.android.ci;

import hudson.CopyOnWrite;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/4/13
 * Time: 3:12 PM
 */
public class AndroidDescriptor extends BuildStepDescriptor<Builder> {
    public static final String PLUG_IN_NAME = "Tablet Android Choose Station";
    @CopyOnWrite
    private volatile StationParam[] stations = new StationParam[0];
    @CopyOnWrite
    private volatile String mappingFile = "";
    public AndroidDescriptor() {
        super(AndroidBuilder.class);
        load();
    }

    @Override
    public String getDisplayName() {
        return PLUG_IN_NAME;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws Descriptor.FormException {
        this.stations = req.bindParametersToList(StationParam.class, "Station.").toArray(new StationParam[0]);
        this.mappingFile = formData.getString("mappingFile");
        save();
        return true;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return Boolean.TRUE;
    }

    public String getMappingFile() {
        return mappingFile;
    }

    public StationParam[] getStations() {
        return stations;
    }

    public FormValidation doCheckMandatory(@QueryParameter String value) {
        FormValidation returnValue = FormValidation.ok();
        if (StringUtils.isBlank(value)) {
            returnValue = FormValidation.error("Error: it's shouldn't be empty.");
        }
        return returnValue;
    }
}
