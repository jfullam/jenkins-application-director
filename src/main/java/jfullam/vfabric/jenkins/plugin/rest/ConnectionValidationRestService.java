package jfullam.vfabric.jenkins.plugin.rest;

import jfullam.vfabric.rest.appdir.RestProvider;

/**
 * Uses a REST provider to validate connection information to Application Director
 * 
 * @author Jonathan Fullam
 */
public class ConnectionValidationRestService implements ConnectionValidationService {

	private RestProvider restProvider;
	
	@Override
	public void setRestProvider(RestProvider restProvider) {
		this.restProvider = restProvider;
		
	}

	@Override
	public boolean connectionValid() throws ServiceException {
		restProvider.getApplications();
		return true;
	}

}
