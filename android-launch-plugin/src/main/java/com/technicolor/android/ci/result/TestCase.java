package com.technicolor.android.ci.result;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/13/13
 * Time: 3:17 PM
 */

@XmlRootElement
public class TestCase implements Serializable {
    private String name;
    private String failure;

    public TestCase(String name) {
        this.name = name;
    }

    public TestCase() {
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }
}
