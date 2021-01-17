package key;

import java.util.HashMap;
import java.util.Map;

public class KeyFactory {
	
	public AbstractKey createKey(KeyType type, int id, String description, Map<String, String> props) {
		AbstractKey key = null;
		
		switch (type) {
		case CARD:
			key = new Card(props);
			break;
		case CRYPTO_WALLET:
			key = new CryptoWallet(props);
			break;
		case FISCAL_CODE:
			key = new FiscalCode(props);
			break;
		case ADDRESS:
			key = new Address(props);
			break;
		case ACCESS_CODE:
			key = new AccessCode(props);
			break;
		case CREDENTIALS:
			key = new Credentials(props);
			break;
		default:
			key = null;
		}
			
		key.setId(id);
		key.setDescription(description);
		
		return key;
	}
	
	public Map<KeyType, String[]> getKeysMetadata() {
		Map<KeyType, String[]> result = new HashMap<KeyType, String[]>();
		
		result.put(KeyType.CARD, new Card(null).getAllowedProperties());
		result.put(KeyType.CRYPTO_WALLET, new CryptoWallet(null).getAllowedProperties());
		result.put(KeyType.FISCAL_CODE, new FiscalCode(null).getAllowedProperties());
		result.put(KeyType.ADDRESS, new Address(null).getAllowedProperties());
		result.put(KeyType.ACCESS_CODE, new AccessCode(null).getAllowedProperties());
		result.put(KeyType.CREDENTIALS, new Credentials(null).getAllowedProperties());
		
		return result;
	}
}
