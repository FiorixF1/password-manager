package key;

import java.util.Map;

public class FiscalCode extends ProxyKey {
	public FiscalCode(Map<String, String> props) {
		super(props);
	}
	
	public void createProperties() {
		type = KeyType.FISCAL_CODE;
		allowedProperties = new String[]{ "Fiscal Code" };
		reservedProperties = new String[]{ };
	}
}
