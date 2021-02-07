package key;

import java.util.Map;

public abstract class ProxyKey extends AbstractKey {
	protected KeyType type;
	protected String[] allowedProperties;
	protected String[] reservedProperties;
	
	public ProxyKey(Map<String, String> props) {
		super(props);
	}

	@Override
	public KeyType getType() {
		return type;
	}

	@Override
	public String[] getAllowedProperties() {
		return allowedProperties;
	}

	@Override
	public String[] getReservedProperties() {
		return reservedProperties;
	}
}
