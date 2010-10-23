package org.transdroid.desktop.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.transdroid.daemon.DaemonSettings;

public class ConnectDialog extends JDialog {

	private static final long serialVersionUID = 5341384572231073007L;

	private DaemonSettingsView server;

	protected DialogResultListener callback;

	public static ConnectDialog showDialog(Frame frame, DialogResultListener resultListener) {
		ConnectDialog dialog = new ConnectDialog(frame, "Connect to a server", resultListener);
		dialog.setVisible(true);
		return dialog;
	}
	
	private ConnectDialog(Frame frame, String title, DialogResultListener resultListener) {
		super(frame, title, false);
		this.callback = resultListener;
		initialize(frame);
	}

	/**
	 * Initialize the contents of the dialog.
	 * @param frame Frame to position this dialog to
	 * @param savedServers A list of saved server settings
	 */
	private void initialize(Frame frame) {
		
		server = new DaemonSettingsView(callback.getSavedSettings());
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		JButton connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (server.isDataValid()) {
					callback.onSettingsCompleted(getServerSettings());
					setVisible(false);
				}
			}
		});
		buttonPane.add(connectButton);
		JButton saveServerButton = new JButton("Save server");
		saveServerButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (server.isDataValid()) {
					callback.onSaveSettings(getServerSettings());
					server = new DaemonSettingsView(callback.getSavedSettings());
				}
			}
		});
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(saveServerButton);
		JButton removeServerButton = new JButton("Remove server");
		removeServerButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (server.isSavedServer()) {
					callback.onRemoveSettings(getServerSettings());
					server = new DaemonSettingsView(callback.getSavedSettings());
				}
			}
		});
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(removeServerButton);
		getRootPane().setDefaultButton(connectButton);

		getContentPane().add(server, BorderLayout.CENTER);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(frame);
		
	}

	private DaemonSettings getServerSettings() {
		return server.getServerSettings();
	}
	
	/**
	 * Used to listen whether the settings of the dialog have been completed
	 */
	public interface DialogResultListener {

		/**
		 * Called when the dialog needs the current list of saved server settings
		 * @return The list of saved server settings
		 */
		List<DaemonSettings> getSavedSettings();

		/**
		 * Called when the dialog result (a server configuration) is known
		 * @param serverSettings The completed server settings
		 */
		void onSettingsCompleted(DaemonSettings serverSettings);

		/**
		 * Called when the user requested some server settings to be removed
		 * @param serverSettings The settings to remove form the saved servers
		 */
		void onRemoveSettings(DaemonSettings serverSettings);

		/**
		 * Called when the user requested to save the entered server settings
		 * @param serverSettings The settings to save
		 */
		void onSaveSettings(DaemonSettings serverSettings);
		
	}

}
