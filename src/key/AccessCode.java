package key;

import java.util.Map;

public class AccessCode extends ProxyKey {
	public AccessCode(Map<String, String> props) {
		super(props);
	}
	
	public void createProperties() {
		type = KeyType.ACCESS_CODE;
		allowedProperties = new String[]{ "Code" };
		reservedProperties = new String[]{ "Code" };
	}
}
