package jfullam.vfabric.jenkins.plugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;

import java.io.IOException;

import jfullam.vfabric.jenkins.plugin.rest.ApplicationDirectorClient;
import jfullam.vfabric.jenkins.plugin.rest.ApplicationDirectorClientManager;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author Jonathan Fullam
 */
public class ApplicationDirectorPostBuildDeployer extends BuildWrapper {

	private String appName;
	private String deploymentProfile;
	private ApplicationDirectorClient applicationDirector;

	@DataBoundConstructor
	public ApplicationDirectorPostBuildDeployer(String appName,
			String deploymentProfile) {
		this.appName = appName;
		this.deploymentProfile = deploymentProfile;
	}

	@Override
	public Environment setUp(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws IOException, InterruptedException {

		Environment applicationDirectorEnv = new Environment() {
			public boolean tearDown(final AbstractBuild build,
					final BuildListener listener) throws IOException,
					InterruptedException {

				listener.getLogger().println("AppName:  " + appName);
				listener.getLogger().println(
						"DeploymentProfile:  " + deploymentProfile);
				listener.getLogger().println(
						"base uri:  " + getDescriptor().getAppDirBaseURI());
				listener.getLogger().println(
						"user:  " + getDescriptor().getUserName());
				listener.getLogger().println(
						"last deployment:  "
								+ getDescriptor().getLastDeployment());
				
				applicationDirector = ApplicationDirectorClientManager.applicationDirectorClient();
				applicationDirector.tearDown(appName, deploymentProfile);
				applicationDirector.scheduleDeployment(appName,
						deploymentProfile);

				saveLastDeploymentId(String.valueOf(System.currentTimeMillis()));
				return true;
			}

			private void saveLastDeploymentId(String id) {
				getDescriptor().setLastDeployment(id);
			}
		};

		return applicationDirectorEnv;
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

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData)
				throws FormException {

			appDirBaseURI = formData.getString("appDirBaseURI");
			userName = formData.getString("userName");
			password = formData.getString("password");
			ApplicationDirectorClientManager
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
