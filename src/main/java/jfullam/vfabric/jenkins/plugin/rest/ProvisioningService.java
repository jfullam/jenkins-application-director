package jfullam.vfabric.jenkins.plugin.rest;

import org.codehaus.jackson.JsonNode;

import jfullam.vfabric.rest.appdir.RestProvider;

/**
 * Application Director service for provisioning and teardown actions.
 * 
 * @author Jonathan Fullam
 */
public interface ProvisioningService {

	/**
	 * Schedule the teardown an existing deployment
	 * 
	 * @param applicationName - The name of the application 
	 * @param deploymentProfileName - deployment profile name
	 * @return ServiceResult - based on the response from Application Director
	 * @throws ServiceException
	 */
	ServiceResult tearDown(String applicationName, String deploymentProfileName) throws ServiceException;
	/**
	 * Schedule the deployment of an application based on a deployment profile.
	 * 
	 * @param deploymentProfielId
	 * @return ServiceResult - based on the response from Application Director
	 * @throws ServiceException
	 */
	ServiceResult scheduleDeployment(String deploymentProfielId) throws ServiceException;
	
	void setRestProvider(RestProvider restProvider);
	
	
}
