package core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import key.AbstractKey;
import key.KeyFactory;
import key.KeyType;

public class Parser {
	private KeyFactory keyFactory;
	
	public Parser() {
		this.keyFactory = new KeyFactory();
	}
	
	public String serializeKeys(List<AbstractKey> keys) {
		StringBuilder output = new StringBuilder();
    	for (AbstractKey key : keys) {
    		output.append(key.toString() + "\n");
    	}
    	return output.toString();
	}
	
	// START|type|id|created|modified|description|[key|value]+|END
	public List<AbstractKey> deserializeKeys(String input) {
		if (input != null && input.length() > 0) {
        	String[] lines = input.split("\n");
        	int index = 0;
        	
        	List<AbstractKey> result = new ArrayList<AbstractKey>();
        	while (index < lines.length) {
        		// lines[index] just contains "START"
        		KeyType type = KeyType.valueOf(lines[index+1]);
        		int id = Integer.parseInt(lines[index+2]);
        		LocalDate created = LocalDate.parse(lines[index+3]);
        		LocalDate modified = LocalDate.parse(lines[index+4]);
        		String description = lines[index+5];

        		Map<String, String> props = new HashMap<String, String>();
        		for (int i = index+6; !lines[i].equals("END"); i += 2) {
    				props.put(lines[i], lines[i+1]);
    			}
        		
        		AbstractKey key = keyFactory.createKey(type, id, description, props);
        		key.setCreated(created);
        		key.setModified(modified);
        		result.add(key);
        		
        		index += 5 + props.keySet().size()*2 + 2;
        	}
        	
        	return result;
		} else {
			return new ArrayList<AbstractKey>();
		}
	}
}
