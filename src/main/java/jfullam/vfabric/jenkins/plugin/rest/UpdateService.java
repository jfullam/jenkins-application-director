package jfullam.vfabric.jenkins.plugin.rest;

import jfullam.vfabric.rest.appdir.RestProvider;

/**
 * Application Director service to update existing deployments
 * 
 * @author Jonathan Fullam
 */
public interface UpdateService {
	
	void setRestProvider(RestProvider restProvider);
	
	/**
	 * @param deployment - deploymentId of an existing deployment
	 * @param component - component to update
	 * @param updateProperty - propert to update
	 * @param updatePropertyValue
	 * @return ServiceResult - the result of making the call to Application Director
	 * @throws ServiceException
	 */
	ServiceResult updateDeployment(String deployment, String component,
			String updateProperty, String updatePropertyValue) throws ServiceException;

}
