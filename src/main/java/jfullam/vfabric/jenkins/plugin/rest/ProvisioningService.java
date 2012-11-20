package jfullam.vfabric.jenkins.plugin.rest;

import jfullam.vfabric.rest.appdir.ApplicationDirectorRestProvider;

/**
 * @author Jonathan Fullam
 */
public interface ProvisioningService {

	void tearDown(String applicationName, String deploymentProfileName);
	void scheduleDeployment(String deploymentProfielId);
	void setRestProvider(ApplicationDirectorRestProvider restProvider);
	
	
}
