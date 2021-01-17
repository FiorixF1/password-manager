package key;

import java.util.Map;

public class Address extends AbstractKey {
	private static KeyType type = KeyType.ADDRESS;
	private static String[] props = { "Name", "Surname", "Road", "Civic Number", "City", "Province", "Zip Code", "Country" };
	
	public Address(Map<String, String> props) {
		super(props);
	}

	public KeyType getType() {
		return type;
	}
	
	public String[] getAllowedProperties() {
		return props;
	}
}
