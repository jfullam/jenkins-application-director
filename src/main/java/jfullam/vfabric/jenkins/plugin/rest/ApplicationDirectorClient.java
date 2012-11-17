package jfullam.vfabric.jenkins.plugin.rest;

import jfullam.vfabric.rest.appdir.ApplicationDirectorRestProvider;

/**
 * @author Jonathan Fullam
 */
public interface ApplicationDirectorClient {

	void tearDown(String applicationName, String deploymentProfileName);
	void scheduleDeployment(String applicationName, String deploymentProfileName);
	void setRestProvider(ApplicationDirectorRestProvider restProvider);
	
	
}
