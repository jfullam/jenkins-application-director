package jfullam.vfabric.jenkins.plugin.rest;

public class ApplicationDirectorRestRuntimeException extends RuntimeException {


	public ApplicationDirectorRestRuntimeException(Throwable t) {
		super("There was an issue while using the Application Director REST API.", t);
	}

}
