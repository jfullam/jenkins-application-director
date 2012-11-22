package jfullam.vfabric.rest.appdir;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jfullam.vfabric.jenkins.plugin.rest.ServiceException;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Uses Apache HttpClient to call the Application Director REST API
 * 
 * @author Jonathan Fullam
 */
public class DefaultRestProvider implements RestProvider {

	private String baseApi;
	private String user;
	private String password;
	
	private DefaultHttpClient httpClient;
	
	private static final Log log = LogFactory.getLog(DefaultRestProvider.class);
	
	private static final String PAGE = "0";
	private static final String PAGE_SIZE = "100";

	private static final String DEPLOYMENT_API = "/deployments/page/${page}/page-size/${pagesize}";
	private static final String TEARDOWN_API = "/deployment/${deploymentid}/action/teardown/";
	private static final String SCHEDULE_DEPLOYMENT_API = "/deployment-profile/${deployment-profileid}/action/deploy";
	private static final String APPLICATIONS_API = "/applications/page/${page}/page-size/${pagesize}";
	private static final String DEPLOYMENT_PROFILES_API = "/deployment-profiles/${application-versionid}/page/${page}/page-size/${pagesize}";
	private static final String DEPLOY_PROPS_API = "/deployment-profile/${deployment-profileid}/deploy-props";
	private static final String UPDATE_PROPS_API = "/deployment/${deploymentid}/config-update-props";
	private static final String UPDATE_DEPLOYMENT_API =  "/deployment/${deploymentid}/action/config-update";

	public DefaultRestProvider(String baseApi, String user, String password) throws KeyManagementException, NoSuchAlgorithmException {
		this.baseApi = baseApi;
		this.user = user;
		this.password = password;
		
		initializeHttpClient();
	}


	/* (non-Javadoc)
	 * @see com.vmware.demo.vfabric.management.appdirector.ApplicationDirectorRestAPI#getDeployments()
	 */
	public JsonNode getDeployments() throws ServiceException {
		
		HashMap<String,String> pathValueMap = new HashMap<String,String>();
		pathValueMap.put("page", PAGE);
		pathValueMap.put("pagesize", PAGE_SIZE);
		StrSubstitutor pathResolver = new StrSubstitutor(pathValueMap);
		
		String deploymentsUri = baseApi + pathResolver.replace(DEPLOYMENT_API);

		return doGet(deploymentsUri);

		
	}
	
	/* (non-Javadoc)
	 * @see com.vmware.demo.vfabric.management.appdirector.ApplicationDirectorRestAPI#postTeardown(java.lang.String)
	 */
	public JsonNode postTeardown(String deploymentId) throws ServiceException {
		
		HashMap<String,String> pathValueMap = new HashMap<String,String>();
		pathValueMap.put("deploymentid", deploymentId);
		StrSubstitutor pathResolver = new StrSubstitutor(pathValueMap);
		
		String teardownUri = baseApi + pathResolver.replace(TEARDOWN_API);

		return doPost(teardownUri, null);
	}

	/* (non-Javadoc)
	 * @see com.vmware.demo.vfabric.management.appdirector.ApplicationDirectorRestAPI#postScheduleDeployment(java.lang.String, org.codehaus.jackson.JsonNode)
	 */
	public JsonNode postScheduleDeployment(String deploymentProfileId,
			JsonNode deployRequest) throws ServiceException {
		
		HashMap<String,String> pathValueMap = new HashMap<String,String>();
		pathValueMap.put("deployment-profileid", deploymentProfileId);
		StrSubstitutor pathResolver = new StrSubstitutor(pathValueMap);
		
		String deployUri = baseApi + pathResolver.replace(SCHEDULE_DEPLOYMENT_API);
	
		return doPost(deployUri, deployRequest);

	}
	
	public JsonNode getApplications() throws ServiceException {
		
		HashMap<String,String> pathValueMap = new HashMap<String,String>();
		pathValueMap.put("page", PAGE);
		pathValueMap.put("pagesize", PAGE_SIZE);
		StrSubstitutor pathResolver = new StrSubstitutor(pathValueMap);
		
		String appsUri = baseApi + pathResolver.replace(APPLICATIONS_API);
		
		return doGet(appsUri);
	}
	
	public JsonNode getDeployProperties(String deploymentProfileId) throws ServiceException {
		
		HashMap<String,String> pathValueMap = new HashMap<String,String>();
		pathValueMap.put("deployment-profileid", deploymentProfileId);
		StrSubstitutor pathResolver = new StrSubstitutor(pathValueMap);
		
		String deployPropsUri = baseApi + pathResolver.replace(DEPLOY_PROPS_API);

		return doGet(deployPropsUri);
	}
	

	public JsonNode getDeploymentProfiles(String applicationId) throws ServiceException {
		
		HashMap<String,String> pathValueMap = new HashMap<String,String>();
		pathValueMap.put("page", PAGE);
		pathValueMap.put("pagesize", PAGE_SIZE);
		pathValueMap.put("application-versionid", applicationId);
		StrSubstitutor pathResolver = new StrSubstitutor(pathValueMap);		
		
		String profilesUri = baseApi + pathResolver.replace(DEPLOYMENT_PROFILES_API);

		return doGet(profilesUri);
	}	
	

	@Override
	public JsonNode getDeploymentUpdateProperties(String deploymentId) throws ServiceException {
		HashMap<String,String> pathValueMap = new HashMap<String,String>();
		pathValueMap.put("deploymentid", deploymentId);
		StrSubstitutor pathResolver = new StrSubstitutor(pathValueMap);
		
		String updatePropsUri = baseApi + pathResolver.replace(UPDATE_PROPS_API);

		return doGet(updatePropsUri);
	}
	
	@Override
	public JsonNode updateDeployment(String deployment, JsonNode updateRequest) throws ServiceException {
		HashMap<String,String> pathValueMap = new HashMap<String,String>();
		pathValueMap.put("deploymentid", deployment);
		StrSubstitutor pathResolver = new StrSubstitutor(pathValueMap);
		
		String deployUri = baseApi + pathResolver.replace(UPDATE_DEPLOYMENT_API);
	
		return doPost(deployUri, updateRequest);
	}
	
	
	public static void main(String[] args) {
		try {
			DefaultRestProvider provider = 
				new DefaultRestProvider("https://10.64.81.24:8443/darwin/api/1.0", "admin", "Passw0rd");
			System.out.println(provider.getDeploymentUpdateProperties("152"));
			System.out.println(provider.getApplications());
		} catch (Throwable e) {
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
	
	private JsonNode doGet(String uri) throws ServiceException {
		log.debug("Calling " +  uri);
		HttpGet get = new HttpGet(uri);
		get.setHeader("Accept", "application/json");
		
		return executeHttpMethod(get);
	}
	
	private JsonNode doPost(String uri, JsonNode postJson) throws ServiceException {
		log.debug("Calling " + uri + " with entity " + postJson);
		HttpPost post = new HttpPost(uri);
		
		if (postJson != null) {
			HttpEntity postEntity = new StringEntity(postJson.toString(), ContentType.APPLICATION_JSON);
			post.setEntity(postEntity);
		}
		
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-Type", "application/json");
		
		return executeHttpMethod(post);
	}


	private JsonNode executeHttpMethod(HttpUriRequest httpUriRequest) throws ServiceException {
		HttpResponse response = null;
		
		try {
			response = httpClient.execute(httpUriRequest);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					ObjectMapper jsonObjectMapper = new ObjectMapper();
					JsonNode jsonResponseEntity =  jsonObjectMapper.readTree(new InputStreamReader(entity.getContent()));
					return jsonResponseEntity;
				}
			} else {
				throw new ServiceException("REST called returned a response code of " +
						response.getStatusLine().getStatusCode());
			}
		} catch (Throwable t) {
			throw new ServiceException(t);
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private void initializeHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
		  	httpClient = new DefaultHttpClient();
		    HttpParams params = httpClient.getParams();

		    httpClient = new DefaultHttpClient(
		        new ThreadSafeClientConnManager(), params);
		configureClientForAuthentication();
		configureClientToIgnoreSSLCertificate();
	}

	/*
	 * Configure the HttpClient to accept the SSL certificate and
	 * allow any hostname to be present in that certificate.
	 */
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
}
