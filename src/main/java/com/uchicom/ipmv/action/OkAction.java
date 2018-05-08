package com.uchicom.ipmv.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.uchicom.ipmv.window.MessageDialog;

public class OkAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MessageDialog dialog;
	public OkAction(MessageDialog dialog) {
		this.dialog = dialog;
		putValue(NAME, "OK");
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.send();

	}

}
