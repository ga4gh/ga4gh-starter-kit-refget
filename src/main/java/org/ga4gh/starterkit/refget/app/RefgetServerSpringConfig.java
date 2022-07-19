package org.ga4gh.starterkit.refget.app;

import org.apache.catalina.connector.*;
import org.apache.commons.cli.*;
import org.ga4gh.starterkit.common.config.DatabaseProps;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.common.hibernate.HibernateEntity;
import org.ga4gh.starterkit.common.util.*;
import org.ga4gh.starterkit.common.util.logging.*;
import org.ga4gh.starterkit.common.util.webserver.*;
import org.ga4gh.starterkit.refget.model.*;
import org.ga4gh.starterkit.refget.utils.hibernate.RefgetHibernateUtil;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.web.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.*;
import org.springframework.boot.web.servlet.*;
import org.springframework.context.annotation.*;
import org.ga4gh.starterkit.common.config.LogLevel;
import org.springframework.web.filter.*;

import java.io.*;
import java.util.*;

@Configuration
@ConfigurationProperties
public class RefgetServerSpringConfig {

    @Value("${server.admin.port:4501}")
    private String serverAdminPort;

    /* ******************************
     * LOGGING
     * ****************************** */

    @Bean
    public LoggingUtil loggingUtil() {
        return new LoggingUtil();
    }


    //configuring the webserver with custom configurations
    @Bean
    public WebServerFactoryCustomizer servletContainer() {
        Connector[] additionalConnectors = AdminEndpointsConnector.additionalConnector(serverAdminPort);
        ServerProperties serverProperties = new ServerProperties();
        return new TomcatMultiConnectorServletWebServerFactoryCustomizer(serverProperties, additionalConnectors);
    }

    // Filter out requests to "/admin/**" and route them to admin port
    @Bean
    public FilterRegistrationBean<AdminEndpointsFilter> adminEndpointsFilter() {
        return new FilterRegistrationBean<AdminEndpointsFilter>(new AdminEndpointsFilter(Integer.valueOf(serverAdminPort)));
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter(
            @Autowired ServerProps serverProps
    ) {
        return new CorsFilterBuilder(serverProps).buildFilter();
    }

    /**
     * Load command line options object, to enable parsing of program args
     * @return valid command line options to be parsed
     */
    @Bean
    public Options getCommandLineOptions() {
        final Options options = new Options();
        options.addOption("c", "config", true, "Path to Refget YAML config file");
        return options;
    }

    /**
     * Loads an empty Refget config container
     * @return Refget config container with empty properties
     */
    @Bean
    @Scope(RefgetServerConstants.PROTOTYPE)
    @Qualifier(RefgetServerConstants.EMPTY_REFGET_CONFIG_CONTAINER)
    public RefgetServerYamlConfigContainer emptyRefgetConfigContainer() {
        return new RefgetServerYamlConfigContainer(new RefgetServerYamlConfig());
    }

    /**
     * Loads a Refget config container singleton containing all default properties
     * @return Refget config container containing defaults
     */
    @Bean
    @Qualifier(RefgetServerConstants.DEFAULT_REFGET_CONFIG_CONTAINER)
    public RefgetServerYamlConfigContainer defaultRefgetConfigContainer() {
        return new RefgetServerYamlConfigContainer(new RefgetServerYamlConfig());
    }

    /**
     * Loads a Refget config container singleton containing user-specified properties (via config file)
     * @param args command line args
     * @param options valid set of command line options to be parsed
     * @param refgetConfigContainer empty Refget config container
     * @return Refget config container singleton containing user-specified properties
     */
    @Bean
    @Qualifier(RefgetServerConstants.USER_REFGET_CONFIG_CONTAINER)
    public RefgetServerYamlConfigContainer runtimeRefgetConfigContainer(
            @Autowired ApplicationArguments args,
            @Autowired() Options options,
            @Qualifier(RefgetServerConstants.EMPTY_REFGET_CONFIG_CONTAINER) RefgetServerYamlConfigContainer refgetConfigContainer
    ) {
        RefgetServerYamlConfigContainer userConfigContainer = CliYamlConfigLoader.load(RefgetServerYamlConfigContainer.class, args, options, "config");
        if (userConfigContainer != null) {
            return userConfigContainer;
        }
        return refgetConfigContainer;
    }

    /**
     * Loads the final Refget config container singleton containing merged properties
     * between default and user-specified
     * @param defaultContainer contains default properties
     * @param userContainer contains user-specified properties
     * @return contains merged properties
     */
    @Bean
    @Qualifier(RefgetServerConstants.FINAL_REFGET_CONFIG_CONTAINER)
    public RefgetServerYamlConfigContainer mergedRefgetConfigContainer(
            @Qualifier(RefgetServerConstants.DEFAULT_REFGET_CONFIG_CONTAINER) RefgetServerYamlConfigContainer defaultContainer,
            @Qualifier(RefgetServerConstants.USER_REFGET_CONFIG_CONTAINER) RefgetServerYamlConfigContainer userContainer
    ) {
        DeepObjectMerger merger = new DeepObjectMerger();
        merger.merge(userContainer, defaultContainer);
        return defaultContainer;
    }

    /**
     * Retrieve server props object from merged Refget config container
     * @param refgetConfigContainer merged Refget config container
     * @return merged server props
     */
    @Bean
    public ServerProps getServerProps(
            @Qualifier(RefgetServerConstants.FINAL_REFGET_CONFIG_CONTAINER) RefgetServerYamlConfigContainer refgetConfigContainer
    ) {
        return refgetConfigContainer.getRefget().getServerProps();
    }

    /**
     * Retrieve database props object from merged Refget config container
     * @param annotatedClasses list of hibernate entity classes to be managed by the Refget hibernate util
     * @param refgetConfigContainer merged Refget config container
     * @return merged database props
     */
    @Bean
    public DatabaseProps getDatabaseProps(
            @Autowired List<Class<? extends HibernateEntity<? extends Serializable>>> annotatedClasses,
            @Qualifier(RefgetServerConstants.FINAL_REFGET_CONFIG_CONTAINER) RefgetServerYamlConfigContainer refgetConfigContainer
    ) {
        return refgetConfigContainer.getRefget().getDatabaseProps();
    }

    /**
     * Retrieve Refget service info object from merged Refget config container
     * @param refgetConfigContainer merged Refget config container
     * @return merged Refget service info
     */
    @Bean
    public RefgetServiceInfo getServiceInfo(
            @Qualifier(RefgetServerConstants.FINAL_REFGET_CONFIG_CONTAINER) RefgetServerYamlConfigContainer refgetConfigContainer
    ) {
        return refgetConfigContainer.getRefget().getServiceInfo();
    }

    @Bean
    public CustomExceptionHandling customExceptionHandling() {
        return new CustomExceptionHandling();
    }

//    @Bean
//    public RefgetMetadata getMetadata(){
//        RefgetMetadata metadata = new RefgetMetadata();
//        return metadata;
//    }

//    @Bean
//    public ServerProps getServerProps() {
//        ServerProps serverProp = new ServerProps();
//        serverProp.setLogLevel(LogLevel.TRACE);
//        return serverProp;
//    }



    /* ******************************
     * HIBERNATE CONFIG
     * ****************************** */

    /**
     * List of hibernate entity classes to be managed by the Refget hibernate util
     * @return list of Refget-related managed entity classes
     */
    @Bean
    public List<Class<? extends HibernateEntity<? extends Serializable>>> getAnnotatedClasses() {
        List<Class<? extends HibernateEntity<? extends Serializable>>> annotatedClasses = new ArrayList<>();
        annotatedClasses.add(RefgetData.class);
        annotatedClasses.add(Aliases.class);
        return annotatedClasses;
    }

    /**
     * Loads/retrieves the hibernate util singleton providing access to Refget-related database tables
     * @param annotatedClasses list of Refget-related entities to be managed
     * @param databaseProps database properties from configuration
     * @return loaded hibernate util singleton managing Refget entities
     */
    @Bean
    public RefgetHibernateUtil getRefgetHibernateUtil(
            @Autowired List<Class<? extends HibernateEntity<? extends Serializable>>> annotatedClasses,
            @Autowired DatabaseProps databaseProps
    ) {
        RefgetHibernateUtil hibernateUtil = new RefgetHibernateUtil();
        hibernateUtil.setAnnotatedClasses(annotatedClasses);
        hibernateUtil.setDatabaseProps(databaseProps);
        return hibernateUtil;
    }

}
