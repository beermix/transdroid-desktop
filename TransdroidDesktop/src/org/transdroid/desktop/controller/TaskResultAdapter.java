package org.transdroid.desktop.controller;

import javax.swing.SwingUtilities;

import org.transdroid.daemon.IDaemonCallback;
import org.transdroid.daemon.task.DaemonTask;
import org.transdroid.daemon.task.DaemonTaskFailureResult;
import org.transdroid.daemon.task.DaemonTaskSuccessResult;

public class TaskResultAdapter implements IDaemonCallback {

	private IDaemonCallback callback;
	
	public TaskResultAdapter(IDaemonCallback callback) {
		this.callback = callback;
	}
	
	@Override
	public void onQueueEmpty() {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				callback.onQueueEmpty();
			}
		});
	}

	@Override
	public void onQueuedTaskFinished(final DaemonTask finished) {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				callback.onQueuedTaskFinished(finished);
			}
		});
	}

	@Override
	public void onQueuedTaskStarted(final DaemonTask started) {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				callback.onQueuedTaskStarted(started);
			}
		});
	}

	@Override
	public void onTaskFailure(final DaemonTaskFailureResult result) {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				callback.onTaskFailure(result);
			}
		});
	}

	@Override
	public void onTaskSuccess(final DaemonTaskSuccessResult result) {
		SwingUtilities.invokeLater(new Runnable() {			
			@Override
			public void run() {
				callback.onTaskSuccess(result);
			}
		});
	}

}
