package org.ga4gh.starterkit.refget.app;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RefgetServerConstantsTest {

	@Test
	public void instantiateClass() {
		RefgetServerConstants refgetServerConstants = new RefgetServerConstants();
		Assert.assertEquals(refgetServerConstants.getClass().getSimpleName(), "RefgetServerConstants");
	}

}
