package org.transdroid.desktop.gui;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.guicomponents.JTabbedPaneWithCloseIcons;
import org.json.JSONException;
import org.transdroid.daemon.DaemonSettings;
import org.transdroid.daemon.OS;
import org.transdroid.daemon.util.DLog;
import org.transdroid.daemon.util.ITLogger;
import org.transdroid.desktop.controller.AppSettings;
import org.transdroid.desktop.gui.ConnectDialog.DialogResultListener;

public class Transdroid {

	public static final String DECIMAL_FORMATTER = "%.1f";
	private static final String TAG = "Transdroid";
	private static final String SYSTEM_OS = System.getProperty("os.name").toLowerCase();
	private static final String FILE_SEPERATOR = File.separator;
	private static final File DEFAULT_SETTINGS = new File(System.getProperty("user.home") + 
			FILE_SEPERATOR + ".transdroid-desktop" + FILE_SEPERATOR + "settings.json");
	private static final String IMAGESDIR = "res/img/";
	
	private Logger logger;
	private AppSettings appSettings;
	private ConnectDialog connectDialog;
	
	private JFrame frame;
	private StatusBar statusBar;
	private JTabbedPaneWithCloseIcons serverTabs;
	private JButton btnRefresh;
	private JButton btnLoadTorentFile;
	private AbstractButton btnLoadUrl;
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnResume;
	private JButton btnPause;
	private JButton btnRemove;

	/**
	 * Create the application.
	 */
	public Transdroid() {
		
		// Initialize the logging
		logger = Logger.getLogger(TAG);
		DLog.setLogger(new ITLogger() {
			@Override
			public void e(String self, String msg) {
				logger.warning(self + ": " + msg);
			}			
			@Override
			public void d(String self, String msg) {
				logger.finer(self + ": " + msg);
			}
		});
		DLog.d(TAG, "Welcome to Transdroid Desktop");

		// Initialize the UI
		initialize();

		// Load default application settings
		appSettings = new AppSettings();
		if (DEFAULT_SETTINGS.exists()) {
			try {
				appSettings.loadFromFile(DEFAULT_SETTINGS);
			} catch (FileNotFoundException e) {
				StatusBar.e(TAG, "Cannot read the default settings file " + DEFAULT_SETTINGS);
			} catch (JSONException e) {
				StatusBar.e(TAG, "The default settings file " + DEFAULT_SETTINGS + " does not contain readable settings");
			}
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Transdroid Desktop");		
		frame.setBounds(100, 100, 1000, 450);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setIconImage(new ImageIcon(IMAGESDIR + FILE_SEPERATOR + "icon-72.png").getImage());
		
		/*JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu mnAbout = new JMenu("Transdroid");
		menuBar.add(mnAbout);
		JMenuItem mntmSetupHelp = new JMenuItem("Setup help...");
		mnAbout.add(mntmSetupHelp);
		JMenuItem mntmAboutTransdroid = new JMenuItem("About Transdroid");
		mnAbout.add(mntmAboutTransdroid);*/
		
		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(connectListener);
		toolBar.add(btnConnect);
		toolBar.addSeparator();
		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(refreshListener);
		btnRefresh.setEnabled(false);
		toolBar.add(btnRefresh);
		btnLoadTorentFile = new JButton("Load .torrent");
		btnLoadTorentFile.addActionListener(loadTorrentFileListener);
		btnLoadTorentFile.setEnabled(false);
		toolBar.add(btnLoadTorentFile);
		btnLoadUrl = new JButton("Load URL");
		btnLoadUrl.addActionListener(loadUrlListener);
		btnLoadUrl.setEnabled(false);
		toolBar.add(btnLoadUrl);
		toolBar.addSeparator();
		btnStart = new JButton("Start");
		btnStart.addActionListener(startListener);
		btnStart.setEnabled(false);
		toolBar.add(btnStart);
		btnStop = new JButton("Stop");
		btnStop.addActionListener(stopListener);
		btnStop.setEnabled(false);
		toolBar.add(btnStop);
		btnResume = new JButton("Resume");
		btnResume.addActionListener(resumeListener);
		btnResume.setEnabled(false);
		toolBar.add(btnResume);
		btnPause = new JButton("Pause");
		btnPause.addActionListener(pauseListener);
		btnPause.setEnabled(false);
		toolBar.add(btnPause);
		btnRemove = new JButton("Remove");
		btnRemove.addActionListener(removeListener);
		btnRemove.setEnabled(false);
		toolBar.add(btnRemove);
		
		serverTabs = new JTabbedPaneWithCloseIcons();
		serverTabs.addChangeListener(tabListener);
		frame.getContentPane().add(serverTabs, BorderLayout.CENTER);
		
		statusBar = new StatusBar();
		frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		
	}
	
	public void setVisible(boolean visible) {
		frame.setVisible(visible);
	}
	
	private ChangeListener tabListener = new ChangeListener() {		
		@Override
		public void stateChanged(ChangeEvent e) {
			updateToolBar();
		}
	};
	
	private ActionListener connectListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (connectDialog == null) {
				connectDialog = ConnectDialog.showDialog(frame, dialogConnectListener);
			} else {
				connectDialog.setVisible(true);
			}
		}
	};
	
	private DialogResultListener dialogConnectListener = new DialogResultListener() {
		@Override
		public void onSettingsCompleted(DaemonSettings settings) {
			// Create a new server connection and server view
			ServerView server = new ServerView(settings);
			serverTabs.addTab(settings.getName(), server);
			//updateToolBar();
			// Start a first refresh
			server.refresh();
		}
		@Override
		public void onSaveSettings(DaemonSettings serverSettings) {
			try {
				appSettings.saveServer(serverSettings);
				appSettings.saveToFile(DEFAULT_SETTINGS);
			} catch (IOException ex) {
				StatusBar.e(TAG, "Cannot save settings to " + DEFAULT_SETTINGS);
			} catch (JSONException ex) {
				StatusBar.e(TAG, "Cannot parse the settings to save");
			}
		}
		@Override
		public void onRemoveSettings(DaemonSettings serverSettings) {
			try {
				appSettings.removeServer(serverSettings.getIdString());
				appSettings.saveToFile(DEFAULT_SETTINGS);
			} catch (IOException ex) {
				StatusBar.e(TAG, "Cannot save settings to " + DEFAULT_SETTINGS);
			} catch (JSONException ex) {
				StatusBar.e(TAG, "Cannot parse the settings to save");
			}
		}
		@Override
		public List<DaemonSettings> getSavedSettings() {
			return appSettings.getSavedServers();
		}
	};
	
	private void updateToolBar() {
		boolean ok = getCurrentServer() != null;
		btnRefresh.setEnabled(ok);
		btnLoadTorentFile.setEnabled(ok);
		btnLoadUrl.setEnabled(ok);
		btnStart.setEnabled(ok);
		btnStop.setEnabled(ok);
		btnResume.setEnabled(ok);
		btnPause.setEnabled(ok);
		btnRemove.setEnabled(ok);
	}

	private ActionListener refreshListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			getCurrentServer().refresh();
		}
	};
	
	private ActionListener loadTorrentFileListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			File toAdd = showFileChooserDialog();
			if (toAdd != null) {
				getCurrentServer().loadFile(toAdd);
			}
		}

	};

	private ActionListener loadUrlListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			String url = JOptionPane.showInputDialog("Enter the .torrent URL to add");
			getCurrentServer().loadUrl(url);
		}
	};
	
	private ActionListener startListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			getCurrentServer().start(false);
		}
	};
	
	private ActionListener stopListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			getCurrentServer().stop();
		}
	}; 
	
	private ActionListener resumeListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			getCurrentServer().resume();
		}
	};
	
	private ActionListener pauseListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			getCurrentServer().pause();
		}
	};
	
	private ActionListener removeListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			int result = JOptionPane.showOptionDialog(frame, 
					"Are you sure you want to delete the selected torrent(s)?", 
					"Confirm removal", JOptionPane.DEFAULT_OPTION, 
					JOptionPane.QUESTION_MESSAGE, null, 
					new String[] {"Yes", "Yes, with data", "No"}, null);
			if (result == 0 || result == 1) {
				getCurrentServer().remove(result == 1);
			}
		}
	};
	
	protected ServerView getCurrentServer() {
		return (ServerView) serverTabs.getSelectedComponent();
	}

	private File showFileChooserDialog() {
		File selectedFile = null;
		if (getSystemOS() != OS.Mac) {
			// Ask the user to select a file
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedFile = chooser.getSelectedFile();
			}
		} else {
			// Ask the user to select a file
			FileDialog dialog = new FileDialog(frame);
			dialog.setVisible(true);
			if (dialog.getFile() != null) {
				selectedFile = new File(dialog.getFile());
			}
		}
		return selectedFile;
	}

	/**
	 * Return the system OS depending on the name of the running OS
	 * @return The operating system
	 */
	private OS getSystemOS() {
		if (SYSTEM_OS.indexOf("win") >= 0) {
			return OS.Windows;
		} else if (SYSTEM_OS.indexOf("mac") >= 0) {
			return OS.Mac;
		} else {
			return OS.Linux;
		}
	}
	
}
