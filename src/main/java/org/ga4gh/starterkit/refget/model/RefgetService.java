package org.ga4gh.starterkit.refget.model;

import java.util.List;

public class RefgetService {
    private boolean circular_supported;
    private List<String> algorithms;
    private Integer subsequence_limit;
    private List<String> supported_api_versions;

    public RefgetService(boolean circular_supported, List<String> algorithms, Integer subsequence_limit, List<String> supported_api_versions) {
        this.circular_supported = circular_supported;
        this.algorithms = algorithms;
        this.subsequence_limit = subsequence_limit;
        this.supported_api_versions = supported_api_versions;
    }

    public RefgetService() {
    }

    public boolean isCircular_supported() {
        return circular_supported;
    }

    public void setCircular_supported(boolean circular_supported) {
        this.circular_supported = circular_supported;
    }

    public List<String> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<String> algorithms) {
        this.algorithms = algorithms;
    }

    public Integer getSubsequence_limit() {
        return subsequence_limit;
    }

    public void setSubsequence_limit(Integer subsequence_limit) {
        this.subsequence_limit = subsequence_limit;
    }

    public List<String> getSupported_api_versions() {
        return supported_api_versions;
    }

    public void setSupported_api_versions(List<String> supported_api_versions) {
        this.supported_api_versions = supported_api_versions;
    }
}
