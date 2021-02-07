package key;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractKey {
	private int id;
	private String description;
	private LocalDate created;
	private LocalDate modified;
	private Map<String, String> properties;
	
	public static final String NOT_AVAILABLE = "N/A";
	public static final String STARTING_TAG = "START";
	public static final String ENDING_TAG = "END";
	
	// these are implemented in ProxyKey, the link between AbstractKey and the derived classes
	public abstract KeyType getType();
	public abstract String[] getAllowedProperties();
	public abstract String[] getReservedProperties();
	
	// implement this in derived classes by setting properties
	public abstract void createProperties();
	
	AbstractKey(Map<String, String> props) {
		createProperties();
		
		created = modified = LocalDate.now();
		properties = new HashMap<String, String>();
		if (props == null) return;
		for (String key : getAllowedProperties()) {
			String value = props.get(key);
			if (value != null && value.length() > 0) {
				properties.put(key, value);
			} else {
				properties.put(key, NOT_AVAILABLE);
			}
		}
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}
	
	public void setProperty(String key, String value) {
		if (checkProperty(key)) {
			properties.put(key, value);
		}
	}
	
	public void setProperties(Map<String, String> props) {
		modified = LocalDate.now();
		for (String key : props.keySet()) {
			setProperty(key, props.get(key));
		}
	}
	
	public boolean isReserved(String key) {
		for (String prop : getReservedProperties()) {
			if (prop.equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkProperty(String key) {
		for (String prop : getAllowedProperties()) {
			if (prop.equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public LocalDate getCreated() {
		return created;
	}
	
	public void setCreated(LocalDate created) {
		this.created = created;
	}
	
	public LocalDate getModified() {
		return modified;
	}
	
	public void setModified(LocalDate modified) {
		this.modified = modified;
	}
	
	public String getData() {
		StringBuilder output = new StringBuilder();
		for (String key : getAllowedProperties()) {
			output.append(key + ": " + properties.get(key) + "\n");
		}
		return output.toString();
	}
	
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append(STARTING_TAG         + "\n" +  
		              getType().name()     + "\n" +
		              getId()              + "\n" +
		              getCreated()         + "\n" +
				      getModified()        + "\n" +
				      getDescription()     + "\n");
		
		for (String key : getAllowedProperties()) {
			output.append(key + "\n");
			output.append(properties.get(key) + "\n");
		}
		
		output.append(ENDING_TAG);
		return output.toString();
	}
}
