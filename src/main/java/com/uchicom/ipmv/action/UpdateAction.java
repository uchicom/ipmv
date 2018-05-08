package com.uchicom.ipmv.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.uchicom.ipmv.window.IpmFrame;

public class UpdateAction extends AbstractAction {

	private IpmFrame frame;
	public UpdateAction(IpmFrame frame) {
		this.frame = frame;
		putValue(NAME, "UPDATE");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e) {

		frame.update();
	}

}
