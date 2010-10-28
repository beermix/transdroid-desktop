package org.transdroid.desktop.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.transdroid.daemon.DaemonSettings;
import org.transdroid.daemon.IDaemonAdapter;
import org.transdroid.daemon.IDaemonCallback;
import org.transdroid.daemon.TaskQueue;
import org.transdroid.daemon.Torrent;
import org.transdroid.daemon.task.AddByFileTask;
import org.transdroid.daemon.task.AddByMagnetUrlTask;
import org.transdroid.daemon.task.AddByUrlTask;
import org.transdroid.daemon.task.DaemonTask;
import org.transdroid.daemon.task.DaemonTaskFailureResult;
import org.transdroid.daemon.task.DaemonTaskSuccessResult;
import org.transdroid.daemon.task.PauseTask;
import org.transdroid.daemon.task.RemoveTask;
import org.transdroid.daemon.task.ResumeTask;
import org.transdroid.daemon.task.RetrieveTask;
import org.transdroid.daemon.task.RetrieveTaskSuccessResult;
import org.transdroid.daemon.task.SetDownloadLocationTask;
import org.transdroid.daemon.task.SetLabelTask;
import org.transdroid.daemon.task.StartTask;
import org.transdroid.daemon.task.StopTask;
import org.transdroid.desktop.controller.TaskResultAdapter;
import org.transdroid.desktop.controller.TorrentsModel;

public class ServerView extends JPanel implements IDaemonCallback {
	
	private static final long serialVersionUID = 6055269789801381397L;

	private DaemonSettings settings;
	private IDaemonAdapter adapter;
	private TorrentsTable table;
	private TorrentsModel torrents;
	private TaskQueue queue;

	private String status;

	/**
	 * Create the panel.
	 * @param settings The server configuration attached to this view
	 * @param actionPopup The popup to show with torrents actions 
	 */
	public ServerView(DaemonSettings settings, JPopupMenu actionPopup) {
		this.settings = settings;
		this.adapter = settings.getType().createAdapter(settings);
		this.torrents = new TorrentsModel();
		this.queue = new TaskQueue(new TaskResultAdapter(this));
		this.queue.start();
		
		initialize(actionPopup);
	}

	/**
	 * Initialize the contents of the panel.
	 */
	private void initialize(JPopupMenu actionPopup) {
		setLayout(new BorderLayout(0, 0));
		
		table = new TorrentsTable(torrents, actionPopup);
		table.setFillsViewportHeight(true);
		JScrollPane torrentsScroll = new JScrollPane(table);
		add(torrentsScroll, BorderLayout.CENTER);
		
		/*JList listViews = new JList();
		add(listViews, BorderLayout.WEST);*/
	}

	public DaemonSettings getSettings() {
		return this.settings;
	}

	private String getTag() {
		// Use the server name as log tag
		return settings.getName();
	}

	public void refresh() {
		queue.enqueue(RetrieveTask.create(adapter));
	}

	public void loadFile(File file) {
		queue.enqueue(AddByFileTask.create(adapter, "file://" + file.getAbsolutePath()));
		queue.enqueue(RetrieveTask.create(adapter));
	}

	public void loadUrl(String url) {
		queue.enqueue(AddByUrlTask.create(adapter, url));
		queue.enqueue(RetrieveTask.create(adapter));
	}

	public void loadMagnetUrl(String url) {
		queue.enqueue(AddByMagnetUrlTask.create(adapter, url));
		queue.enqueue(RetrieveTask.create(adapter));
	}

	public void start(boolean forced) {
		for (Torrent torrent : getSelection()) {
			torrents.mimicStartTorrent(torrent);
			queue.enqueue(StartTask.create(adapter, torrent, forced));
			queue.enqueue(RetrieveTask.create(adapter));
		}
	}

	public void stop() {
		for (Torrent torrent : getSelection()) {
			torrents.mimicStopTorrent(torrent);
			queue.enqueue(StopTask.create(adapter, torrent));
		}
	}

	public void resume() {
		for (Torrent torrent : getSelection()) {
			torrents.mimicResumeTorrent(torrent);
			queue.enqueue(ResumeTask.create(adapter, torrent));
			queue.enqueue(RetrieveTask.create(adapter));
		}
	}

	public void pause() {
		for (Torrent torrent : getSelection()) {
			torrents.mimicPauseTorrent(torrent);
			queue.enqueue(PauseTask.create(adapter, torrent));
			queue.enqueue(RetrieveTask.create(adapter));
		}
	}

	public void remove(boolean includingData) {
		for (Torrent torrent : getSelection()) {
			torrents.mimicRemoveTorrent(torrent);
			queue.enqueue(RemoveTask.create(adapter, torrent, includingData));
		}
	}

	public void setLabel(String newLabel) {
		for (Torrent torrent : getSelection()) {
			torrents.mimicNewLabel(torrent, newLabel);
			queue.enqueue(SetLabelTask.create(adapter, torrent, newLabel));
		}
	}

	public void setDownloadLocation(String newLocation) {
		for (Torrent torrent : getSelection()) {
			torrents.mimicNewDownloadLocation(torrent, newLocation);
			queue.enqueue(SetDownloadLocationTask.create(adapter, torrent, newLocation));
		}
	}
	
	/**
	 * Returns the list of currently selected torrents in the table
	 * @return A list of Torrents corresponding to the selected rows
	 */
	private ArrayList<Torrent> getSelection() {
		ArrayList<Torrent> selection = new ArrayList<Torrent>();
		for (int sel : table.getSelectedRows()) {
			selection.add(torrents.getTorrent(sel));
		}
		return selection;
	}

	/**
	 * Returns a the known labels on the server
	 * @return A list of label names with the number of torrents that have this label
	 */
	public Map<String, Integer> getExistingLabels() {
		return torrents.getExistingLabels();
	}
	
	@Override
	public void onQueueEmpty() {
		// Nothing to do
	}

	@Override
	public void onQueuedTaskFinished(DaemonTask finished) {
		// Nothing to do
	}

	@Override
	public void onQueuedTaskStarted(DaemonTask started) {
		switch (started.getMethod()) {
		case AddByFile:
			StatusBar.d(getTag(), "Uploading '" + ((AddByFileTask)started).getFile() + "' to the server");
			break;
		case AddByUrl:
			StatusBar.d(getTag(), "Adding '" + ((AddByUrlTask)started).getUrl() + "' to the server");
			break;
		case AddByMagnetUrl:
			StatusBar.d(getTag(), "Adding '" + ((AddByMagnetUrlTask)started).getUrl() + "' to the server");
			break;
		case Pause:
			StatusBar.d(getTag(), "Pausing'" + started.getTargetTorrent().getName() + "'");
			break;
		case Resume:
			StatusBar.d(getTag(), "Resuming'" + started.getTargetTorrent().getName() + "'");
			break;
		case Stop:
			StatusBar.d(getTag(), "Stoppping'" + started.getTargetTorrent().getName() + "'");
			break;
		case Start:
			StatusBar.d(getTag(), "Starting'" + started.getTargetTorrent().getName() + "'");
			break;
		case Remove:
			StatusBar.d(getTag(), "Removing'" + started.getTargetTorrent().getName() + "'");
			break;
		case Retrieve:
			StatusBar.d(getTag(), "Refreshing...");
			break;
		}
	}

	@Override
	public void onTaskFailure(DaemonTaskFailureResult result) {
		switch (result.getException().getType()) {
		case MethodUnsupported:
			StatusBar.e(getTag(), "Operation not supported by your client");
			break;
		case ConnectionError:
			StatusBar.e(getTag(), "Connection error (please check your settings)");
			break;
		case UnexpectedResponse:
			StatusBar.e(getTag(), "Unexpected server response");
			break;
		case AuthenticationFailure:
			StatusBar.e(getTag(), "Failed to authenticate (please check your username and password)");
			break;
		case NotConnected:
			StatusBar.e(getTag(), "There is no conenction to a running daemon on your server");
			break;
		case ParsingFailed:
			StatusBar.e(getTag(), "Failed to parse the server response (please check your settings)");
			break;
		case FileAccessError:
			StatusBar.e(getTag(), "Cannot read the .torrent file");
			break;
		}
	}

	@Override
	public void onTaskSuccess(DaemonTaskSuccessResult result) {
		switch (result.getTask().getMethod()) {
		case Retrieve:
			updateServerStats(((RetrieveTaskSuccessResult)result).getTorrents());
		default:
			// Show 'old' server statistics
			StatusBar.d(getTag(), status);
		}
	}

	private void updateServerStats(List<Torrent> result) {
		torrents.resetTorrents(result);
		status = result.size() + " torrents running";
		StatusBar.d(getTag(), status);
	}

}
