package org.ga4gh.starterkit.refget.app;

import org.apache.commons.cli.Options;
import org.ga4gh.starterkit.common.util.webserver.ServerPropertySetter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan(basePackages = {"org.ga4gh.starterkit.refget"})
public class RefgetServer {

	/**
	 * Run the Refget standalone server as a Spring Boot application.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		boolean setupSuccess = setup(args);
		if (setupSuccess) {
			SpringApplication.run(RefgetServer.class, args);
		} else {
			System.out.println("Application failed at initial setup phase, this is likely an error in the YAML config file. Exiting");
		}
	}

	private static boolean setup(String[] args) {
		Options options = new RefgetServerSpringConfig().getCommandLineOptions();
		ServerPropertySetter setter = new ServerPropertySetter();
		return setter.setServerProperties(RefgetServerYamlConfigContainer.class, args, options, "config");
	}
}



