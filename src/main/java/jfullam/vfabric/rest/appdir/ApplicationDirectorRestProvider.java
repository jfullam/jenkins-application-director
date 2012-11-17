package jfullam.vfabric.rest.appdir;

import org.codehaus.jackson.JsonNode;

/**
 * @author Jonathan Fullam
 */
public interface ApplicationDirectorRestProvider {

	 JsonNode getDeployments();

	 JsonNode postTeardown(String deploymentId);

	 JsonNode postScheduleDeployment(String deployemtProfileId,
			JsonNode deployRequest);

	JsonNode getApplications();

	JsonNode getDeployProperties(String deploymentProfileId);

	JsonNode getDeploymentProfiles(String applicationId);

}