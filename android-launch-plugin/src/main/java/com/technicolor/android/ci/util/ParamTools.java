package com.technicolor.android.ci.util;

import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;

import java.util.ArrayList;
import java.util.List;

public class ParamTools {
    public static void store(AbstractBuild build, String key, String value) {
        ParametersAction action;
        List<ParameterValue> currentList;
        try {
            action = build.getAction(ParametersAction.class);
            List<ParameterValue> parameterValueList = action.getParameters();
            currentList = new ArrayList<>(parameterValueList);
        } catch (NullPointerException e) {
            action = new ParametersAction();
            currentList = new ArrayList<>();
        }

        ParameterValue sameKeyParam = null;
        for (ParameterValue v : currentList) {
            if (v.getName().equals(key)) {
                sameKeyParam = v;
                break;
            }
        }
        if (sameKeyParam != null) {
            currentList.remove(sameKeyParam);
        }
        currentList.add(new StringParameterValue(key, value));
        build.getActions().remove(action);
        ParametersAction newAction = new ParametersAction(currentList);
        build.getActions().add(newAction);
    }

    public static String retrieve(AbstractBuild build, String key) {
        try {
            ParametersAction action = build.getAction(ParametersAction.class);
            ParameterValue parameter = action.getParameter(key);
            StringParameterValue stringParameterValue = (StringParameterValue) parameter;
            return stringParameterValue.value;
        } catch (NullPointerException e) {
            return null;
        }
    }
}
