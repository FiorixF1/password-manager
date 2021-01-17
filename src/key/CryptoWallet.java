package key;

import java.util.Map;

public class CryptoWallet extends AbstractKey {
	private static KeyType type = KeyType.CRYPTO_WALLET;
	private static String[] props = { "Address", "Private Key", "Seed" };
	
	public CryptoWallet(Map<String, String> props) {
		super(props);
	}

	public KeyType getType() {
		return type;
	}
	
	public String[] getAllowedProperties() {
		return props;
	}
}
