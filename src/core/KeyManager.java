package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import key.AbstractKey;
import key.KeyFactory;
import key.KeyType;

public class KeyManager implements UserInterface {
	private static KeyManager instance = null;
	private static final String MASTERKEY_FILE = "masterkey.dat";
	private static final String KEYS_FILE = "keys.dat";
	
	private byte[] initVector;
	private String salt;
	private byte[] masterKey;
	
	private Cryptographer crypto;
	private KeyFactory keyFactory;
	private List<AbstractKey> keys;

	private KeyManager() {
		crypto = new Cryptographer();
		keyFactory = new KeyFactory();
		keys = new ArrayList<AbstractKey>();
		try {
			File file = new File(MASTERKEY_FILE);
			BufferedReader br = new BufferedReader(new FileReader(file));
			initVector = Base64.getDecoder().decode(br.readLine());
			salt = br.readLine();
			br.close();
			if (initVector == null || salt == null) {
				initVector = null;
				salt = null;
				masterKey = null;
			}
		} catch (IOException e) {
			initVector = null;
			salt = null;
			masterKey = null;
		}
	}
	
	public static KeyManager getInstance() {
		if (instance == null) {
			instance = new KeyManager();
		}
		return instance;
	}
	
	public boolean isPasswordSet() {
		return initVector != null && salt != null;
	}
	
	public boolean login(String password) {
		masterKey = crypto.createKey(password, salt);
		if (loadKeys()) {
			return true;
		} else {
			masterKey = null;
			keys.clear();
			return false;
		}
	}
	
	public boolean isSecureEnough(String password) {
		boolean length = password.length() >= 8;
		boolean lowercase = false;
		boolean uppercase = false;
		boolean digit = false;
		
		for (int i = 0; i < password.length(); ++i) {
			char ch = password.charAt(i);
			lowercase = lowercase || Character.isLowerCase(ch);
			uppercase = uppercase || Character.isUpperCase(ch);
			digit = digit || Character.isDigit(ch);
		}
		
		return length && lowercase && uppercase && digit;
	}
	
	// set the master key starting from the user defined password
	// it is created as HASH^256(salt + password)
	public void setMasterKey(String password) {
		try {
			initVector = crypto.generateIV();
			salt = crypto.generateSalt();
			masterKey = crypto.createKey(password, salt);
			File file = new File(MASTERKEY_FILE);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(Base64.getEncoder().encodeToString(initVector) + "\n" + salt);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			initVector = null;
			salt = null;
			masterKey = null;
		}
	}
	
	// update the master key and encrypt keys with it
	public boolean updateMasterKey(String oldPassword, String newPassword) {
		// for some reason, comparing arrays directly always returns false, let's use Base64
		String a = Base64.getEncoder().encodeToString(crypto.createKey(oldPassword, salt));
		String b = Base64.getEncoder().encodeToString(masterKey);
		
		if (oldPassword == null || !a.equals(b)) {
			return false;
		} else {
			setMasterKey(newPassword);
			saveKeys();
			return true;
		}
	}
	
	// delete the master key
	public boolean deleteMasterKey() {
		File file = new File(MASTERKEY_FILE);
		if (file.delete()) {
			initVector = null;
			salt = null;
			masterKey = null;
			deleteAllKeys();
			return true;
		} else {
			return false;
		}
	}
	
	public Map<KeyType, String[]> getKeysMetadata() {
		return keyFactory.getKeysMetadata();
	}

	public List<AbstractKey> getKeys() {
		return keys;
	}
	
	public AbstractKey getKeyById(int id) {
		for (AbstractKey key : keys) {
			if (key.getId() == id) {
				return key;
			}
		}
		return null;
	}

	public List<AbstractKey> getKeysByDescription(String query) {
		List<AbstractKey> result = new ArrayList<AbstractKey>();
		
		for (AbstractKey key : keys) {
			if (key.getDescription().toLowerCase().contains(query.toLowerCase())) {
				result.add(key);
			}
		}
		
		return result;
	}
	
	public AbstractKey insertKey(KeyType type, String description, Map<String, String> props) {
		AbstractKey newKey = keyFactory.createKey(type, generateId(), description, props);
		if (newKey != null) {
			keys.add(newKey);
			saveKeys();
		}
		return newKey;
	}
	
	public void updateKey(AbstractKey key, String newDescription, Map<String, String> newProps) {
		key.setProperties(newProps);
		
		if (newDescription != null && newDescription.length() > 0) {
			key.setDescription(newDescription);
		}
		
		saveKeys();
	}
	
	public boolean deleteKey(int id) {
		AbstractKey deleting = getKeyById(id);
		if (deleting != null) {
			keys.remove(deleting);
			saveKeys();
			return true;
		}
		return false;
	}
	
	private boolean deleteAllKeys() {
		File file = new File(KEYS_FILE);
		if (file.delete()) {
			initVector = null;
			salt = null;
			masterKey = null;
			return true;
		} else {
			return false;
		}
	}
	
	// deserialize keys from disk
	private boolean loadKeys() {
		try {
			String encrypted = new String(Files.readAllBytes(Paths.get(KEYS_FILE)), StandardCharsets.UTF_8);
			keys = crypto.decrypt(masterKey, initVector, encrypted);
			return true;
		} catch (IOException | InvalidKeyException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// serialize keys to disk
	// TODO: dovrebbe usare un file temporaneo e servirebbe un sistema di backup in caso di interruzioni moleste del programma
	private void saveKeys() {
		try {
			String encrypted = crypto.encrypt(masterKey, initVector, keys);
			File file = new File(KEYS_FILE);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(encrypted);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int generateId() {
		int id = 0;
		for (AbstractKey key : keys) {
			if (key.getId() >= id) {
				id = key.getId() + 1;
			}
		}
		return id;
	}
}
