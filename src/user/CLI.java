package user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import core.KeyManager;
import core.UserInterface;
import key.AbstractKey;
import key.KeyType;

public class CLI {
	private static Scanner cin = new Scanner(System.in);
	private static UserInterface ui = KeyManager.getInstance();
	
	private static Map<KeyType, String[]> keysMetadata;
	private static KeyType[] types;
	
	public static void main(String[] args) {
		if (ui.isPasswordSet()) {
			askPassword();
		} else {
			setPassword();
		}
		
		startInterface();
	}
	
	public static void askPassword() {
		int tries = 10;
		do {
			String password = readPassword("Enter your password (" + tries + " attempts left): ");
			if (ui.login(password)) {
				System.out.println("");
				return;
			} else {
				--tries;
			}
		} while (tries > 0);
		
		ui.deleteMasterKey();
		System.out.println("Password and keys deleted!!!\n");
		setPassword();
	}
	
	public static void setPassword() {
		String firstPassword = readPassword("Set your password: ");
		
		if (ui.isSecureEnough(firstPassword)) {
			String secondPassword = readPassword("Confirm your password: ");
			if (firstPassword.equals(secondPassword)) {
				ui.setMasterKey(firstPassword);
			} else {
				System.out.println("The passwords don't match!");
				setPassword();
			}
		} else {
			System.out.println( "The chosen password is not secure enough. it must respect these constraints:" + "\n" +
								"- Be at least 8 characters long" + "\n" +
								"- Contain lowercase letters" + "\n" +
								"- Contain uppercase letters" + "\n" +
								"- Contain digits");
			setPassword();
		}
	}
	
	public static void startInterface() {
		// at first load the supported key types
		keysMetadata = ui.getKeysMetadata();
		types = new KeyType[keysMetadata.keySet().size()];
		int i = 0;
		for (KeyType type : keysMetadata.keySet()) {
			types[i] = type;
			++i;
		}
		
		System.out.println("----------[ Welcome! ]----------");
		
		while (true) {
			System.out.print( "\n1 - Show all keys"   + "      " +
								"2 - Search key"      + "      " +
								"3 - Insert key"      + "      " +
								"4 - Update key"      + "      " +
								"5 - Delete key"      + "      " +
								"6 - Edit password"   + "      " +
								"7 - Exit"            + "\n"     +
								"> ");
			
			int option = 0;
			do {
				option = cin.nextInt();
				cin.nextLine();
			} while (option < 1 && option > 7);
			
			switch (option) {
			case 1:
				printKeys();
				break;
			case 2:
				searchKey();
				break;
			case 3:
				insertKey();
				break;
			case 4:
				updateKey();
				break;
			case 5:
				deleteKey();
				break;
			case 6:
				editPassword();
				break;
			case 7:
				System.exit(0);
				break;
			}
		}
	}
	
	public static void printKeys() {
		List<AbstractKey> keys = ui.getKeys();
		if (keys.size() == 0) {
			System.out.println("There are currently no stored keys.");
		} else {
			for (AbstractKey k : keys) {
				printKey(k);
			}
		}
	}
	
	public static void printKey(AbstractKey key) {
		System.out.println(key.getId() + "\n" +
						   "Created: " + key.getCreated()         + "\n" +
						   "Last modified: " + key.getModified()  + "\n" +
						   "Description: " + key.getDescription() + "\n" +
						   key.getData()
						   );
	}
	
	public static void searchKey() {
		String query = "";
		do {
			System.out.print("Type a description to search: ");
			query = cin.nextLine();
		} while (query.length() == 0);
		
		List<AbstractKey> result = ui.getKeysByDescription(query);
		if (result.size() == 0) {
			System.out.println("The search returned no keys.");
		} else {
			for (AbstractKey key : result) {
				printKey(key);
			}
		}
	}
	
	public static void insertKey() {		
		for (int i = 1; i <= types.length; ++i) {
			System.out.println(i + " - " + types[i-1].toString());
		}
		System.out.print("Select type of key: ");
		
		int option = 0;
		do {
			option = cin.nextInt();
			cin.nextLine();
		} while (option < 1 || option > types.length);
		
		System.out.print("Insert a description for your key: ");
		String description = cin.nextLine();
		
		KeyType type = types[option-1];
		String[] requestedInput = keysMetadata.get(type);
		Map<String, String> props = new HashMap<String, String>();
		for (String prop : requestedInput) {
			System.out.print(prop + ": ");
			props.put(prop, cin.nextLine());
		}
		
		System.out.println("Saving...");
		ui.insertKey(type, description, props);
		
		try {
			// add suspance
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done!");
	}
	
	public static void updateKey() {
		int id = -1;
		do {
			System.out.print("Type an ID to search: ");
			id = cin.nextInt();
			cin.nextLine();
		} while (id < 0);
		
		AbstractKey result = ui.getKeyById(id);
		if (result == null) {
			System.out.println("Key not found!");
		} else {
			printKey(result);
			System.out.print("Are you sure you want to update this key (y/n)? ");
			String ans = null;
			do {
				ans = cin.nextLine();
			} while (!ans.equals("y") && !ans.equals("n") && !ans.equals("Y") && !ans.equals("N"));
			if (ans.equals("y") || ans.equals("Y")) {
				System.out.println("Update your key, press Enter to keep a value unmodified");
				
				System.out.print("Insert a description for your key: ");
				String description = cin.nextLine();
				
				KeyType type = result.getType();
				String[] requestedInput = keysMetadata.get(type);
				Map<String, String> props = new HashMap<String, String>();
				for (String prop : requestedInput) {
					System.out.print(prop + ": ");
					String value = cin.nextLine();
					if (value.length() > 0) {
						props.put(prop, value);
					}
				}
				
				System.out.println("Saving...");
				ui.updateKey(result, description, props);
				
				try {
					// add suspance
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Done!");
			}
		}
	}
	
	public static void deleteKey() {
		int id = -1;
		do {
			System.out.print("Type an ID to search: ");
			id = cin.nextInt();
			cin.nextLine();
		} while (id < 0);
		
		AbstractKey result = ui.getKeyById(id);
		if (result == null) {
			System.out.println("Key not found!");
		} else {
			printKey(result);
			System.out.print("Are you sure you want to delete this key (y/n)? ");
			String ans = null;
			do {
				ans = cin.nextLine();
			} while (!ans.equals("y") && !ans.equals("n") && !ans.equals("Y") && !ans.equals("N"));
			if (ans.equals("y") || ans.equals("Y")) {
				ui.deleteKey(id);
				System.out.println("Key deleted");
			}
		}
	}

	public static void editPassword() {
		String oldPassword = readPassword("Enter your previous password: ");
		
		String firstPassword = readPassword("Set your new password: ");
		
		if (ui.isSecureEnough(firstPassword)) {
			String secondPassword = readPassword("Confirm your password: ");
			if (firstPassword.equals(secondPassword)) {
				if (ui.updateMasterKey(oldPassword, firstPassword)) {
					System.out.println("Password changed!");
				} else {
					System.out.println("The password you entered is wrong!");
				}
			} else {
				System.out.println("The passwords don't match!");
			}
		} else {
			System.out.println( "The chosen password is not secure enough. it must respect these constraints:" + "\n" +
								"- Be at least 8 characters long" + "\n" +
								"- Contain lowercase letters" + "\n" +
								"- Contain uppercase letters" + "\n" +
								"- Contain digits");
		}
	}
	
	private static String readPassword(String prompt) {
		EraserThread et = new EraserThread(prompt);
		Thread mask = new Thread(et);
		mask.start();

		String password = cin.nextLine();
		
		et.stopMasking();
		return password;
	}
}
