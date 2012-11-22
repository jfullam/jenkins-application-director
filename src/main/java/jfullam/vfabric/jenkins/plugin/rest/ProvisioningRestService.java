package jfullam.vfabric.jenkins.plugin.rest;

import java.util.Iterator;

import jfullam.vfabric.rest.appdir.RestProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;

/**
 * Calls the Application Director REST API for scheduling actions such as
 * deploying / provisioning an application environment
 * 
 * @author Jonathan Fullam
 */
public class ProvisioningRestService implements ProvisioningService {

	private RestProvider appDirRestProvider;
	
	private static final Log log = LogFactory.getLog(RestProvider.class);

	/* (non-Javadoc)
	 * @see com.vmware.demo.vfabric.appdirector.jenkins.ApplicationDirectorClient#tearDown(java.lang.String, java.lang.String)
	 * 
	 * Looks for a running deployment that matches the application name and profile name.  
	 * If none are found, nothing happens.
	 */
	public ServiceResult tearDown(String appName, String profileName) throws ServiceException {
		
		String deploymentId = findDeploymentId(appName, profileName);
		if (deploymentId != null) {
			log.info("Tearing down deployment with id of " + deploymentId);
			JsonNode jsonResult = appDirRestProvider.postTeardown(deploymentId);
			return ServiceResult.parseJson(jsonResult);
		} else {
			log.info("Nothing to teardown");
			return new ServiceResult(true, "There was no existing deployment to teardown.");
		}
			
	}

	
	/* (non-Javadoc)
	 * @see com.vmware.demo.vfabric.appdirector.jenkins.ApplicationDirectorClient#scheduleDeployment(java.lang.String, java.lang.String)
	 *
	 * Finds the application ID based on the provided application name.  This application ID is then used
	 * to find the available deployment profiles and an ID is retrieved by matching the profile name.  A
	 * deployment is then scheduled with the deployment profile ID. 
	 * 
	 * NOTE:  The application ID used for finding a profile is not necessarily the latest version of the application.
	 */
	public ServiceResult scheduleDeployment(String deploymentProfileId) throws ServiceException {
		
		log.info("Scheduling a deployment for profile with id " + deploymentProfileId);
		JsonNode deployRequest = appDirRestProvider.getDeployProperties(deploymentProfileId);
		JsonNode result = 
			appDirRestProvider.postScheduleDeployment(deploymentProfileId, deployRequest);
		return ServiceResult.parseJson(result);
	}

	private String findDeploymentId(String applicationName,
			String profileName) throws ServiceException {
		
		JsonNode jsonDeployments = appDirRestProvider.getDeployments();
		Iterator<JsonNode> resultsNodes = jsonDeployments.get("results").getElements();
		while (resultsNodes.hasNext()) {
			JsonNode deployment = resultsNodes.next();
			if (isRunningAndIsAMatch(applicationName, profileName, deployment)) {
				return deployment.get("id").asText();
			}
		}
		return null;
	}

	private boolean isRunningAndIsAMatch(String applicationName,
			String deploymentProfileName, JsonNode deployment) {
		if (deployment.get("applicationName").asText().equals(applicationName) &&
				deployment.get("deploymentState").asText().equals("DEPLOYMENT_SUCCESS") &&
				deployment.get("deploymentProfileName").asText().equalsIgnoreCase(deploymentProfileName)) {
			return true;
		}
		return false;
	}


	public RestProvider getRestProvider() {
		return appDirRestProvider;
	}


	public void setRestProvider(RestProvider restProvider) {
		this.appDirRestProvider = restProvider;
	}	
	
}
