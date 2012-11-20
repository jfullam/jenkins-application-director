package jfullam.vfabric.jenkins.plugin.rest;

import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;

import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import jfullam.vfabric.rest.appdir.ApplicationDirectorRestProvider;

public class UserInterfaceDataRestService implements UserInterfaceDataService {
	
	private ApplicationDirectorRestProvider restProvider;
	private JsonNode lastComponentsFetch;

	@Override
	public ListBoxModel getDeployments() {
		
		JsonNode jsonDeployments = restProvider.getDeployments();
		
		ArrayList<Option> options = new ArrayList<ListBoxModel.Option>();		
		Iterator<JsonNode> resultsNodes = jsonDeployments.get("results").getElements();
		
		while (resultsNodes.hasNext()) {
			JsonNode deployment = resultsNodes.next();
			if (isRunning(deployment)) {
				String key = deployment.get("applicationName").asText() + 
							//" - " + deployment.get("applicationDescription").asText() +
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

	@Override
	public ListBoxModel getComponents(String deploymentId) {
		
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
	public ListBoxModel getApplications() {
		ArrayList<Option> options = new ArrayList<ListBoxModel.Option>();
		JsonNode jsonApplications = restProvider.getApplications();
		Iterator<JsonNode> resultsNodes = jsonApplications.get("results").getElements();
		while (resultsNodes.hasNext()) {
			JsonNode application = resultsNodes.next();
			String appName = application.get("name").asText();
			String appId = application.get("id").asText();
			options.add(new Option(appName, appName+","+appId));
			
		}
		return new ListBoxModel(options);
	}

	@Override
	public ListBoxModel getDeploymentProfiles(String application) {
		ArrayList<Option> options = new ArrayList<ListBoxModel.Option>();
		JsonNode deploymentProfiles = restProvider.getDeploymentProfiles(application);
		
		Iterator<JsonNode> resultsNodes = deploymentProfiles.get("results").getElements();
		while (resultsNodes.hasNext()) {
			JsonNode profile = resultsNodes.next();
			String profileName = profile.get("name").asText();
			String profileId = profile.get("id").asText();
			options.add(new Option(profileName, profileName+","+profileId));
		}
		return new ListBoxModel(options);
	}


	@Override
	public void setRestProvider(ApplicationDirectorRestProvider restProvider) {
		this.restProvider = restProvider;
	}




}
