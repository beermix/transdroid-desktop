package org.transdroid.desktop.gui;

import javax.swing.JLabel;

public class StatusBar extends JLabel {

	private static final long serialVersionUID = -6427300303008857078L;

	private static StatusBar instance = null;
	
	/**
	 * Create the status bar.
	 */
	public StatusBar() {
        super();
        instance = this;
	}

	public static void d(String self, String msg) {
		if (instance != null) {
			instance.setText(msg);
		}
	}

	public static void e(String self, String msg) {
		if (instance != null) {
			instance.setText(msg);
		}
	}

}
