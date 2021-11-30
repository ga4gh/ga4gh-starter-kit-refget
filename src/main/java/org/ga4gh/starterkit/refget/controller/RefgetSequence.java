package org.ga4gh.starterkit.refget.controller;

import org.apache.commons.lang3.StringUtils;
import org.ga4gh.starterkit.common.exception.BadRequestException;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.util.logging.LoggingUtil;
import org.ga4gh.starterkit.refget.exception.RefgetNotAcceptableException;
import org.ga4gh.starterkit.refget.exception.RefgetNotImplemented;
import org.ga4gh.starterkit.refget.exception.RefgetRangeNotSatifiable;
import org.ga4gh.starterkit.refget.model.RefgetData;
import org.ga4gh.starterkit.refget.utils.hibernate.RefgetHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.ga4gh.starterkit.refget.constant.RefgetApiConstants.CIRCULAR_SUPPORTED;
import static org.ga4gh.starterkit.refget.constant.RefgetApiConstants.REFGET_API_V1;
import static org.ga4gh.starterkit.refget.constant.RefgetApiConstants.SUBSEQUENCE_LIMIT;
import static org.ga4gh.starterkit.refget.constant.RefgetApiConstants.DEFAULT_CONTENT_TYPE;

@RestController
public class RefgetSequence {

    @Autowired
    private RefgetHibernateUtil hibernateUtil;

    @Autowired
    private LoggingUtil loggingUtil;

    @GetMapping(REFGET_API_V1 + "/sequence/{id}")
    public ResponseEntity<String> getSequence(@PathVariable String id,
                                              @RequestHeader(value = "Accept", required = false) final String acceptHeader,
                                              @RequestHeader(value = "Range", required = false) final String rangeHeader,
                                              @RequestParam(value = "start", required = false) Integer start,
                                              @RequestParam(value = "end", required = false) Integer end
                              ) throws URISyntaxException, IOException, InterruptedException {

        loggingUtil.debug("Public API request: get sequence");
        loggingUtil.trace(String.format("Accept: %s, Range: %s, start: %d, end: %d",acceptHeader,rangeHeader,start,end));

        if (!acceptHeader.equals(DEFAULT_CONTENT_TYPE) && !acceptHeader.equals("*/*")) {

            // The server SHOULD respond with an Not Acceptable error if the client requests a format not supported by the server.
            throw new RefgetNotAcceptableException("Invalid Accept Header: " + acceptHeader);
        }
        RefgetData refgetDataObject = hibernateUtil.readEntityObject(RefgetData.class,id,false);
        if (refgetDataObject == null) {

            // If the identifier is not known by the server, a 404 status code and NotFound error shall be returned.
            throw new ResourceNotFoundException("No RefgetObject found by id: " + id);
        }
        String refgetSequenceOutput = refgetDataObject.getSequence();
        if (refgetSequenceOutput.startsWith("http://") ||refgetSequenceOutput.startsWith("https://")) {

            // TODO: 303 redirecting the client to where it can retrieve the sequence.
            //  ho In this case
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(refgetSequenceOutput))
                    .headers("Content-Type", "text/plain;charset=UTF-8")
                    .timeout(Duration.of(10, SECONDS))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {

                // If the identifier is not known by the server, a 404 status code and NotFound error shall be returned.
                //TODO: Add response to exception message or just log?
                throw new ResourceNotFoundException("No RefgetObject found by id: " +id+ " at the provided storage URL. Error: " + response.body());
            }
            refgetSequenceOutput = response.body();
        }
        final boolean refgetSequenceIsCircular = refgetDataObject.getIscircular();
        final Integer refgetSequenceOutputLength = refgetDataObject.getLength();
        final Pattern rangeHeaderPattern = Pattern.compile("bytes=[0-9]*-[0-9]*$", Pattern.CASE_INSENSITIVE);
        if (!(rangeHeader==null)) {

            // An origin server MUST ignore a Range header field that contains a
            // range unit it does not understand
            if (!(start == null) || !(end == null)) {

                // The server MUST respond with a Bad Request error if both a Range header and start or end query parameters are specified.
                throw new BadRequestException("Both Range header and start or end query parameters are specified");
            }
            Matcher matcher = rangeHeaderPattern.matcher(rangeHeader);
            if ((matcher.find())) {

                final String[] rangeBytes = rangeHeader.split("-");
                final Integer fbs = Integer.valueOf(StringUtils.substringBetween(rangeHeader, "=", "-"));
                final Integer lbs = Integer.valueOf(rangeBytes[1]);

                if ((fbs > refgetSequenceOutputLength - 1) || (lbs > refgetSequenceOutputLength - 1) || (fbs > lbs)) {
                    // lbs<=fbs : Sub-sequences of circular chromosomes across the origin may not be requested via the Range header.
                    // The server MUST respond with a Bad Request error if one or more ranges are out of bounds of the sequence.
                    throw new RefgetRangeNotSatifiable("Range header is out of bounds");
                }

                else if(lbs-fbs+1 > SUBSEQUENCE_LIMIT) {

                    // A server may place a length limit on sub-sequences returned via query parameter,
                    // queries exceeding this limit shall return Range Not Satisfiable.
                    throw new RefgetRangeNotSatifiable("The output sub-sequence length is greater than the sub-sequence limit of the server");
                }

                // A server SHOULD return a 206 status code if a Range header was specified and the request was successful.
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(refgetSequenceOutput.substring(fbs, lbs + 1)); //check that it doesn't go beyond subsequence limit
            }
        }
        if (!(start == null) || !(end == null)) {
            if ((start == null)) {
                start = 0 ;
            }
            if ((end == null)) {
                end = refgetSequenceOutputLength ;
            }
            if ((start < 0) || (end < 0)) {
                throw new RefgetRangeNotSatifiable("One or both of start and end parameters are less than 0");
            }
            if ((start >= refgetSequenceOutputLength) || (end > refgetSequenceOutputLength)) {

                // The server MUST respond with a Bad Request error if start is specified and is larger than the total sequence length.
                throw new RefgetRangeNotSatifiable("One or both of start and end parameters are greater than the length of the reference sequence");
            }

            // TODO: If a start and/or end query parameter are specified the server should include a Accept-Ranges: none header in the response.
            else if (start > end) {
                if (CIRCULAR_SUPPORTED == false) {

                    // if the server does not support circular chromosomes it MUST respond with Not Implemented if the start is greater than the end.
                    throw new RefgetNotImplemented("The server does not support circular sequences");
                }
                else {
                    if (refgetSequenceIsCircular == false) {

                        // The server MUST respond with a Range Not Satisfiable error if start and end are specified and start is greater than end and the sequence is not a circular chromosome.
                        throw new RefgetRangeNotSatifiable("The start parameter is greater than the end parameter and the reference sequence is not circular");
                    }
                    else {
                        String refgetOutputSubsequence = refgetSequenceOutput.substring(start,refgetSequenceOutputLength) + refgetSequenceOutput.substring(0,end);
                        if (refgetOutputSubsequence.length()>SUBSEQUENCE_LIMIT) {
                            throw new RefgetRangeNotSatifiable("The output sub-sequence length is greater than the server's sub-sequence limit");
                        }
                        else {
                            return ResponseEntity.status(HttpStatus.OK).body(refgetOutputSubsequence);
                        }
                    }
                }
            }
            else if(end-start > SUBSEQUENCE_LIMIT) {

                // A server may place a length limit on sub-sequences returned via query parameter,
                // queries exceeding this limit shall return Range Not Satisfiable.
                throw new RefgetRangeNotSatifiable("The output sequence length is greater than the subsequence limit of the server");
            }
            return ResponseEntity.status(HttpStatus.OK).body(refgetSequenceOutput.substring(start,end));
        }

        // TODO:
        //  logging -> how to set the log? which file do they write to? Is there a stream handler?
        //  add logging bean to configure
        return ResponseEntity.status(HttpStatus.OK).body(refgetSequenceOutput);
    }
}