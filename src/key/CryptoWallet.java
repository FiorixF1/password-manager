package key;

import java.util.Map;

public class CryptoWallet extends ProxyKey {
	public CryptoWallet(Map<String, String> props) {
		super(props);
	}
	
	public void createProperties() {
		type = KeyType.CRYPTO_WALLET;
		allowedProperties = new String[]{ "Address", "Private Key" };
		reservedProperties = new String[]{ "Private Key" };
	}
}
