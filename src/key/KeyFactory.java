package key;

import java.util.HashMap;
import java.util.Map;

public class KeyFactory {
	
	public AbstractKey createKey(KeyType type, int id, String description, Map<String, String> props) {
		AbstractKey key = null;
		
		switch (type) {
		case ACCESS_CODE:
			key = new AccessCode(props);
			break;
		case ADDRESS:
			key = new Address(props);
			break;
		case BANK_ACCOUNT:
			key = new BankAccount(props);
			break;
		case CRYPTO_WALLET:
			key = new CryptoWallet(props);
			break;
		case FISCAL_CODE:
			key = new FiscalCode(props);
			break;
		case LOGIN_CREDENTIALS:
			key = new LoginCredentials(props);
			break;
		case PAYMENT_CARD:
			key = new PaymentCard(props);
			break;
		default:
			key = null;
		}
			
		key.setId(id);
		key.setDescription(description);
		
		return key;
	}
	
	public Map<KeyType, AbstractKey> getKeysMetadata() {
		Map<KeyType, AbstractKey> result = new HashMap<KeyType, AbstractKey>();
		
		result.put(KeyType.ACCESS_CODE, new AccessCode(null));
		result.put(KeyType.ADDRESS, new Address(null));
		result.put(KeyType.BANK_ACCOUNT, new BankAccount(null));
		result.put(KeyType.CRYPTO_WALLET, new CryptoWallet(null));
		result.put(KeyType.FISCAL_CODE, new FiscalCode(null));
		result.put(KeyType.LOGIN_CREDENTIALS, new LoginCredentials(null));
		result.put(KeyType.PAYMENT_CARD, new PaymentCard(null));
		
		return result;
	}
}
