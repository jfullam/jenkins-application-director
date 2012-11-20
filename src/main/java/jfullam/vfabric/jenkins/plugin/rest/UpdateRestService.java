package jfullam.vfabric.jenkins.plugin.rest;

import java.util.HashMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import jfullam.vfabric.rest.appdir.ApplicationDirectorRestProvider;

public class UpdateRestService implements UpdateService {
	
	private ApplicationDirectorRestProvider restProvider;
	

	@Override
	public JsonNode updateDeployment(String deployment, String component,
			String updateProperty, String updatePropertyValue) {
		
		ConfigUpdateRequest requestObj = new ConfigUpdateRequest();
		HashMap<String, String> props = new HashMap<String, String>();
		props.put(updateProperty, updatePropertyValue);
		requestObj.addProfileNodeComponentProperties(component, props);
		
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode jsonResult = restProvider.updateDeployment(deployment, mapper.valueToTree(requestObj));
		return jsonResult;
		
	}

	@Override
	public void setRestProvider(ApplicationDirectorRestProvider restProvider) {
		this.restProvider = restProvider;		
	}

}
