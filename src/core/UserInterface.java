package core;

import java.util.List;
import java.util.Map;

import key.AbstractKey;
import key.KeyType;

public interface UserInterface {
	public boolean isPasswordSet();
	public boolean login(String password);
	public boolean isSecureEnough(String password);

	public void setMasterKey(String password);
	public boolean updateMasterKey(String oldPassword, String newPassword);
	public boolean deleteMasterKey();
	
	public Map<KeyType, AbstractKey> getKeysMetadata();
	public List<AbstractKey> getKeys();
	public AbstractKey getKeyById(int id);
	public List<AbstractKey> getKeysByDescription(String query);
	
	public AbstractKey insertKey(KeyType type, String description, Map<String, String> props);
	public void updateKey(AbstractKey key, String newDescription, Map<String, String> newProps);
	public boolean deleteKey(int id);
}
