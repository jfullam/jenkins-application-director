package jfullam.vfabric.jenkins.plugin.rest;

import jfullam.vfabric.rest.appdir.RestProvider;
import hudson.util.ListBoxModel;

/**
 * Application Service used to get values for UI select 
 * boxes.
 * 
 * @author Jonathan Fullam
 */
public interface UserInterfaceDataService {

	void setRestProvider(RestProvider restProvider);
	
	/**
	 * @return ListBoxModel - list of current deployments
	 * @throws ServiceException
	 */
	ListBoxModel getDeployments() throws ServiceException;
	/**
	 * @param deploymentId - ID of a current deployment
	 * @return ListBoxModel - List of application components
	 * @throws ServiceException
	 */
	ListBoxModel getComponents(String deploymentId) throws ServiceException;
	/**
	 * @param component - Name of an existing component
	 * @return ListBoxModel - List of update propertes for the component
	 */
	ListBoxModel getUpdateProperties(String component);

	/**
	 * @return ListBoxModel - List of applications in App. Dir.
	 * @throws ServiceException
	 */
	ListBoxModel getApplications() throws ServiceException;

	/**
	 * @param applicationId - the id of an application (blueprint) in Application
	 * Director
	 * @return ListBoxModel - List of deployment profile for a given application
	 * @throws ServiceException
	 */
	ListBoxModel getDeploymentProfiles(String applicaitonId) throws ServiceException;
	
}
