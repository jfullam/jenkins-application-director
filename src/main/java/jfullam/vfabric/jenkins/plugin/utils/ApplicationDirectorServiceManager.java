package jfullam.vfabric.jenkins.plugin.utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import jfullam.vfabric.jenkins.plugin.rest.ProvisioningService;
import jfullam.vfabric.jenkins.plugin.rest.ProvisioningRestService;
import jfullam.vfabric.jenkins.plugin.rest.ApplicationDirectorRestRuntimeException;
import jfullam.vfabric.jenkins.plugin.rest.UpdateRestService;
import jfullam.vfabric.jenkins.plugin.rest.UpdateService;
import jfullam.vfabric.jenkins.plugin.rest.UserInterfaceDataRestService;
import jfullam.vfabric.jenkins.plugin.rest.UserInterfaceDataService;
import jfullam.vfabric.rest.appdir.ApplicationDirectorRestProvider;
import jfullam.vfabric.rest.appdir.DefaultApplicationDirectorRestProvider;

/**
 * @author Jonathan Fullam
 */
public class ApplicationDirectorServiceManager {
	
	private static ProvisioningService provisioningService;
	private static ApplicationDirectorRestProvider appDirRestProvider;
	private static UserInterfaceDataService userInterfaceDataService;
	private static UpdateService updateService;

	public static void configureApplicationDirectorClient(String appDirBaseURI,
			String userName, String password) {
		
		try {
			initializeRestProvider(appDirBaseURI, userName, password);
			initializeProvisioningService();
			initializeUserInterfaceService();
			initializeUpdateService();
		} catch (Throwable t) {
			throw new ApplicationDirectorRestRuntimeException(t);
		} 
		
	}
	
	private static synchronized void initializeUpdateService() {
		if (updateService == null) {
			updateService = new UpdateRestService();
		}
		updateService.setRestProvider(appDirRestProvider);
	}

	private static synchronized void initializeUserInterfaceService() {
		if (userInterfaceDataService == null) {
			userInterfaceDataService = new UserInterfaceDataRestService();
		}
		userInterfaceDataService.setRestProvider(appDirRestProvider);
	}

	public static ProvisioningService provisioningService() {
		return provisioningService;
	}
	
	public static UserInterfaceDataService userInterfaceService() {
		return userInterfaceDataService;
	}

	private static synchronized void initializeProvisioningService() {
		if (provisioningService == null) {
			provisioningService = new ProvisioningRestService();
		}
		provisioningService.setRestProvider(appDirRestProvider);
	}

	private static void initializeRestProvider(String appDirBaseURI,
			String userName, String password) throws KeyManagementException, NoSuchAlgorithmException {
		
			appDirRestProvider = new DefaultApplicationDirectorRestProvider(appDirBaseURI, userName, password);

	}

	public static UpdateService updateService() {
		return updateService;
	}

}
