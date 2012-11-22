package jfullam.vfabric.jenkins.plugin.rest;

import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;

import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import jfullam.vfabric.rest.appdir.RestProvider;

/**
 * Calls Application Director REST API to retrieve values for UI 
 * select controls.
 * 
 * @author Jonathan Fullam
 */
public class UserInterfaceDataRestService implements UserInterfaceDataService {
	
	private RestProvider restProvider;
	private JsonNode lastComponentsFetch;

	/* (non-Javadoc)
	 * 
	 * Gets all deployments calling the REST API. Deployments are represented
	 * to the user as application name - deployment profile combinations.  The
	 * options shown to the user won't necessarily be unique but the ID of 
	 * each deployment is.
	 * 
	 * @see jfullam.vfabric.jenkins.plugin.rest.UserInterfaceDataService#getDeployments()
	 */
	@Override
	public ListBoxModel getDeployments() throws ServiceException {
		
		JsonNode jsonDeployments = restProvider.getDeployments();
		
		ArrayList<Option> options = new ArrayList<ListBoxModel.Option>();		
		Iterator<JsonNode> resultsNodes = jsonDeployments.get("results").getElements();
		
		while (resultsNodes.hasNext()) {
			JsonNode deployment = resultsNodes.next();
			if (isRunning(deployment)) {
				String key = deployment.get("applicationName").asText() + 
							" - " + deployment.get("deploymentProfileName").asText();
				String value = deployment.get("id").asText();
				options.add(new Option(key, value));
			}
		}
		return new ListBoxModel(options);
	}

	private boolean isRunning(JsonNode deployment) {
		return deployment.get("deploymentState").asText().equals("DEPLOYMENT_SUCCESS");
	}

	/* (non-Javadoc)
	 * 
	 * Fetches all the components for a  specific deployment.  This data is kept
	 * local to this instance for use when retrieving the individual component properties.
	 * 
	 * @see jfullam.vfabric.jenkins.plugin.rest.UserInterfaceDataService#getComponents(java.lang.String)
	 */
	@Override
	public ListBoxModel getComponents(String deploymentId) throws ServiceException {
		
		ArrayList<Option> options = new ArrayList<ListBoxModel.Option>();
		
		JsonNode jsonUpdateProps = restProvider.getDeploymentUpdateProperties(deploymentId);
		//Save the deployment fetched so they can be used for similar requests
		lastComponentsFetch = jsonUpdateProps.get("result").get("profileNodeComponents");	
		
		if (lastComponentsFetch != null) {
			Iterator<JsonNode> resultsNodes = lastComponentsFetch.getElements();
	
			
			while (resultsNodes.hasNext()) {
				JsonNode component = resultsNodes.next();
				String keyValue = component.get("name").asText();
				options.add(new Option(keyValue));
			}
		}
		
		return new ListBoxModel(options);
	}
	
	/* (non-Javadoc)
	 * 
	 * Creates a list of update properties using a previously fetched list of 
	 * deployment components (and thier properties)
	 * 
	 * @see jfullam.vfabric.jenkins.plugin.rest.UserInterfaceDataService#getUpdateProperties(java.lang.String)
	 */
	@Override
	public ListBoxModel getUpdateProperties(String component) {
		
		ArrayList<Option> options = new ArrayList<ListBoxModel.Option>();
		
		if (lastComponentsFetch != null) {
		 
			Iterator<JsonNode> componentNodes = lastComponentsFetch.getElements();
			
			while (componentNodes.hasNext()) {
				JsonNode jsonComponent = componentNodes.next();
				if (jsonComponent.get("name").asText().equals(component)) {
					Iterator<JsonNode> propertyNodes = jsonComponent.get("property").getElements();
					while(propertyNodes.hasNext()) {
						JsonNode jsonProperty = propertyNodes.next();
						String keyValue = jsonProperty.get("key").asText();
						options.add(new Option(keyValue));
					}
	
				}
	
			}
		}
		
		return new ListBoxModel(options);
	}
	
	@Override
	public ListBoxModel getApplications() throws ServiceException {
		ArrayList<Option> options = new ArrayList<ListBoxModel.Option>();
		JsonNode jsonApplications = restProvider.getApplications();
		Iterator<JsonNode> resultsNodes = jsonApplications.get("results").getElements();
		while (resultsNodes.hasNext()) {
			JsonNode application = resultsNodes.next();
			String appName = application.get("name").asText();
			String appId = application.get("id").asText();
			
			//Need to keep the appName and appID as a value because
			//appName is needed when finding an exiting deployment by application in
			//a subsequent call
			options.add(new Option(appName, appName+","+appId));
			
		}
		return new ListBoxModel(options);
	}

	@Override
	public ListBoxModel getDeploymentProfiles(String applicationId) throws ServiceException {
		ArrayList<Option> options = new ArrayList<ListBoxModel.Option>();
		JsonNode deploymentProfiles = restProvider.getDeploymentProfiles(applicationId);
		
		if (deploymentProfiles != null) {
			Iterator<JsonNode> resultsNodes = deploymentProfiles.get("results").getElements();
			while (resultsNodes.hasNext()) {
				JsonNode profile = resultsNodes.next();
				String profileName = profile.get("name").asText();
				String profileId = profile.get("id").asText();
				
				//Need to keep the profileName and ID as a value because
				//name is needed when finding an exiting deployment by deployment profile in
				//a subsequent call
				options.add(new Option(profileName, profileName+","+profileId));
			}
		}
		return new ListBoxModel(options);
	}


	@Override
	public void setRestProvider(RestProvider restProvider) {
		this.restProvider = restProvider;
	}




}
