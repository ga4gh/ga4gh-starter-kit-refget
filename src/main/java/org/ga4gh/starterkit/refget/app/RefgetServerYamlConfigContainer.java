package org.ga4gh.starterkit.refget.app;

import org.ga4gh.starterkit.common.config.ContainsServerProps;
import org.ga4gh.starterkit.common.config.ServerProps;

/**
 * Top-level configuration container object for standalone deployments. To
 * be deserialized/loaded as part of a YAML config file specified on the command
 * line.
 */
public class RefgetServerYamlConfigContainer implements ContainsServerProps {

    /**
     * Nested configuration object
     */
    private RefgetServerYamlConfig refget;

    /**
     * Instantiates a new RefgetStandaloneYamlConfigContainer object with default properties
     */
    public RefgetServerYamlConfigContainer() {
        refget = new RefgetServerYamlConfig();
    }

    /**
     * Instantiates a new RefgetStandaloneYamlConfigContainer with a preconfigured RefgetStandaloneYamlConfig object
     * @param refget preconfigured RefgetStandaloneYamlConfig object
     */
    public RefgetServerYamlConfigContainer(RefgetServerYamlConfig refget) {
        this.refget = refget;
    }

    /**
     * Retrieve server props through the nested inner config object
     * @return server props
     */
    public ServerProps getServerProps() {
        return getRefget().getServerProps();
    }

    /**
     * Assign refget
     * @param refget RefgetStandaloneYamlConfig object
     */
    public void setRefget(RefgetServerYamlConfig refget) {
        this.refget = refget;
    }

    /**
     * Retrieve refget
     * @return RefgetStandaloneYamlConfig object
     */
    public RefgetServerYamlConfig getRefget() {
        return refget;
    }
}
