package jfullam.vfabric.jenkins.plugin.rest;

import java.util.HashMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import jfullam.vfabric.rest.appdir.RestProvider;

/**
 * REST access to Application Director deployment update
 * capabilities
 * 
 * @author Jonathan Fullam
 */
public class UpdateRestService implements UpdateService {
	
	private RestProvider restProvider;
	
	/* 
	 * Creates a ConfigUpdateRequest using user data for calling the update service.
	 * 
	 * @see jfullam.vfabric.jenkins.plugin.rest.UpdateService#updateDeployment(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ServiceResult updateDeployment(String deployment, String component,
			String updateProperty, String updatePropertyValue) throws ServiceException  {
		
		ConfigUpdateRequest requestObj = new ConfigUpdateRequest();
		HashMap<String, String> props = new HashMap<String, String>();
		props.put(updateProperty, updatePropertyValue);
		requestObj.addProfileNodeComponentProperties(component, props);
		
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode jsonResult;
		try {
			jsonResult = restProvider.updateDeployment(deployment, mapper.valueToTree(requestObj));
		} catch (Throwable e) {
			throw new ServiceException(e);
		}
		return ServiceResult.parseJson(jsonResult);
		
	}

	@Override
	public void setRestProvider(RestProvider restProvider) {
		this.restProvider = restProvider;		
	}

}
