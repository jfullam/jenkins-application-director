package jfullam.vfabric.jenkins.plugin.rest;

import java.util.Iterator;

import jfullam.vfabric.rest.appdir.ApplicationDirectorRestProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;

/**
 * @author Jonathan Fullam
 */
public class ProvisioningRestService implements ProvisioningService {

	private ApplicationDirectorRestProvider appDirRestProvider;
	
	private static final Log log = LogFactory.getLog(ApplicationDirectorRestProvider.class);

	/* (non-Javadoc)
	 * @see com.vmware.demo.vfabric.appdirector.jenkins.ApplicationDirectorClient#tearDown(java.lang.String, java.lang.String)
	 * 
	 * Looks for a running deployment that matches the application name and profile name.  
	 * If none are found, nothing happens.
	 */
	public void tearDown(String appName, String profileName) {
		
		String deploymentId = findDeploymentId(appName, profileName);
		if (deploymentId != null) {
			log.info("Tearing down deployment with id of " + deploymentId);
			appDirRestProvider.postTeardown(deploymentId);
		} else {
			log.info("Nothing to teardown");
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
	public void scheduleDeployment(String deploymentProfileId) {
		
		log.info("Scheduling a deployment for profile with id " + deploymentProfileId);
		JsonNode deployRequest = appDirRestProvider.getDeployProperties(deploymentProfileId);
		appDirRestProvider.postScheduleDeployment(deploymentProfileId, deployRequest);
	}

	private String findDeploymentId(String applicationName,
			String profileName) {
		
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


	public ApplicationDirectorRestProvider getRestProvider() {
		return appDirRestProvider;
	}


	public void setRestProvider(ApplicationDirectorRestProvider restProvider) {
		this.appDirRestProvider = restProvider;
	}	
	
}