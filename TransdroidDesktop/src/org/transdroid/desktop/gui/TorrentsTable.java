package org.transdroid.desktop.gui;

import javax.swing.JTable;

import org.transdroid.desktop.controller.TorrentsModel;

public class TorrentsTable extends JTable {

	private static final long serialVersionUID = -246035344011341269L;

	/**
	 * Create the table.
	 * @param torrents 
	 */
	public TorrentsTable(TorrentsModel torrents) {
		super(torrents);
		
		// Initialize
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setColumnSelectionAllowed(false);
		for (int i = 0; i < torrents.getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth(torrents.getColumnPreferredSize(i));
		}
		
	}
	
}
