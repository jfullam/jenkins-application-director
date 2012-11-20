package jfullam.vfabric.jenkins.plugin.rest;

import org.codehaus.jackson.JsonNode;

import jfullam.vfabric.rest.appdir.ApplicationDirectorRestProvider;

public interface UpdateService {
	
	void setRestProvider(ApplicationDirectorRestProvider restProvider);
	
	JsonNode updateDeployment(String deployment, String component,
			String updateProperty, String updatePropertyValue);

}
