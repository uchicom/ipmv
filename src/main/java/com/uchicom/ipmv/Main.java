package com.uchicom.ipmv;

import javax.swing.SwingUtilities;

import com.uchicom.ipmv.window.IpmFrame;

/**
 * ipmライブラリを利用したサンプルプログラム
 * 
 * @author Shigeki.Uchiyama
 *
 */
public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			IpmFrame frame = new IpmFrame();
			frame.setVisible(true);
		});
	}

}
