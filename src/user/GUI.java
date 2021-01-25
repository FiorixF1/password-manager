package user;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import core.KeyManager;
import core.UserInterface;
import key.AbstractKey;
import key.KeyType;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;

public class GUI {
	private UserInterface ui;
	private Map<KeyType, String[]> keysMetadata;
	private KeyType[] types;

	private JFrame frame;
	private SpringLayout springLayout;
	private JButton insertButton;
	private JButton updateButton;
	private JButton deleteButton;
	private JList<String> list;
	private JScrollPane scrollPane;
	
	private DefaultListModel<String> dlm;
	
	public static void exec() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					
					if (window.ui.isPasswordSet()) {
						window.askPassword();
					} else {
						window.setPassword();
					}
					
					window.startInterface();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GUI() {
		initialize();
	}

	private void initialize() {
		ui = KeyManager.getInstance();
		keysMetadata = ui.getKeysMetadata();
		types = new KeyType[keysMetadata.keySet().size()];
		int i = 0;
		for (KeyType type : keysMetadata.keySet()) {
			types[i++] = type;
		}
	}

	public void askPassword() {
		int tries = 10;
		do {
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(400, 70));
			
			SpringLayout springLayout = new SpringLayout();
			panel.setLayout(springLayout);
			
			JLabel label = new JLabel("Enter your password:");
			springLayout.putConstraint(SpringLayout.NORTH, label, 8, SpringLayout.NORTH, panel);
			springLayout.putConstraint(SpringLayout.WEST, label, 8, SpringLayout.WEST, panel);
			panel.add(label);
			
			JPasswordField passwordField = new JPasswordField();
			springLayout.putConstraint(SpringLayout.NORTH, passwordField, 0, SpringLayout.NORTH, label);
			springLayout.putConstraint(SpringLayout.WEST, passwordField, 200, SpringLayout.WEST, panel);
			springLayout.putConstraint(SpringLayout.EAST, passwordField, -8, SpringLayout.EAST, panel);
			passwordField.requestFocusInWindow();
			panel.add(passwordField);
			
			JLabel attempts = new JLabel(tries + " attempts left");
			if (tries <= 3) attempts.setForeground(new Color(0xff0000));
			if (tries == 1) attempts.setText("1 attempt left");
			springLayout.putConstraint(SpringLayout.NORTH, attempts, 16, SpringLayout.SOUTH, passwordField);
			springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, attempts, 0, SpringLayout.HORIZONTAL_CENTER, panel);
			panel.add(attempts);
			
			int okCxl = JOptionPane.showConfirmDialog(null,
				panel,
				"Password Manager",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
			
			if (okCxl == JOptionPane.OK_OPTION) {
			    String password = new String(passwordField.getPassword());
			    if (ui.login(password)) {
					return;
				} else {
					--tries;
				}
			} else {
				System.exit(0);
			}
		} while (tries > 0);
		
		ui.deleteMasterKey();
		JOptionPane.showMessageDialog(frame,
			"Password and keys deleted!!!",
			"Password Manager",
			JOptionPane.ERROR_MESSAGE);
		setPassword();
	}
	
	public void setPassword() {;
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(400, 70));
		
		SpringLayout springLayout = new SpringLayout();
		panel.setLayout(springLayout);
		
		JLabel label = new JLabel("Set your password:");
		springLayout.putConstraint(SpringLayout.NORTH, label, 8, SpringLayout.NORTH, panel);
		springLayout.putConstraint(SpringLayout.WEST, label, 8, SpringLayout.WEST, panel);
		panel.add(label);
		
		JPasswordField passwordField = new JPasswordField();
		springLayout.putConstraint(SpringLayout.NORTH, passwordField, 0, SpringLayout.NORTH, label);
		springLayout.putConstraint(SpringLayout.WEST, passwordField, 200, SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.EAST, passwordField, -8, SpringLayout.EAST, panel);
		panel.add(passwordField);
		
		JLabel labelConfirm = new JLabel("Confirm your password:");
		springLayout.putConstraint(SpringLayout.NORTH, labelConfirm, 16, SpringLayout.SOUTH, label);
		springLayout.putConstraint(SpringLayout.WEST, labelConfirm, 0, SpringLayout.WEST, label);
		panel.add(labelConfirm);
		
		JPasswordField passwordFieldConfirm = new JPasswordField();
		springLayout.putConstraint(SpringLayout.NORTH, passwordFieldConfirm, 0, SpringLayout.NORTH, labelConfirm);
		springLayout.putConstraint(SpringLayout.WEST, passwordFieldConfirm, 0, SpringLayout.WEST, passwordField);
		springLayout.putConstraint(SpringLayout.EAST, passwordFieldConfirm, 0, SpringLayout.EAST, passwordField);
		panel.add(passwordFieldConfirm);
		
		int okCxl = JOptionPane.showConfirmDialog(null,
			panel,
			"Password Manager",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.PLAIN_MESSAGE);
		
		if (okCxl == JOptionPane.OK_OPTION) {
		    String firstPassword = new String(passwordField.getPassword());
		    String secondPassword = new String(passwordFieldConfirm.getPassword());
		    if (ui.isSecureEnough(firstPassword)) {
				if (firstPassword.equals(secondPassword)) {
					ui.setMasterKey(firstPassword);
				} else {
					JOptionPane.showMessageDialog(null,
						"The passwords don't match!",
						"Password Manager",
						JOptionPane.INFORMATION_MESSAGE);
					setPassword();
				}
			} else {
				JOptionPane.showMessageDialog(null,
					"The chosen password is not secure enough. it must respect these constraints:" + "\n" +
					"- Be at least 8 characters long" + "\n" +
					"- Contain lowercase letters" + "\n" +
					"- Contain uppercase letters" + "\n" +
					"- Contain digits",
					"Password Manager",
					JOptionPane.INFORMATION_MESSAGE);
				setPassword();
			}
		} else {
			System.exit(0);
		}
	}
	
	public void startInterface() {
		frame = new JFrame();
		frame.setBounds(0, 0, 1000, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		
		insertButton = new JButton("Insert");
		springLayout.putConstraint(SpringLayout.NORTH, insertButton, 24, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, insertButton, -128, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, insertButton, -24, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(insertButton);
		
		updateButton = new JButton("Update");
		springLayout.putConstraint(SpringLayout.NORTH, updateButton, 24, SpringLayout.SOUTH, insertButton);
		springLayout.putConstraint(SpringLayout.WEST, updateButton, 0, SpringLayout.WEST, insertButton);
		springLayout.putConstraint(SpringLayout.EAST, updateButton, 0, SpringLayout.EAST, insertButton);
		frame.getContentPane().add(updateButton);
		
		deleteButton = new JButton("Delete");
		springLayout.putConstraint(SpringLayout.NORTH, deleteButton, 24, SpringLayout.SOUTH, updateButton);
		springLayout.putConstraint(SpringLayout.WEST, deleteButton, 0, SpringLayout.WEST, updateButton);
		springLayout.putConstraint(SpringLayout.EAST, deleteButton, 0, SpringLayout.EAST, updateButton);
		frame.getContentPane().add(deleteButton);
		
		insertButton.setEnabled(true);
		updateButton.setEnabled(false);
		deleteButton.setEnabled(false);
		
		dlm = new DefaultListModel<String>();
		for (AbstractKey key : ui.getKeys()) {
		    dlm.addElement(key.getDescription());
		}
		
		list = new JList<String>(dlm);
		frame.getContentPane().add(list);
		
		scrollPane = new JScrollPane(list);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, insertButton);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 24, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, scrollPane, -24, SpringLayout.WEST, insertButton);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -24, SpringLayout.SOUTH, frame.getContentPane());
		frame.getContentPane().add(scrollPane);
		
		list.addListSelectionListener(new ListSelectionListener() {
		    public void valueChanged(ListSelectionEvent e) {
		    	updateButton.setEnabled(true);
				deleteButton.setEnabled(true);
		    }
		});

		insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (insertKey()) {
                	JScrollBar vertical = scrollPane.getVerticalScrollBar();
        			vertical.setValue(vertical.getMaximum());
                }
            }
        });
		
		updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int index = list.getSelectedIndex();
                updateKey(index);
            }
        });
		
		deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = list.getSelectedIndex();
                if (deleteKey(index)) {
                	updateButton.setEnabled(false);
    				deleteButton.setEnabled(false);
                }
            }
        });
		
		frame.setVisible(true);
	}
	
	public boolean insertKey() {
		JPanel introPanel = new JPanel();
		introPanel.setPreferredSize(new Dimension(400, 150));
		
		SpringLayout springLayout = new SpringLayout();
		introPanel.setLayout(springLayout);
		
		JComboBox<KeyType> combo = new JComboBox<KeyType>(types);
		springLayout.putConstraint(SpringLayout.NORTH, combo, 8, SpringLayout.NORTH, introPanel);
		springLayout.putConstraint(SpringLayout.WEST, combo, 8, SpringLayout.WEST, introPanel);
		springLayout.putConstraint(SpringLayout.EAST, combo, -8, SpringLayout.EAST, introPanel);		
		introPanel.add(combo);
		
		int typeCxl = JOptionPane.showConfirmDialog(null,
			introPanel,
			"Password Manager",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.PLAIN_MESSAGE);
		
		if (typeCxl == JOptionPane.OK_OPTION) {
			KeyType type = combo.getItemAt(combo.getSelectedIndex());
			JPanel panel = createPanel(type);
			int okCxl = JOptionPane.showConfirmDialog(null,
				panel,
				"Password Manager",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
			
			if (okCxl == JOptionPane.OK_OPTION) {
				String description = null;
    			Map<String, String> props = new HashMap<String, String>();
    			for (Component component : panel.getComponents()) {
    				if (component instanceof JTextField) {
    					String prop = component.getName();
    					String value = ((JTextField) component).getText();
    					if (prop != null && value.length() > 0) {
    						if (prop.equals("description")) {
    							description = value;
    						} else {
    							props.put(prop, value);
    						}
    					}
    				}
    			}
    			ui.insertKey(type, description, props);
    			dlm.addElement(description);
    			return true;
			}
		}
		
		return false;
	}
	
	public boolean updateKey(int index) {
		AbstractKey key = ui.getKeys().get(index);
		JPanel panel = createPanel(key);
		int okCxl = JOptionPane.showConfirmDialog(null,
			panel,
			"Password Manager",
			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.PLAIN_MESSAGE);
		
		if (okCxl == JOptionPane.OK_OPTION) {
			String description = null;
			Map<String, String> props = new HashMap<String, String>();
			for (Component component : panel.getComponents()) {
				if (component instanceof JTextField) {
					String prop = component.getName();
					String value = ((JTextField) component).getText();
					if (prop != null && value.length() > 0) {
						if (prop.equals("description")) {
							description = value;
						} else {
							props.put(prop, value);
						}
					}
				}
			}
			ui.updateKey(key, description, props);
			dlm.set(index, description);
			return true;
		}
		
		return false;
	}
	
	public boolean deleteKey(int index) {
		int okCxl = JOptionPane.showConfirmDialog(null,
			"Are you sure you want to delete this key?",
			"Password Manager",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE);
		
		if (okCxl == JOptionPane.YES_OPTION) {
			int id = ui.getKeys().get(index).getId();
			ui.deleteKey(id);
			dlm.remove(index);
			return true;
		}
		
		return false;
	}
	
	public JPanel createPanel(AbstractKey key) {
		KeyType type = key.getType();
		String[] props = keysMetadata.get(type);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(400, 350));
		
		SpringLayout springLayout = new SpringLayout();
		panel.setLayout(springLayout);
		
		JLabel mainLabel = new JLabel("Description:");
		springLayout.putConstraint(SpringLayout.NORTH, mainLabel, 8, SpringLayout.NORTH, panel);
		springLayout.putConstraint(SpringLayout.WEST, mainLabel, 8, SpringLayout.WEST, panel);
		panel.add(mainLabel);
		
		JTextField mainText = new JTextField(key.getDescription());
		mainText.setName("description");
		springLayout.putConstraint(SpringLayout.NORTH, mainText, 0, SpringLayout.NORTH, mainLabel);
		springLayout.putConstraint(SpringLayout.WEST, mainText, 200, SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.EAST, mainText, -8, SpringLayout.EAST, panel);
		panel.add(mainText);
		
		JLabel previousLabel = mainLabel;
		JTextField previousText = mainText;
		for (String prop : props) {
			JLabel label = new JLabel(prop + ":");
			springLayout.putConstraint(SpringLayout.NORTH, label, 16, SpringLayout.SOUTH, previousLabel);
			springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, previousLabel);
			panel.add(label);
			
			JTextField text = new JTextField(key.getProperty(prop));
			text.setName(prop);
			springLayout.putConstraint(SpringLayout.NORTH, text, 0, SpringLayout.NORTH, label);
			springLayout.putConstraint(SpringLayout.WEST, text, 0, SpringLayout.WEST, previousText);
			springLayout.putConstraint(SpringLayout.EAST, text, 0, SpringLayout.EAST, previousText);
			panel.add(text);
			
			previousLabel = label;
			previousText = text;
		}
		
		JLabel createdLabel = new JLabel("Created:");
		springLayout.putConstraint(SpringLayout.NORTH, createdLabel, 16, SpringLayout.SOUTH, previousLabel);
		springLayout.putConstraint(SpringLayout.WEST, createdLabel, 0, SpringLayout.WEST, previousLabel);
		panel.add(createdLabel);
			
		JTextField createdText = new JTextField(key.getCreated().toString());
		springLayout.putConstraint(SpringLayout.NORTH, createdText, 0, SpringLayout.NORTH, createdLabel);
		springLayout.putConstraint(SpringLayout.WEST, createdText, 0, SpringLayout.WEST, previousText);
		springLayout.putConstraint(SpringLayout.EAST, createdText, 0, SpringLayout.EAST, previousText);
		panel.add(createdText);
			
		JLabel modifiedLabel = new JLabel("Last modified:");
		springLayout.putConstraint(SpringLayout.NORTH, modifiedLabel, 16, SpringLayout.SOUTH, createdLabel);
		springLayout.putConstraint(SpringLayout.WEST, modifiedLabel, 0, SpringLayout.WEST, createdLabel);
		panel.add(modifiedLabel);
			
		JTextField modifiedText = new JTextField(key.getModified().toString());
		springLayout.putConstraint(SpringLayout.NORTH, modifiedText, 0, SpringLayout.NORTH, modifiedLabel);
		springLayout.putConstraint(SpringLayout.WEST, modifiedText, 0, SpringLayout.WEST, createdText);
		springLayout.putConstraint(SpringLayout.EAST, modifiedText, 0, SpringLayout.EAST, createdText);
		panel.add(modifiedText);
		
		return panel;
	}
	
	public JPanel createPanel(KeyType type) {
		String[] props = keysMetadata.get(type);

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(400, 350));
		
		SpringLayout springLayout = new SpringLayout();
		panel.setLayout(springLayout);
		
		JLabel mainLabel = new JLabel("Description:");
		springLayout.putConstraint(SpringLayout.NORTH, mainLabel, 8, SpringLayout.NORTH, panel);
		springLayout.putConstraint(SpringLayout.WEST, mainLabel, 8, SpringLayout.WEST, panel);
		panel.add(mainLabel);
		
		JTextField mainText = new JTextField();
		mainText.setName("description");
		springLayout.putConstraint(SpringLayout.NORTH, mainText, 0, SpringLayout.NORTH, mainLabel);
		springLayout.putConstraint(SpringLayout.WEST, mainText, 200, SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.EAST, mainText, -8, SpringLayout.EAST, panel);
		panel.add(mainText);

		JLabel previousLabel = mainLabel;
		JTextField previousText = mainText;
		for (String prop : props) {
			JLabel label = new JLabel(prop + ":");
			springLayout.putConstraint(SpringLayout.NORTH, label, 16, SpringLayout.SOUTH, previousLabel);
			springLayout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, previousLabel);
			panel.add(label);

			JTextField text = new JTextField();
			text.setName(prop);
			springLayout.putConstraint(SpringLayout.NORTH, text, 0, SpringLayout.NORTH, label);
			springLayout.putConstraint(SpringLayout.WEST, text, 0, SpringLayout.WEST, previousText);
			springLayout.putConstraint(SpringLayout.EAST, text, 0, SpringLayout.EAST, previousText);
			panel.add(text);

			previousLabel = label;
			previousText = text;
		}
		
		return panel;
	}
}
