package key;

import java.util.Map;

public class LoginCredentials extends ProxyKey {
	public LoginCredentials(Map<String, String> props) {
		super(props);
	}
	
	public void createProperties() {
		type = KeyType.LOGIN_CREDENTIALS;
		allowedProperties = new String[]{ "Username", "Password" };
		reservedProperties = new String[]{ "Password" };
	}
}
