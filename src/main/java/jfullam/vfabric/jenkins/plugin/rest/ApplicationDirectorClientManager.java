package jfullam.vfabric.jenkins.plugin.rest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import jfullam.vfabric.rest.appdir.ApplicationDirectorRestProvider;
import jfullam.vfabric.rest.appdir.DefaultApplicationDirectorRestProvider;

/**
 * @author Jonathan Fullam
 */
public class ApplicationDirectorClientManager {
	
	private static ApplicationDirectorClient applicationDirectorClient;
	private static ApplicationDirectorRestProvider appDirRestProvider;

	public static void configureApplicationDirectorClient(String appDirBaseURI,
			String userName, String password) {
		
		try {
			initializeRestProvider(appDirBaseURI, userName, password);
		} catch (Throwable t) {
			throw new ApplicationDirectorRestRuntimeException(t);
		} 
		
	}

	private static synchronized void initializeRestProvider(String appDirBaseURI,
			String userName, String password) throws KeyManagementException, NoSuchAlgorithmException {
		
		if (appDirRestProvider == null) {
			appDirRestProvider = new DefaultApplicationDirectorRestProvider(appDirBaseURI, userName, password);
		}
	}

}
