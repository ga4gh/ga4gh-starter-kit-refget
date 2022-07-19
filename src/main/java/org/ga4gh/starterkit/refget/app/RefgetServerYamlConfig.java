package org.ga4gh.starterkit.refget.app;

import org.ga4gh.starterkit.common.config.DatabaseProps;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.refget.model.RefgetServiceInfo;

/**
 * Contains multiple configuration objects affecting application behavior.
 * To be deserialized/loaded as part of a YAML config file specified on the
 * command line
 */
public class RefgetServerYamlConfig {

    private ServerProps serverProps;
    private DatabaseProps databaseProps;
    private RefgetServiceInfo serviceInfo;

    /**
     * Instantiates a new RefgetStandaloneYamlConfig object with default properties
     */
    public RefgetServerYamlConfig() {
        serverProps = new ServerProps();
        databaseProps = new DatabaseProps();
        serviceInfo = new RefgetServiceInfo();
    }

    /**
     * Assign serverProps
     * @param serverProps ServerProps object
     */
    public void setServerProps(ServerProps serverProps) {
        this.serverProps = serverProps;
    }

    /**
     * Retrieve server props
     * @return ServerProps object
     */
    public ServerProps getServerProps() {
        return serverProps;
    }

    /**
     * Assign databaseProps
     * @param databaseProps DatabaseProps object
     */
    public void setDatabaseProps(DatabaseProps databaseProps) {
        this.databaseProps = databaseProps;
    }

    /**
     * Retrieve databaseProps
     * @return DatabaseProps object
     */
    public DatabaseProps getDatabaseProps() {
        return databaseProps;
    }

    /**
     * Assign serviceInfo
     * @param serviceInfo RefgetServiceInfo object
     */
    public void setServiceInfo(RefgetServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    /**
     * Retrieve serviceInfo
     * @return RefgetServiceInfo object
     */
    public RefgetServiceInfo getServiceInfo() {
        return serviceInfo;
    }

}