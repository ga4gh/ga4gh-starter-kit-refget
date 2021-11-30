package org.ga4gh.starterkit.refget.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import static org.ga4gh.starterkit.refget.constant.RefgetServiceInfoDefaults.CIRCULAR_SUPPORTED;
import static org.ga4gh.starterkit.refget.constant.RefgetServiceInfoDefaults.ALGORITHMS;
import static org.ga4gh.starterkit.refget.constant.RefgetServiceInfoDefaults.SUBSEQUENCE_LIMIT;
import static org.ga4gh.starterkit.refget.constant.RefgetServiceInfoDefaults.SUPPORTED_API_VERSIONS;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class RefgetServiceInfo {

    private RefgetService service;

    /* Constructors */

    public RefgetServiceInfo() {
        service = new RefgetService();
        setAllDefaults();
    }

    /* Setters and Getters */

    public RefgetService getService() {
        return service;
    }

    public void setService(RefgetService service) {
        this.service = service;
    }

    public void setAllDefaults(){
        getService().setCircular_supported(CIRCULAR_SUPPORTED);
        getService().setAlgorithms(ALGORITHMS);
        getService().setSubsequence_limit(SUBSEQUENCE_LIMIT);
        getService().setSupported_api_version(SUPPORTED_API_VERSIONS);
    }
}

