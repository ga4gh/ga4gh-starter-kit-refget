package org.ga4gh.starterkit.refget.app;

public class RefgetServerConstants {
    /* Spring bean names - Refget config container */

    /**
     * Spring bean qualifier for an empty refget config container
     */
    public static final String EMPTY_REFGET_CONFIG_CONTAINER = "emptyRefgetConfigContainer";

    /**
     * Spring bean qualifier for the refget config container containing all defaults
     */
    public static final String DEFAULT_REFGET_CONFIG_CONTAINER = "defaultRefgetConfigContainer";

    /**
     * Spring bean qualifier for the refget config container containing user-loaded properties
     */
    public static final String USER_REFGET_CONFIG_CONTAINER = "userRefgetConfigContainer";

    /**
     * Spring bean qualifier for the final refget config container containing merged
     * properties from default and user-loaded (user-loaded properties override
     * defaults)
     */
    public static final String FINAL_REFGET_CONFIG_CONTAINER = "finalRefgetConfigContainer";

    /* Spring bean scope */

    /**
     * Indicates Spring bean has 'prototype' lifecycle
     */
    public static final String PROTOTYPE = "prototype";
}