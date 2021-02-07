package key;

import java.util.Map;

public class PaymentCard extends ProxyKey {
	public PaymentCard(Map<String, String> props) {
		super(props);
	}
	
	public void createProperties() {
		type = KeyType.PAYMENT_CARD;
		allowedProperties = new String[]{ "Card Number", "Expiration Date", "Security Code", "Holder Name", "PIN" };
		reservedProperties = new String[]{ "Security Code", "PIN" };
	}
}
