package jfullam.vfabric.jenkins.plugin.rest;

import jfullam.vfabric.rest.appdir.RestProvider;

/**
 * Validates a connection to Application Director
 * 
 * @author Jonathan Fullam
 */
public interface ConnectionValidationService {
	
	void setRestProvider(RestProvider restProvider);

	/**
	 * @return boolean - whether the connection information is valid for 
	 * the set RestProvider.
	 * @throws ServiceException
	 */
	boolean connectionValid() throws ServiceException;

}
