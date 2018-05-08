package com.uchicom.ipmv.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.uchicom.ipmv.window.MessageDialog;

public class CancelAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MessageDialog dialog;

	public CancelAction(MessageDialog dialog, boolean editable) {
		this.dialog = dialog;
		if (editable) {
			putValue(NAME, "CANCEL");
		} else {
			putValue(NAME, "CLOSE");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		dialog.cancel();

	}

}
