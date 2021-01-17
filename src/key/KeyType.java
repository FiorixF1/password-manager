package key;

public enum KeyType {
	ACCESS_CODE,
	ADDRESS,
	CARD,
	CREDENTIALS,
	CRYPTO_WALLET,
	FISCAL_CODE;
	
	@Override
	public String toString() {
		String tmp = this.name().toLowerCase().replace('_', ' ');
		return tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
	}
}
