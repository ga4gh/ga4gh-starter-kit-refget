package org.ga4gh.starterkit.refget.constant;

import org.testng.*;
import org.testng.annotations.*;

public class RefgetServiceInfoDefaultsTest {

    @Test
    public void instantiateClass() {
        RefgetServiceInfoDefaults refgetServiceInfoDefaults = new RefgetServiceInfoDefaults();
        Assert.assertEquals(refgetServiceInfoDefaults.getClass().getSimpleName(), "RefgetServiceInfoDefaults");
    }
}
