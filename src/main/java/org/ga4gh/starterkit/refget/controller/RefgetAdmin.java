package org.ga4gh.starterkit.refget.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.ga4gh.starterkit.common.exception.ConflictException;
import org.ga4gh.starterkit.common.exception.ResourceNotFoundException;
import org.ga4gh.starterkit.common.hibernate.exception.EntityDoesntExistException;
import org.ga4gh.starterkit.common.hibernate.exception.EntityExistsException;
import org.ga4gh.starterkit.common.hibernate.exception.EntityMismatchException;
import org.ga4gh.starterkit.common.util.logging.LoggingUtil;
import org.ga4gh.starterkit.refget.exception.*;
import org.ga4gh.starterkit.refget.model.Aliases;
import org.ga4gh.starterkit.refget.model.RefgetData;
import org.ga4gh.starterkit.refget.model.RefgetInputSequence;
import org.ga4gh.starterkit.refget.utils.hibernate.RefgetHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Arrays;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.ga4gh.starterkit.refget.constant.RefgetApiConstants.ADMIN_REFGET_API_V1;

@RestController
public class RefgetAdmin {

    @Autowired
    private RefgetHibernateUtil hibernateUtil;

    @Autowired
    private LoggingUtil loggingUtil;

    //TODO:
    // 1. Do we need an admin endpoint to list all available refget sequence ids?
    // 2. Extensive logging?

    public String getMd5(final String sequence) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // add try catch -> no such algorithm
        final MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigestBytes = md.digest(sequence.getBytes("UTF-8"));
        StringBuffer md5Buffer = new StringBuffer();
        for (int i = 0; i < messageDigestBytes.length; ++i) {
            md5Buffer.append(Integer.toHexString((messageDigestBytes[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return md5Buffer.toString();
    }

    public String getTrunc512(final String sequence) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // SHA-512 digest of a sanitised sequence
        // A hex-encoding of the first 24 bytes of that digest resulting in a 48 character string
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] messageDigestBytes = md.digest(sequence.getBytes("UTF-8"));
        byte[] messageDigest24Bytes = Arrays.copyOfRange(messageDigestBytes, 0, 24);
        String trunc512Checksum = new BigInteger(1, messageDigest24Bytes).toString(16);
        while (trunc512Checksum.length() < 48) {
            trunc512Checksum = "0" + trunc512Checksum;
        }
        return trunc512Checksum;
    }

    public String getCleanedSequence(final String inputSequence) throws URISyntaxException, IOException, InterruptedException {
        String rawSequence;
        if (inputSequence.startsWith("http://") || inputSequence.startsWith("https://")) {
            // TODO: 303 redirecting the client to where it can retrieve the sequence.
            //  ho In this case
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = null;

            request = HttpRequest.newBuilder()
                    .uri(new URI(inputSequence))
                    .headers("Content-Type", "text/plain;charset=UTF-8")
                    .timeout(Duration.of(10, SECONDS))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                //TODO: Add response to exception message or just log?
                throw new ResourceNotFoundException("No Sequence found by at the provided storage URL. " +
                        "Error: " + response.body());
            }
            rawSequence = response.body(); //TODO: use a different variable?
        } else {
            rawSequence = inputSequence;
        }
        final String cleanedInputSequence = rawSequence
                .replaceAll(">.*?\\\n", "") // TODO: better matching considering
                // the IUPAC ambiguity codes
                // $text = preg_replace( '/(\r\n)+|\r+|\n+|\t+/', ' ', $text )
                .replaceAll("[^aAtTgGcCnN]", "")
                .toUpperCase();
        return cleanedInputSequence;
    }

    private MappingJacksonValue loadFormattedRefgetObject(String id) {
        RefgetData refgetDataObject = hibernateUtil.readEntityObject(
                RefgetData.class,
                id,
                false);
        if (refgetDataObject == null) {
            throw new ResourceNotFoundException("No RefgetObject found by id: " + id);
        }
        MappingJacksonValue mapping = new MappingJacksonValue(refgetDataObject);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(
                "id",
                "md5",
                "trunc512",
                "length",
                "aliases");
        FilterProvider filters = new SimpleFilterProvider().addFilter("refgetDataFilter", filter);
        mapping.setFilters(filters);
        return mapping;
    }

    // TODO: required - get sequence or storage url, aliases and is_circular from end user
    // insert into sequence table. if fails, return the message with error code
    // return only md5 id and trunc512 id
    @PostMapping(ADMIN_REFGET_API_V1 + "/sequence")
    public MappingJacksonValue createRefgetObject(
            @Valid @RequestBody RefgetInputSequence refgetSequenceObject
    ) {
        loggingUtil.debug("Admin API request: Post sequence");
        loggingUtil.trace(String.format("New Sequence Object: %s",refgetSequenceObject.toString()));
        try {
            String inputSequence = refgetSequenceObject.getSequence();
            String cleanedInputSequence = getCleanedSequence(inputSequence);
            final String md5Checksum = getMd5(cleanedInputSequence);
            final String trunc512Checksum = getTrunc512(cleanedInputSequence);
            final int sequenceLength = cleanedInputSequence.length();

            RefgetData refgetObject = new RefgetData(md5Checksum, md5Checksum, trunc512Checksum, sequenceLength, cleanedInputSequence, refgetSequenceObject.getIscircular());
            for (Aliases alias : refgetSequenceObject.getAliases()) {
                alias.setRefgetData(refgetObject);
            }
            refgetObject.setAliases(refgetSequenceObject.getAliases());
            hibernateUtil.createEntityObject(RefgetData.class, refgetObject);
            MappingJacksonValue mapping = loadFormattedRefgetObject(refgetObject.getId());
            return mapping;
        } catch (EntityExistsException ex) {
            throw new ConflictException(ex.getMessage());
        } catch (Exception ex) {
            //TODO: Which exception should be thrown when it fails to clean, generate md5, trunc512 ids
            throw new RefgetNotAcceptableException(ex.getMessage());
        }
    }

    @PutMapping(ADMIN_REFGET_API_V1 + "/sequence/{id}")
    public MappingJacksonValue updateRefgetObject(
            @PathVariable String id,
            @Valid @RequestBody RefgetInputSequence refgetSequenceObject
    ) {
        loggingUtil.debug(String.format("Admin API request: Update sequence with id: %s", id));
        loggingUtil.trace(String.format("New Sequence Object: %s",refgetSequenceObject.toString()));
        try {
            String inputSequence = refgetSequenceObject.getSequence();
            String cleanedInputSequence = getCleanedSequence(inputSequence);
            final String newMd5Checksum = getMd5(cleanedInputSequence);

            // if id is not present in db -> throw error - entity does not exist
            //check if id == newMd5Checksum -> update the object(id)
            //if id != newMd5Checksum -> post the new object and delete old object

            RefgetData refgetDataObject = hibernateUtil.readEntityObject(RefgetData.class, id, false);
            if (refgetDataObject == null) {
                throw new ResourceNotFoundException("No RefgetObject found by id: " + id);
            }
            final String newTrunc512Checksum = getTrunc512(cleanedInputSequence);
            final int newSequenceLength = cleanedInputSequence.length();
            RefgetData refgetObject = new RefgetData(
                    newMd5Checksum,
                    newMd5Checksum,
                    newTrunc512Checksum,
                    newSequenceLength,
                    cleanedInputSequence,
                    refgetSequenceObject.getIscircular());
            for (Aliases alias : refgetSequenceObject.getAliases()) {
                alias.setRefgetData(refgetObject);
            }
            refgetObject.setAliases(refgetSequenceObject.getAliases());
            if (id.equals(newMd5Checksum)) {
                hibernateUtil.updateEntityObject(RefgetData.class, id, refgetObject);
            } else {
                hibernateUtil.createEntityObject(RefgetData.class, refgetObject);
                hibernateUtil.deleteEntityObject(RefgetData.class, id);
                //TODO: If delete fails, should we try to delete the created entity
                // and then throw an error saying Failed to update. i.e,
                // should the 2 commands be treated as a single transaction?
            }
            MappingJacksonValue mapping = loadFormattedRefgetObject(refgetObject.getId());
            return mapping;
        } catch (EntityExistsException |
                EntityMismatchException |
                EntityDoesntExistException |
                URISyntaxException |
                IOException |
                NoSuchAlgorithmException |
                InterruptedException
                ex) {
            throw new ConflictException(ex.getMessage());
        }
    }

    //TODO: should deletion provide a more detailed response?
    @DeleteMapping(ADMIN_REFGET_API_V1 + "/sequence/{id}")
    public RefgetData deleteRefgetObject(
            @PathVariable(name = "id") String id
    ){
        loggingUtil.debug(String.format("Admin API request: Delete sequence with ID: %s",id));
        try {
            hibernateUtil.deleteEntityObject(RefgetData.class, id);
            return hibernateUtil.readEntityObject(RefgetData.class, id, false);
        } catch (EntityDoesntExistException ex) {
            throw new ConflictException(ex.getMessage());
        } catch (EntityExistsException ex) {
            throw new ConflictException(ex.getMessage());
        }
    }

}