package org.transdroid.desktop.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.guicomponents.LabelledItemPanel;
import org.transdroid.daemon.Daemon;
import org.transdroid.daemon.DaemonSettings;
import org.transdroid.daemon.OS;

public class DaemonSettingsView extends LabelledItemPanel {

	private static final long serialVersionUID = -8107335581244820813L;

	private JComboBox savedServerBox;
	private JTextField name = new JTextField();
	private JComboBox type = new JComboBox(Daemon.values());
	private JTextField host = new JTextField();
	private JTextField port = new JTextField();
	private JCheckBox useAuth = new JCheckBox();
	private JTextField username = new JTextField();
	private JTextField password = new JPasswordField();
	private JTextField folder = new JTextField();
	private JComboBox os = new JComboBox(OS.values());
	private JCheckBox useSsl = new JCheckBox();
	private JTextField sslFingerprint = new JTextField();
	private JCheckBox sslAcceptAll = new JCheckBox();

	/**
	 * Create the panel.
	 */
	public DaemonSettingsView() {
		this(null);
	}

	public DaemonSettingsView(List<DaemonSettings> savedServers) {
		initialize((savedServers == null? new ArrayList<DaemonSettings>(): savedServers));
	}

	public void setSettings(DaemonSettings existing) {	
		if (existing != null) {
			name.setText(existing.getName());
			type.setSelectedItem(existing.getType());
			host.setText(existing.getAddress());
			port.setText(Integer.toString(existing.getPort()));
			useAuth.setSelected(existing.shouldUseAuthentication());
			username.setText(existing.getUsername());
			password.setText(existing.getPassword());
			folder.setText(existing.getFolder());
			os.setSelectedItem(existing.getOS());
			useSsl.setSelected(existing.getSsl());
			sslFingerprint.setText(existing.getSslTrustKey());
			sslAcceptAll.setSelected(existing.getSslTrustAll());
		}
	}

	/**
	 * Initialize the contents of the panel.
	 */
	private void initialize(List<DaemonSettings> savedServers) {

        setBorder(BorderFactory.createEtchedBorder());

        // Set up a combobox where the user can select a saved server's settings
        savedServers.add(0, null);
        savedServerBox = new JComboBox(savedServers.toArray(new DaemonSettings[] {}));
        savedServerBox.addActionListener(savedServerSelected);
        
        addItem("Saved server", savedServerBox);
        addItem("Name", name );
        addItem("Server type", type);
        addItem("Host (ip address)", host);
        addItem("Port", port);
        addItem("Use authentication", useAuth);
        addItem("Username", username);
        addItem("Password", password);
        addItem("Folder", folder);
        addItem("OS", os);
        addItem("Use SSL", useSsl);
        addItem("Custom SSL fingerprint", sslFingerprint);
        addItem("Accept all certificates", sslAcceptAll);
        
	}
	
	private ActionListener savedServerSelected = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			DaemonSettings settings = (DaemonSettings) savedServerBox.getSelectedItem();
			if (settings != null) {
				setSettings(settings);
			}
		}
	};
	
	public DaemonSettings getServerSettings() {
		return new DaemonSettings(name.getText(), (Daemon)type.getSelectedItem(), 
				host.getText(), Integer.parseInt(port.getText()), useSsl.isSelected(), 
				sslAcceptAll.isSelected(), sslFingerprint.getText(), folder.getText(),
				useAuth.isSelected(), username.getText(), password.getText(),
				(OS) os.getSelectedItem(), null, null, null, false, false, 
				host.getText(), false);
	}
	
	@Override
	protected boolean isDataValid() {
		if (host.getText() == null || host.getText().equals("")) {
			JOptionPane.showMessageDialog(this, 
					"Please enter your hostname or IP address to connect to.", 
					"No host specified", 
					JOptionPane.WARNING_MESSAGE);
			host.requestFocus();
			return false;
		}
		if (port.getText() == null || port.getText().equals("")) {
			JOptionPane.showMessageDialog(this, 
					"Please enter your port number to connect to.", 
					"No port specified", 
					JOptionPane.WARNING_MESSAGE);
			host.requestFocus();
			return false;
		}
		try {
			Integer.parseInt(port.getText());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, 
					"The port entered is not a valid number.", 
					"No valid port", 
					JOptionPane.WARNING_MESSAGE);
			host.requestFocus();
			return false;
		}
		
		// TODO: Add other validations
		return true;
	}

	/**
	 * Return whether a selection was made from the saved servers
	 * @return
	 */
	public boolean isSavedServer() {
		return savedServerBox.getSelectedIndex() > 0;
	}
	
}
