package jfullam.vfabric.jenkins.plugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ListBoxModel;

import java.io.IOException;
import java.util.StringTokenizer;

import jfullam.vfabric.jenkins.plugin.rest.ProvisioningService;
import jfullam.vfabric.jenkins.plugin.utils.ApplicationDirectorServiceManager;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author Jonathan Fullam
 */
public class ApplicationDirectorPostBuildDeployer extends BuildWrapper {

	private static final String PROVISION = "provision"; 
	
	private String application;
	private String deploymentProfile;
	private String deployment;
	private String component;
	private String updateProperty;
	private String updatePropertyValue;
	
	private String provisionOrUpdate;
	


	@DataBoundConstructor
	public ApplicationDirectorPostBuildDeployer(String provisionOrUpdate, String application,
			String deploymentProfile, String deployment, String component, String updateProperty,
			String updatePropertyValue) {
		
		this.application = application;
		this.deploymentProfile = deploymentProfile;
		this.deployment = deployment;
		this.component = component;
		this.updateProperty = updateProperty;
		this.updatePropertyValue = updatePropertyValue;
		this.provisionOrUpdate = provisionOrUpdate;
	}
	


	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {

		Environment applicationDirectorEnv = new Environment() {
			public boolean tearDown(final AbstractBuild build,
					final BuildListener listener) throws IOException,
					InterruptedException {
				
				boolean result = false;
				JsonNode jsonResponse = null;

				listener.getLogger().println("Application ID:  " + application);
				listener.getLogger().println(
						"Deployment Profile ID:  " + deploymentProfile);
				listener.getLogger().println(
						"base uri:  " + getDescriptor().getAppDirBaseURI());
				listener.getLogger().println(
						"user:  " + getDescriptor().getUserName());

				
				if (provisionOrUpdate.equals(PROVISION)) {
					//Need to parse the deployment profile and application into an ID and name due to the need
					//for the profile and application name when finding a specific deployment.
					//These fields are formatted as name,id
					StringTokenizer profileParser = new StringTokenizer(deploymentProfile, ",");
					StringTokenizer applicationParser = new StringTokenizer(application, ",");
					ApplicationDirectorServiceManager.provisioningService().tearDown(applicationParser.nextToken(), profileParser.nextToken());
					ApplicationDirectorServiceManager.provisioningService().scheduleDeployment(profileParser.nextToken());
				} else {
					ApplicationDirectorServiceManager.updateService().updateDeployment(deployment,component,updateProperty,updatePropertyValue);
				}

				return true;
			}

		};

		return applicationDirectorEnv;
	}
	
	public String getApplication() {
		return application;
	}



	public void setApplication(String application) {
		this.application = application;
	}



	public String getProvisionOrUpdate() {
		return provisionOrUpdate;
	}



	public void setProvisionOrUpdate(String provisionOrUpdate) {
		this.provisionOrUpdate = provisionOrUpdate;
	}

	public String getDeploymentProfile() {
		return deploymentProfile;
	}

	public void setDeploymentProfile(String deploymentProfile) {
		this.deploymentProfile = deploymentProfile;
	}

	public String getDeployment() {
		return deployment;
	}

	public void setDeployment(String deployment) {
		this.deployment = deployment;
	}

	public String getUpdateProperty() {
		return updateProperty;
	}

	public void setUpdateProperty(String updateProperty) {
		this.updateProperty = updateProperty;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getUpdatePropertyValue() {
		return updatePropertyValue;
	}

	public void setUpdatePropertyValue(String updatePropertyValue) {
		this.updatePropertyValue = updatePropertyValue;
	}

	@Override
	public Descriptor getDescriptor() {
		return (Descriptor) super.getDescriptor();
	}

	@Extension
	public static class Descriptor extends BuildWrapperDescriptor {

		private String appDirBaseURI;
		private String userName;
		private String password;

		private String lastDeployment;

		@Override
		public boolean isApplicable(AbstractProject<?, ?> item) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Application Director Client";
		}
		
		public ListBoxModel doFillDeploymentItems() {
			return ApplicationDirectorServiceManager.userInterfaceService().getDeployments();
		}
		
		public ListBoxModel doFillComponentItems(@QueryParameter String deployment) {
			return ApplicationDirectorServiceManager.userInterfaceService().getComponents(deployment);
		}
		
		public ListBoxModel doFillUpdatePropertyItems(@QueryParameter String component) {
			return ApplicationDirectorServiceManager.userInterfaceService().getUpdateProperties(component);
		}

		public ListBoxModel doFillApplicationItems() {
			return ApplicationDirectorServiceManager.userInterfaceService().getApplications();
		}
		
		public ListBoxModel doFillDeploymentProfileItems(@QueryParameter String application) {
			
			if (StringUtils.isNotBlank(application)) {
				//Get the id by parsing past the name (application is formatted as name,id)
				StringTokenizer applicationParser = new StringTokenizer(application, ",");
				applicationParser.nextToken();
				return ApplicationDirectorServiceManager.userInterfaceService().getDeploymentProfiles(applicationParser.nextToken());
			}
			return new ListBoxModel();
			
		}
		
		@Override
		public boolean configure(StaplerRequest req, JSONObject formData)
				throws FormException {

			appDirBaseURI = formData.getString("appDirBaseURI");
			userName = formData.getString("userName");
			password = formData.getString("password");
			ApplicationDirectorServiceManager
					.configureApplicationDirectorClient(appDirBaseURI,
							userName, password);

			save();
			return super.configure(req, formData);
		}

		/**
		 * This method returns the base Application Director API URI that will
		 * be used to call the REST based interface.
		 */
		public String getAppDirBaseURI() {
			return appDirBaseURI;
		}

		public String getUserName() {
			return userName;
		}

		public String getPassword() {
			return password;
		}

		public String getLastDeployment() {
			return lastDeployment;
		}

		public void setLastDeployment(String id) {
			lastDeployment = id;
			save();
		}

	}

	

	


}
