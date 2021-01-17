package core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import key.AbstractKey;

public class Cryptographer {
	private static final int KEY_LENGTH = 16;
	private static final boolean INPUT_NOT_ENCRYPTED = false;
	private static final boolean OUTPUT_NOT_ENCRYPTED = false;
	
	private Parser parser;
	
	public Cryptographer() { 
		this.parser = new Parser();
	}
	
	// create a new key as HASH^256(salt + password)
	public byte[] createKey(String password, String salt) {
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    byte[] hash = (salt + password).getBytes(StandardCharsets.UTF_8);
		    for (int i = 0; i < 256; ++i)
		    	hash = digest.digest(hash);
		    return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// generate an array of 16 random bytes to be used as IV
	public byte[] generateIV() {
		SecureRandom random = new SecureRandom();
		byte[] initVector = new byte[KEY_LENGTH];
		random.nextBytes(initVector);
		return initVector;
	}
	
	// generate a random string of 8 chars to be used as salt
	public String generateSalt() {
		Random random = new Random();
		String alphabet = "1234567890QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";
		String salt = "";
		for (int i = 0; i < 8; ++i)
			salt += alphabet.charAt(random.nextInt(alphabet.length()));
		return salt;
	}
	
	// encrypt data with AES-128 using an IV and a key
	// the key must be 128 bits long (so far it is 256 bits long and half of them are discarded)
	public String encrypt(byte[] key, byte[] iv, List<AbstractKey> keys) {
        try {
        	if (OUTPUT_NOT_ENCRYPTED) return parser.serializeKeys(keys);
        	
			byte[] data = compress(parser.serializeKeys(keys));

			byte[] subKey = new byte[KEY_LENGTH];
			for (int i = 0; i < KEY_LENGTH; ++i)
				subKey[i] = key[i];
			
			data = realEncrypt(data, subKey, iv);
			return Base64.getEncoder().encodeToString(data);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
	
	// execute the real encryption
	private byte[] realEncrypt(byte[] data, byte[] key, byte[] initVector) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector);
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(data);
			return encrypted;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new byte[KEY_LENGTH];
		}
	}
	
	// compress data with GZIP
	private byte[] compress(String data) {
		try {
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        GZIPOutputStream gzip = new GZIPOutputStream(bos);
	        gzip.write(data.getBytes("UTF-8"));
	        gzip.close();
	        return bos.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// decrypt data with AES-128 using an IV and a key
	// the key must be 128 bits long (so far it is 256 bits long and half of them are discarded)
	public List<AbstractKey> decrypt(byte[] key, byte[] iv, String encrypted) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
    	if (encrypted == null) {
    		return new ArrayList<AbstractKey>();
    	}
    	
        if (INPUT_NOT_ENCRYPTED) return parser.deserializeKeys(encrypted);
        	
        byte[] data = Base64.getDecoder().decode(encrypted);
        	
		byte[] subKey = new byte[KEY_LENGTH];
		for (int i = 0; i < KEY_LENGTH; ++i)
			subKey[i] = key[i];
		
		data = realDecrypt(data, subKey, iv);
        return parser.deserializeKeys(decompress(data));
    }

	// execute the real decryption
	private byte[] realDecrypt(byte[] data, byte[] key, byte[] initVector) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		IvParameterSpec iv = new IvParameterSpec(initVector);
	    SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
	
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	    cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	
	    byte[] decrypted = cipher.doFinal(data);
		return decrypted;
	}
	
	// decompress data with GZIP
	private String decompress(byte[] data) {
		try {
	        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
	        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
	        StringBuilder sb = new StringBuilder();
	        String line;
	        while ((line = bf.readLine()) != null) {
	            sb.append(line);
	            sb.append('\n');
	        }
	        return sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}
}
