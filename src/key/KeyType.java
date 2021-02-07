package key;

public enum KeyType {
	ACCESS_CODE,
	ADDRESS,
	BANK_ACCOUNT,
	CRYPTO_WALLET,
	FISCAL_CODE,
	LOGIN_CREDENTIALS,
	PAYMENT_CARD;
	
	@Override
	public String toString() {
		String tmp = this.name().toLowerCase().replace('_', ' ');
		return tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
	}
}
