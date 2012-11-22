package jfullam.vfabric.jenkins.plugin.rest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import jfullam.vfabric.rest.appdir.RestProvider;
import jfullam.vfabric.rest.appdir.DefaultRestProvider;

/**
 * Performs intitialization of and access to the various Application Director
 * service interfaces.
 * 
 * @author Jonathan Fullam
 */
public class ServiceManager {
	
	private static ProvisioningService provisioningService;
	private static RestProvider appDirRestProvider;
	private static UserInterfaceDataService userInterfaceDataService;
	private static UpdateService updateService;
	private static ConnectionValidationService connectionValidationService;

	/**
	 * Configures REST communication to Application Director and initializes
	 * all services.
	 * 
	 * @param appDirBaseURI
	 * @param userName
	 * @param password
	 * @throws ServiceException
	 */
	public static void configureApplicationDirectorClient(String appDirBaseURI,
			String userName, String password) throws ServiceException {
		
		try {
			initializeRestProvider(appDirBaseURI, userName, password);
			initializeProvisioningService();
			initializeUserInterfaceService();
			initializeUpdateService();
			initializeConnectionValidationService();
		} catch (Throwable t) {
			throw new ServiceException(t);
		} 
		
	}

	/**
	 * @return ProvisioningService
	 */
	public static ProvisioningService provisioningService() {
		return provisioningService;
	}
	
	/**
	 * @return UserInterfaceDataService
	 */
	public static UserInterfaceDataService userInterfaceService() {
		return userInterfaceDataService;
	}


	/**
	 * @return UpdateService
	 */
	public static UpdateService updateService() {
		return updateService;
	}

	/**
	 * @return ConnectionValidationService
	 */
	public static ConnectionValidationService connectionValidationService() {
		return connectionValidationService;
	}
	

	private static synchronized void initializeProvisioningService() {
		if (provisioningService == null) {
			provisioningService = new ProvisioningRestService();
		}
		provisioningService.setRestProvider(appDirRestProvider);
	}

	private static void initializeRestProvider(String appDirBaseURI,
			String userName, String password) throws KeyManagementException, NoSuchAlgorithmException {
		
			appDirRestProvider = new DefaultRestProvider(appDirBaseURI, userName, password);

	}
	
	
	private static synchronized void initializeConnectionValidationService() {
		if (connectionValidationService == null) {
			connectionValidationService = new ConnectionValidationRestService();
		}
		connectionValidationService.setRestProvider(appDirRestProvider);
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

}
