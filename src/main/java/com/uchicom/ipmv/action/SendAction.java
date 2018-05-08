package com.uchicom.ipmv.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.uchicom.ipmv.window.IpmFrame;

public class SendAction extends AbstractAction {

	private IpmFrame frame;
	public SendAction(IpmFrame frame) {
		this.frame = frame;
		putValue(NAME, "SEND");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {

		frame.openDialog();
	}

}
