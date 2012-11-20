package jfullam.vfabric.jenkins.plugin.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class ConfigUpdateRequest {

	ArrayList<ProfileNodeComponent> profileNodeComponents;
	
	public ConfigUpdateRequest() {
		profileNodeComponents = new ArrayList<ConfigUpdateRequest.ProfileNodeComponent>();
	}
	
	public ArrayList<ProfileNodeComponent> getProfileNodeComponents() {
		return profileNodeComponents;
	}

	public void setProfileNodeComponents(
			ArrayList<ProfileNodeComponent> profileNodeComponents) {
		this.profileNodeComponents = profileNodeComponents;
	}
	
	public void addProfileNodeComponentProperties(String name, HashMap<String,String> properties) {
		ProfileNodeComponent nodeComponent = new ProfileNodeComponent();
		nodeComponent.setName(name);
		ArrayList<Property> propertyList = new ArrayList<ConfigUpdateRequest.Property>();
		for (String key : properties.keySet()) {
			propertyList.add(new Property(key, properties.get(key)));
		}
		nodeComponent.setProperty(propertyList);
		profileNodeComponents.add(nodeComponent);
		
	}
	
	
	class ProfileNodeComponent {
		
		String name;
		ArrayList<Property> property;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public ArrayList<Property> getProperty() {
			return property;
		}
		public void setProperty(ArrayList<Property> property) {
			this.property = property;
		}
		
		
	}
	
	class Property {
		
		String key;
		String value;
		
		public Property(String key, String value) {
			setKey(key);
			setValue(value);
		}
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	public static void main (String[] args) {
		ConfigUpdateRequest requestObj = new ConfigUpdateRequest();
		HashMap<String, String> props = new HashMap<String, String>();
		props.put("war_file", "http://blahblahlbah/app.war");
		requestObj.addProfileNodeComponentProperties("spring_travel_wark", props);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(System.out, requestObj);
			System.out.println(mapper.writeValueAsString(requestObj));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
 