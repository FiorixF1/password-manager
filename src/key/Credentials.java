package key;

import java.util.Map;

public class Credentials extends AbstractKey {
	private static KeyType type = KeyType.CREDENTIALS;
	private static String[] props = { "Username", "Password" };
	
	public Credentials(Map<String, String> props) {
		super(props);
	}

	public KeyType getType() {
		return type;
	}
	
	public String[] getAllowedProperties() {
		return props;
	}
}
