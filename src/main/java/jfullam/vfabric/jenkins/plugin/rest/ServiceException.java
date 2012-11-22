package jfullam.vfabric.jenkins.plugin.rest;

/**
 * Exception for any problems while calling ApplicationDirector
 * services.
 * 
 * @author Jonathan Fullam
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = -5597074476766201758L;

	public ServiceException(Throwable t) {
		super("There was an issue while using the Application Director REST API.", t);
	}
	
	public ServiceException(String msg) {
		super(msg);
	}

}
