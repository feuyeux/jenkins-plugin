package com.technicolor.android.ci;

import com.technicolor.android.ci.util.PluginPropertiesUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/19/13
 * Time: 1:29 PM
 */
public class TestPropertiesUtil {

    @Test
    public void testStore() throws IOException {
        PluginPropertiesUtil util = new PluginPropertiesUtil("here.properties");
        util.store(" a", "c=c=c=c  =");
        util.store("b", "2");
        String a1 = util.retrieve(" a");
        Assert.assertEquals("c=c=c=c  =", a1);
        System.out.println(a1);
        String b1 = util.retrieve("b");
        Assert.assertEquals("2", b1);
        System.out.println(b1);
    }
}
