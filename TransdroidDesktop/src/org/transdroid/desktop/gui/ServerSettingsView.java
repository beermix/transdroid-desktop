package org.transdroid.desktop.gui;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.guicomponents.LabelledItemPanel;
import org.transdroid.daemon.Daemon;
import org.transdroid.daemon.DaemonSettings;

public class ServerSettingsView extends LabelledItemPanel {

	private static final long serialVersionUID = -8107335581244820813L;
	
	private JTextField name = new JTextField();
	private JComboBox type = new JComboBox(Daemon.values());
	private JTextField host = new JTextField();
	private JTextField port = new JTextField();
	private JCheckBox useAuth = new JCheckBox();
	private JTextField username = new JTextField();
	private JTextField password = new JTextField();
	private JTextField folder = new JTextField();
	private JCheckBox useSsl = new JCheckBox();
	private JTextField sslFingerprint = new JTextField();
	private JCheckBox sslAcceptAll = new JCheckBox();

	/**
	 * Create the panel.
	 */
	public ServerSettingsView() {
		initialize();
	}

	/**
	 * Initialize the contents of the panel.
	 */
	private void initialize() {

        setBorder(BorderFactory.createEtchedBorder());

        addItem("Name", name );
        addItem("Server type", type);
        addItem("Host (ip address)", host);
        addItem("Port", port);
        addItem("Use authentication", useAuth);
        addItem("Username", username);
        addItem("Password", password);
        addItem("Folder", folder);
        addItem("Use SSL", useSsl);
        addItem("Custom SSL fingerprint", sslFingerprint);
        addItem("Accept all certificates", sslAcceptAll);
        
	}
	
	public DaemonSettings getServerSettings() {
		return new DaemonSettings(name.getText(), (Daemon)type.getSelectedItem(), 
				host.getText(), Integer.parseInt(port.getText()), useSsl.isSelected(), 
				sslAcceptAll.isSelected(), sslFingerprint.getText(), folder.getText(),
				useAuth.isSelected(), username.getText(), password.getText(),
				null, null, null, null, false, false, host.getText(), false);
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
	
}
