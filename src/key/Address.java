package key;

import java.util.Map;

public class Address extends ProxyKey {
	public Address(Map<String, String> props) {
		super(props);
	}
	
	public void createProperties() {
		type = KeyType.ADDRESS;
		allowedProperties = new String[]{ "Addressee", "Street Name", "Street Number", "Town", "Province", "Postal Code", "Country" };
		reservedProperties = new String[]{ };
	}
}
