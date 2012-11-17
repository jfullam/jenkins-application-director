package jfullam.vfabric.rest.appdir;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Jonathan Fullam
 */
public class DefaultApplicationDirectorRestProvider implements ApplicationDirectorRestProvider {

	private String baseApi;
	private String user;
	private String password;
	
	private DefaultHttpClient httpClient;
	
	private static final Log log = LogFactory.getLog(DefaultApplicationDirectorRestProvider.class);

	private static final String DEPLOYMENT_API = "/deployments/page/0/page-size/10";
	private static final String TEARDOWN_API = "/deployment/{deploymentid}/action/teardown/";
	private static final String SCHEDULE_DEPLOYMENT_API = "/deployment-profile/{deployment-profileid}/action/deploy";
	private static final String APPLICATOIONS_API = "/applications/page/{page}/page-size/{pagesize}";
	private static final String DEPLOYMENT_PROFILES_API = "/deployment-profiles/{application-versionid}/page/{page}/page-size/{page-size}";
	private static final String DEPLOY_PROPS_API = "/deployment-profile/{deployment-profileid}/deploy-props";

	public DefaultApplicationDirectorRestProvider(String baseApi, String user, String password) throws KeyManagementException, NoSuchAlgorithmException {
		this.baseApi = baseApi;
		this.user = user;
		this.password = password;
		
		initializeHttpClient();
	}


	/* (non-Javadoc)
	 * @see com.vmware.demo.vfabric.management.appdirector.ApplicationDirectorRestAPI#getDeployments()
	 */
	public JsonNode getDeployments() {

		String deploymentsUri = baseApi + DEPLOYMENT_API;
		log.debug("Calling " + deploymentsUri);

		HttpGet get = new HttpGet(deploymentsUri);
		get.setHeader("Accept", "application/json");
		
		HttpResponse response;
		
		try {
			response = httpClient.execute(get);
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				ObjectMapper jsonObjectMapper = new ObjectMapper();
				return jsonObjectMapper.readTree(new InputStreamReader(entity.getContent()));
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}

	/* (non-Javadoc)
	 * @see com.vmware.demo.vfabric.management.appdirector.ApplicationDirectorRestAPI#postTeardown(java.lang.String)
	 */
	public JsonNode postTeardown(String deploymentId) {

		String teardownUri = baseApi + TEARDOWN_API;
		log.debug("Calling " + teardownUri);
		return null;
	}

	/* (non-Javadoc)
	 * @see com.vmware.demo.vfabric.management.appdirector.ApplicationDirectorRestAPI#postScheduleDeployment(java.lang.String, org.codehaus.jackson.JsonNode)
	 */
	public JsonNode postScheduleDeployment(String deployemtProfileId,
			JsonNode deployRequest) {

		String deployUri = baseApi + SCHEDULE_DEPLOYMENT_API;
		log.debug("Calling " + deployUri);
		return null;

	}
	
	public JsonNode getApplications() {

		String appsUri = baseApi + APPLICATOIONS_API;
		log.debug("Calling " + appsUri);
		return null;
	}
	
	public JsonNode getDeployProperties(String deploymentProfileId) {
		
		String deployPropsUri = baseApi + DEPLOY_PROPS_API;
		log.debug("Calling " + deployPropsUri);
		return null;
	}
	

	public JsonNode getDeploymentProfiles(String applicationId) {
		
		String profilesUri = baseApi + DEPLOYMENT_PROFILES_API;
		log.debug("Calling " + profilesUri);
		return null;
	}
	
	
	private void initializeHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
		httpClient = new DefaultHttpClient();	
		configureClientForAuthentication();
		configureClientToIgnoreSSLCertificate();
	}

	private void configureClientToIgnoreSSLCertificate()
			throws NoSuchAlgorithmException, KeyManagementException {
		
		X509TrustManager tm = new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] xcs,
					String string) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] xcs,
					String string) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		TrustManager[] trustManagers = {tm};

		final SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustManagers, null);

		SSLSocketFactory ssf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        ClientConnectionManager connectionManager = httpClient.getConnectionManager();     
        Scheme https = new Scheme("https", 8443, ssf);
        connectionManager.getSchemeRegistry().register(https);
	}

	private void configureClientForAuthentication() {
		httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(user, password));
	}
	
	
	public static void main(String[] args) {
		try {
			DefaultApplicationDirectorRestProvider provider = 
				new DefaultApplicationDirectorRestProvider("https://10.64.81.24:8443/darwin/api/1.0", "admin", "Passw0rd");
			System.out.println(provider.getDeployments());
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void setUser(String user) {
		this.user = user;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public void setHttpClient(DefaultHttpClient httpClient) {
		this.httpClient = httpClient;
	}



}
