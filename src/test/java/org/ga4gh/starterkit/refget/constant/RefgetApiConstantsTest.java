package org.ga4gh.starterkit.refget.constant;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RefgetApiConstantsTest {

    @Test
    public void instantiateClass() {
        RefgetApiConstants refgetApiConstants = new RefgetApiConstants();
        Assert.assertEquals(refgetApiConstants.getClass().getSimpleName(), "RefgetApiConstants");
    }
}
