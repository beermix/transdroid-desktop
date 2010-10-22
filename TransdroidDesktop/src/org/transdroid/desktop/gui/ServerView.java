package org.transdroid.desktop.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
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
	 * @param connection 
	 */
	public ServerView(DaemonSettings settings) {
		this.settings = settings;
		this.adapter = settings.getType().createAdapter(settings);
		this.queue = new TaskQueue(new TaskResultAdapter(this));
		
		initialize();
	}

	/**
	 * Initialize the contents of the panel.
	 */
	private void initialize() {
		setLayout(new BorderLayout(0, 0));
		
		table = new TorrentsTable();
		table.setFillsViewportHeight(true);
		JScrollPane torrentsScroll = new JScrollPane(table);
		add(torrentsScroll, BorderLayout.CENTER);
		
		JList listViews = new JList();
		add(listViews, BorderLayout.WEST);
	}

	private String getTag() {
		// Use the server name as log tag
		return settings.getName();
	}

	public void refresh() {
		queue.enqueue(RetrieveTask.create(adapter));
	}

	public void loadFile(File file) {
		queue.enqueue(AddByFileTask.create(adapter, file.getAbsolutePath()));
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
		case AddByUrl:
			StatusBar.d(getTag(), "Adding '" + ((AddByUrlTask)started).getUrl() + "' to the server");
		case AddByMagnetUrl:
			StatusBar.d(getTag(), "Adding '" + ((AddByMagnetUrlTask)started).getUrl() + "' to the server");
		case Pause:
			StatusBar.d(getTag(), "Pausing'" + started.getTargetTorrent().getName() + "'");
		case Resume:
			StatusBar.d(getTag(), "Resuming'" + started.getTargetTorrent().getName() + "'");
		case Stop:
			StatusBar.d(getTag(), "Stoppping'" + started.getTargetTorrent().getName() + "'");
		case Start:
			StatusBar.d(getTag(), "Starting'" + started.getTargetTorrent().getName() + "'");
		case Remove:
			StatusBar.d(getTag(), "Removing'" + started.getTargetTorrent().getName() + "'");
		case Retrieve:
			StatusBar.d(getTag(), "Refreshing...");
		}
	}

	@Override
	public void onTaskFailure(DaemonTaskFailureResult result) {
		switch (result.getException().getType()) {
		case MethodUnsupported:
			StatusBar.e(getTag(), "Operation not supported by your client");
		case ConnectionError:
			StatusBar.e(getTag(), "Connection error (please check your settings)");
		case UnexpectedResponse:
			StatusBar.e(getTag(), "Unexpected server response");
		case AuthenticationFailure:
			StatusBar.e(getTag(), "Failed to authenticate (please check your username and password)");
		case NotConnected:
			StatusBar.e(getTag(), "There is no conenction to a running daemon on your server");
		case ParsingFailed:
			StatusBar.e(getTag(), "Failed to parse the server response (please check your settings)");
		case FileAccessError:
			StatusBar.e(getTag(), "Cannot read the .torrent file");
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
