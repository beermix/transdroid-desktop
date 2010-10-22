package org.transdroid.desktop.gui;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class TorrentsTable extends JTable {

	private static final long serialVersionUID = -246035344011341269L;

	/**
	 * Create the table.
	 */
	public TorrentsTable() {
		
		// Initialize
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
		setCellSelectionEnabled(false);
		
	}
	
}
