package org.ga4gh.starterkit.refget.constant;

import static org.ga4gh.starterkit.common.constant.StarterKitConstants.ADMIN;
import static org.ga4gh.starterkit.common.constant.StarterKitConstants.GA4GH;
import static org.ga4gh.starterkit.common.constant.StarterKitConstants.REFGET;
import static org.ga4gh.starterkit.common.constant.StarterKitConstants.V1;

/**
 * Refget API URL path/routing constants
 */
public class RefgetApiConstants {

    /**
     * Common REST API route to most (if not all) Refget-related controller functions
     */
    public static final String REFGET_API_V1 = "/" + GA4GH + "/" + REFGET + "/" + V1;

    /**
     * Common REST API route to most (if not all) off-spec, administrative controller
     * functions for modifying Refget-related entities
     */
    public static final String ADMIN_REFGET_API_V1 = "/" + ADMIN + REFGET_API_V1;

    //TODO: Incorporate TRUNC512 as an option while reading, writing, updating sequence and it's meta data
    public static final boolean TRUNC512 = false;

    public static final boolean CIRCULAR_SUPPORTED = true;

    public static final int SUBSEQUENCE_LIMIT = 4000000;

    public static final String DEFAULT_CONTENT_TYPE = "application/vnd.ga4gh.refget.v1.0.0+json";

    public static final String DEFAULT_SEQUENCE_CONTENT_TYPE = "text/vnd.ga4gh.refget.v1.0.0+plain";

}