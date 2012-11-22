package jfullam.vfabric.rest.appdir;

import jfullam.vfabric.jenkins.plugin.rest.ServiceException;

import org.codehaus.jackson.JsonNode;

/**
 * Provide direct access to the Application Director REST API
 * 
 * @author Jonathan Fullam
 */
public interface RestProvider {

	 JsonNode getDeployments() throws ServiceException;

	 JsonNode postTeardown(String deploymentId) throws ServiceException; 

	 JsonNode postScheduleDeployment(String deployemtProfileId,
			JsonNode deployRequest) throws ServiceException; 

	JsonNode getApplications() throws ServiceException; 

	JsonNode getDeployProperties(String deploymentProfileId) throws ServiceException; 

	JsonNode getDeploymentProfiles(String applicationId) throws ServiceException; 
	
	JsonNode getDeploymentUpdateProperties(String deploymentId) throws ServiceException; 

	JsonNode updateDeployment(String deployment, JsonNode updateRequest) throws ServiceException; 

}