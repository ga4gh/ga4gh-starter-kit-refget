package org.ga4gh.starterkit.refget.controller;

import org.ga4gh.starterkit.common.util.logging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.ga4gh.starterkit.refget.exception.RefgetNotAcceptableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static org.ga4gh.starterkit.refget.constant.RefgetApiConstants.DEFAULT_CONTENT_TYPE;
import static org.ga4gh.starterkit.refget.constant.RefgetApiConstants.REFGET_API_V1;

@RestController
public class RefgetServiceInfo {

    @Autowired
    private org.ga4gh.starterkit.refget.model.RefgetServiceInfo refgetServiceInfo;

    @Autowired
    private LoggingUtil loggingUtil;

    @GetMapping(REFGET_API_V1 + "/service-info")
    public org.ga4gh.starterkit.refget.model.RefgetServiceInfo getServiceInfo(@RequestHeader(value = "Accept", required = false) String acceptHeader) {
        loggingUtil.debug("Public API request: get service-info");
        loggingUtil.trace(String.format("Accept: %s",acceptHeader));
        if (!acceptHeader.equals(DEFAULT_CONTENT_TYPE) && !acceptHeader.equals("*/*")){
            throw new RefgetNotAcceptableException("Invalid Accept Header: " + acceptHeader);
        }
        return refgetServiceInfo;
    }

}