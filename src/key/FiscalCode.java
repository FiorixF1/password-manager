package key;

import java.util.Map;

public class FiscalCode extends AbstractKey {
	private static KeyType type = KeyType.FISCAL_CODE;
	private static String[] props = { "Fiscal Code" };
	
	public FiscalCode(Map<String, String> props) {
		super(props);
	}

	public KeyType getType() {
		return type;
	}
	
	public String[] getAllowedProperties() {
		return props;
	}
}
