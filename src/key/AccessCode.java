package key;

import java.util.Map;

public class AccessCode extends AbstractKey {
	private static KeyType type = KeyType.ACCESS_CODE;
	private static String[] props = { "Code" };
	
	public AccessCode(Map<String, String> props) {
		super(props);
	}

	public KeyType getType() {
		return type;
	}
	
	public String[] getAllowedProperties() {
		return props;
	}
}
