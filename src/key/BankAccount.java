package key;

import java.util.Map;

public class BankAccount extends ProxyKey {
	public BankAccount(Map<String, String> props) {
		super(props);
	}
	
	public void createProperties() {
		type = KeyType.BANK_ACCOUNT;
		allowedProperties = new String[]{ "IBAN", "BIC", "Holder Name" };
		reservedProperties = new String[]{ };
	}
}
