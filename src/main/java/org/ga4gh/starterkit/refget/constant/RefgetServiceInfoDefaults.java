package org.ga4gh.starterkit.refget.constant;

import java.util.Arrays;
import java.util.List;

public class RefgetServiceInfoDefaults {

    public static final boolean CIRCULAR_SUPPORTED = RefgetApiConstants.CIRCULAR_SUPPORTED;

    public static final List<String> ALGORITHMS = Arrays.asList("md5"); //TODO: trunc512 support

    public static final int SUBSEQUENCE_LIMIT = RefgetApiConstants.SUBSEQUENCE_LIMIT;

    public static final List<String> SUPPORTED_API_VERSIONS = Arrays.asList("1.0");

}
