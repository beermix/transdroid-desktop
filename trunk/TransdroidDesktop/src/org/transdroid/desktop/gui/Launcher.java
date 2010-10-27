package org.transdroid.desktop.gui;

import java.awt.EventQueue;

import javax.swing.UIManager;

public class Launcher {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Transdroid Desktop");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					Transdroid transdroid = new Transdroid();
					transdroid.setVisible(true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
