package com.uchicom.ipmv.window;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.uchicom.ipm.dto.User;
import com.uchicom.ipmv.action.CancelAction;
import com.uchicom.ipmv.action.OkAction;
import com.uchicom.ipmv.action.ReplyAction;
import com.uchicom.ui.LineNumberView;

/**
 * メッセージ送受信用ダイアログ.
 * 
 * @author Shigeki.Uchiyama
 *
 */
public class MessageDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea textArea = new JTextArea();

	private JCheckBox checkbox = new JCheckBox();

	private IpmFrame frame;

	private List<User> userList;

	public MessageDialog(IpmFrame frame, boolean editable, List<User> userList) {
		super(frame);
		this.frame = frame;
		this.userList = userList;
		initComponent(editable);
	}

	public MessageDialog(IpmFrame frame, boolean editable, List<User> userList, String text) {
		super(frame);
		this.frame = frame;
		this.userList = userList;
		textArea.setText(text);
		initComponent(editable);
	}

	private void initComponent(boolean editable) {
		if (editable) {
			setTitle("To:" + userList.toString());
		} else {
			setTitle("From:" + userList.get(0).toString());
		}
		textArea.setEditable(editable);
		BorderLayout layout = new BorderLayout();
		getContentPane().setLayout(layout);
		LineNumberView view = new LineNumberView(textArea);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setRowHeaderView(view);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		JPanel controlPanel = new JPanel();
		if (editable) {
			controlPanel.add(new JButton(new OkAction(this)));
		}
		controlPanel.add(new JButton(new CancelAction(this, editable)));
		if (editable) {
			checkbox.setText("封書");
			controlPanel.add(checkbox);
		} else {
			controlPanel.add(new JButton(new ReplyAction(this)));
			checkbox.setText("引用");
			controlPanel.add(checkbox);
		}
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		pack();
	}

	public void send() {
		if (true) // TODO checkbox ブロードキャスト
			for (User user : userList) {
				frame.send(this, user, textArea.getText(), checkbox.isSelected(), false);
			}
	}

	public void cancel() {
		dispose();
	}

	public void alert() {
		JOptionPane.showMessageDialog(this, "送信失敗かも");
	}

	/**
	 * 返信
	 */
	public void reply() {
		dispose();
		MessageDialog dialog = null;
		if (checkbox.isSelected()) {
			dialog = new MessageDialog(frame, true, userList, textArea.getText().replaceAll("^", "> ").replaceAll("\\n",  "\n> ") + "\n");
		} else {
			dialog = new MessageDialog(frame, true, userList);
		}
		dialog.setVisible(true);
	}
}
