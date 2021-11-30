package org.ga4gh.starterkit.refget.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.util.logging.LoggingUtil;
import org.ga4gh.starterkit.refget.exception.RefgetNotAcceptableException;
import org.ga4gh.starterkit.refget.model.RefgetData;
import org.ga4gh.starterkit.refget.utils.hibernate.RefgetHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import static org.ga4gh.starterkit.refget.constant.RefgetApiConstants.REFGET_API_V1;
import static org.ga4gh.starterkit.refget.constant.RefgetApiConstants.DEFAULT_CONTENT_TYPE;

@RestController
public class RefgetMetadata {

    @Autowired
    private RefgetHibernateUtil hibernateUtil;

    @Autowired
    private LoggingUtil loggingUtil;

    @GetMapping(REFGET_API_V1 + "/sequence/{id}/metadata")
    public MappingJacksonValue getMetadata(@PathVariable String id, @RequestHeader(value = "Accept", required = false) String acceptHeader) {
        loggingUtil.debug("Public API request: GET metadata for sequence with id: "+ id);
        loggingUtil.trace(String.format("Accept: %s",acceptHeader));
        if (!acceptHeader.equals(DEFAULT_CONTENT_TYPE) && !acceptHeader.equals("*/*")){
            throw new RefgetNotAcceptableException("Invalid Accept Header: " + acceptHeader);
        }
        RefgetData refgetDataObject = hibernateUtil.readEntityObject(RefgetData.class,id,false);
        if (refgetDataObject == null) {
            throw new ResourceNotFoundException("No RefgetObject found by id: " + id);
        }

        MappingJacksonValue mapping = new MappingJacksonValue(refgetDataObject);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("id","md5","trunc512","length","aliases");
        FilterProvider filters = new SimpleFilterProvider().addFilter("refgetDataFilter", filter);
        mapping.setFilters(filters);
        return mapping;
    }

}
