package org.transdroid.desktop.controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.transdroid.daemon.Torrent;
import org.transdroid.daemon.TorrentStatus;
import org.transdroid.daemon.util.FileSizeConverter;
import org.transdroid.daemon.util.TimespanConverter;
import org.transdroid.desktop.gui.Transdroid;

public class TorrentsModel extends AbstractTableModel {

	private static final long serialVersionUID = -7118232097555641598L;

	private List<Torrent> torrents;
	private String[] columns = new String[] {"Name", "Status", "Size", "Done",
			"Downloaded", "Uploaded", "Ratio", "Down speed", "Up speed", "ETA",
			"Label", "Peers", "Availability"};

	private TorrentsModel() {
		this.torrents = new ArrayList<Torrent>();
	}

	private TorrentsModel(List<Torrent> torrents) {
		this.torrents = torrents;
	}

	public void resetTorrents(List<Torrent> torrents) {
		this.torrents = torrents;
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public String getColumnName(int col) {
		return columns[col];
	}
	
	@Override
	public int getRowCount() {
		return torrents.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Torrent tor = torrents.get(row);
		switch (col) {
		case 0:
			return tor.getName();
		case 1:
			return tor.getStatusCode();
		case 2:
			return FileSizeConverter.getSize(tor.getTotalSize());
		case 3:
			return String.format(Transdroid.DECIMAL_FORMATTER, tor.getDownloadedPercentage() * 100) + "%";
		case 4:
			return FileSizeConverter.getSize(tor.getDownloadedEver());
		case 5:
			return FileSizeConverter.getSize(tor.getUploadedEver());
		case 6:
			return getRatioString(tor);
		case 7:
			return FileSizeConverter.getSize(tor.getRateDownload()) + "/s";
		case 8:
			return FileSizeConverter.getSize(tor.getRateUpload()) + "/s";
		case 9:
			return (tor.getStatusCode() == TorrentStatus.Downloading? getRemainingTimeString(tor, true): "");
		case 10:
			return tor.getLabelName();
		case 11:
			if (tor.getStatusCode() == TorrentStatus.Downloading) {
				return tor.getPeersSendingToUs() + " of " + tor.getPeersConnected();
			} else if (tor.getStatusCode() == TorrentStatus.Seeding) {
				return tor.getPeersGettingFromUs() + " of " + tor.getPeersConnected();
			} else {
				return tor.getPeersKnown();
			}
		case 12:
			return String.format(Transdroid.DECIMAL_FORMATTER, tor.getAvailability() * 100) + "%";
		}
		return null;
	}
	
	private String getRatioString(Torrent tor) {
		long baseSize = tor.getTotalSize();
		if (tor.getStatusCode() == TorrentStatus.Downloading) {
			baseSize = tor.getDownloadedEver();
		}
		if (baseSize <= 0) {
			return String.format(Transdroid.DECIMAL_FORMATTER, 0d);
		} else if (tor.getRatio() == Double.POSITIVE_INFINITY) {
			return "\u221E";
		} else {
			return String.format(Transdroid.DECIMAL_FORMATTER, tor.getRatio());
		}
	}
	
	private String getRemainingTimeString(Torrent tor, boolean inDays) {
		if (tor.getEta() == -1 || tor.getEta() == -2) {
			return "Unknown";
		}
		return TimespanConverter.getTime(tor.getEta(), inDays);
	}

	public Torrent getTorrent(int row) {
		return torrents.get(row);
	}

	public void mimicPauseTorrent(Torrent torrent) {
		torrent.mimicPause();
		fireTableDataChanged();
	}

	public void mimicResumeTorrent(Torrent torrent) {
		torrent.mimicResume();
		fireTableDataChanged();
	}

	public void mimicStopTorrent(Torrent torrent) {
		torrent.mimicStop();
		fireTableDataChanged();
	}

	public void mimicStartTorrent(Torrent torrent) {
		torrent.mimicStart();
		fireTableDataChanged();
	}

	public void mimicRemoveTorrent(Torrent remove) {
		torrents.remove(remove);
		fireTableDataChanged();
	}

}
