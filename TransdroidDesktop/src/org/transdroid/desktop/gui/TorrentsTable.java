package org.transdroid.desktop.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.transdroid.desktop.controller.TorrentsModel;

public class TorrentsTable extends JTable {

	private static final long serialVersionUID = -246035344011341269L;
	
	/**
	 * Create the table.
	 * @param torrents 
	 */
	public TorrentsTable(TorrentsModel torrents, final JPopupMenu actionPopup) {
		super(torrents);
		
		// Initialize
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setColumnSelectionAllowed(false);
		for (int i = 0; i < torrents.getColumnCount(); i++) {
			getColumnModel().getColumn(i).setPreferredWidth(torrents.getColumnPreferredSize(i));
		}
		if (actionPopup != null) {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					showPopup(e);
				}			
				@Override
				public void mousePressed(MouseEvent e) {
					showPopup(e);
				}
				private void showPopup(MouseEvent e) {
					// Show popup on right-click (or whatever is the popup trigger)
					if (e.isPopupTrigger()) {
						// If no multi-selection was made yet, use the clicked row as selection
						if (getSelectedRowCount() <= 1) {
							int clickedRow = rowAtPoint(e.getPoint());
							getSelectionModel().setSelectionInterval(clickedRow, clickedRow);
						}
						actionPopup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			});
		}
		
	}
	
}
