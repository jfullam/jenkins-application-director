package jfullam.vfabric.jenkins.plugin.rest;

import jfullam.vfabric.rest.appdir.ApplicationDirectorRestProvider;
import hudson.util.ListBoxModel;

public interface UserInterfaceDataService {

	void setRestProvider(ApplicationDirectorRestProvider restProvider);
	
	ListBoxModel getDeployments();
	ListBoxModel getComponents(String deploymentId);
	ListBoxModel getUpdateProperties(String component);

	ListBoxModel getApplications();

	ListBoxModel getDeploymentProfiles(String application);
	
}
