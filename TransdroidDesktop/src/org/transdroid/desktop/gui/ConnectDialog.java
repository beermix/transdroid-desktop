package org.transdroid.desktop.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.transdroid.daemon.DaemonSettings;

public class ConnectDialog extends JDialog {

	private static final long serialVersionUID = 5341384572231073007L;

	private ServerSettingsView server;
	private JButton connectButton;

	protected DialogResultListener callback;

	public static void showDialog(Frame frame, DialogResultListener resultListener) {
		ConnectDialog dialog = new ConnectDialog(frame, "Connect to a server", resultListener);
		dialog.setVisible(true);
	}
	
	private ConnectDialog(Frame frame, String title, DialogResultListener resultListener) {
		super(frame, title, false);
		this.callback = resultListener;
		initialize(frame);
	}

	/**
	 * Initialize the contents of the dialog.
	 * @param frame Frame to position this dialog to
	 */
	private void initialize(Frame frame) {
		
		server = new ServerSettingsView();
		
		connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (server.isDataValid()) {
					callback.onSettingsCompleted(getServerSettings());
				}
			}
		});
		getRootPane().setDefaultButton(connectButton);

		getContentPane().add(server);
		getContentPane().add(new JLabel("Teest"));
		getContentPane().add(connectButton);
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
		 * Called when the dialog result (a server configuration) is known
		 * @param serverSettings The completed server settings
		 */
		void onSettingsCompleted(DaemonSettings serverSettings);
		
	}

}
