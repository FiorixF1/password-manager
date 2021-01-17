package key;

import java.util.Map;

public class Card extends AbstractKey {
	private static KeyType type = KeyType.CARD;
	private static String[] props = { "Card Number", "Expiration Date", "Security Code", "Holder Name", "PIN" };
	
	public Card(Map<String, String> props) {
		super(props);
	}

	public KeyType getType() {
		return type;
	}

	public String[] getAllowedProperties() {
		return props;
	}
}
