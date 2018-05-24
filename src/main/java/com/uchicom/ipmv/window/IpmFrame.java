package com.uchicom.ipmv.window;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.uchicom.ipm.IpmAdapter;
import com.uchicom.ipm.Message;
import com.uchicom.ipm.dto.User;
import com.uchicom.ipm.type.Mode;
import com.uchicom.ipm.type.Option;
import com.uchicom.ipmv.action.SendAction;
import com.uchicom.ipmv.action.UpdateAction;
import com.uchicom.ipmv.util.IpmUserListModel;

/**
 * 設定ファイルからuser,host,nicknameを読み込み
 * @author hex
 *
 */
public class IpmFrame extends JFrame {


	// Ver(1) : Packet番号 : 自User名 : 自Host名 : Command番号 : 追加部
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<Long, MessageDialog> dialogMap = new HashMap<>();
	private JList<User> userList = new JList<>();
	private JCheckBox checkbox = new JCheckBox();

	private IpmAdapter ipmAdapter = new IpmAdapter("user1", "host1", "hoge", "groupName", false);

	public IpmFrame() {
		super("ipmv");
		initComponents();
	}

	/**
	 * ipmの初期化を実施.
	 * クライアントアプリによって作り方が変わる.
	 */
	private void initIpm() {
		// ここではリスナーの登録を実施。
		try {
			//メッセージ受信
			ipmAdapter.addListener(Mode.IPMSG_SENDMSG, message -> {
				MessageDialog dialog = new MessageDialog(this, false, message.is(Option.IPMSG_SECRETOPT), Arrays.asList(ipmAdapter.getUser(new User.Builder(message).build())), message.getPacketNo(), message.getBody());
				dialog.setVisible(true);
			});
			//メッセージ受信通知
			ipmAdapter.addListener(Mode.IPMSG_RECVMSG, message -> {
				long packetNo = Long.parseLong(message.getBody().trim());
				MessageDialog dialog = dialogMap.get(packetNo);
				dialogMap.remove(packetNo);
				dialog.dispose();
				System.out.println("dispose!");
			});
			//封書の開封通知
			ipmAdapter.addListener(Mode.IPMSG_READMSG, message -> {
				JOptionPane.showMessageDialog(this, "開封されました");
			});
			//封書の破棄通知
			ipmAdapter.addListener(Mode.IPMSG_DELMSG, message -> {
				JOptionPane.showMessageDialog(this, "破棄されました");
			});
			ipmAdapter.entry();//リスナー登録後にentryを実施
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * コンポーネント初期化.
	 */
	private void initComponents() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ipmAdapter.exit();
				dispose();
				System.exit(0);
			}
		});
		userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		userList.setModel(new IpmUserListModel(ipmAdapter.getIpm()));
		userList.addListSelectionListener((event) -> {
			System.out.println(event);
			if (event.getValueIsAdjusting()) {
				System.out.println(event.getFirstIndex());
			}
		});
		checkbox.setText("不在");
		checkbox.addActionListener((e)->{
			ipmAdapter.absence(checkbox.isSelected());
		});
		BorderLayout layout = new BorderLayout();
		getContentPane().setLayout(layout);
		getContentPane().add(new JScrollPane(userList), BorderLayout.CENTER);
		JPanel controlPanel = new JPanel();
		controlPanel.add(new JButton(new SendAction(this)));
		controlPanel.add(new JButton(new UpdateAction(this)));
		controlPanel.add(checkbox);
		getContentPane().add(controlPanel, BorderLayout.SOUTH);

		initIpm();
		pack();
	}

	public void openDialog() {
		// オブザーバで
		MessageDialog dialog = new MessageDialog(this, true, false, userList.getSelectedValuesList());
		dialog.setVisible(true);
	}

	/**
	 * メッセージ送信初回処理
	 * @param user
	 * @param text
	 */
	public void send(MessageDialog dialog, User user, String text, boolean secret, boolean b) {
		Message message = ipmAdapter.createMessage(user, text, secret, b);
		dialogMap.put(message.getPacketNo(), dialog);
		send(dialog, message);
	}
	/**
	 * メッセージ送信
	 * @param dialog
	 * @param message
	 */
	public void send(MessageDialog dialog, Message message) {
		ipmAdapter.send(message, ()->{
			try {
				Thread.sleep(3_000); // 3秒後
				if (dialogMap.containsKey(message.getPacketNo())) {
					dialog.alert(message);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * リスト更新.
	 */
	public void update() {
		ipmAdapter.update();
	}
	
	/**
	 * 通知.
	 * @param mode
	 * @param user
	 * @param packetNo
	 */
	public void notify(Mode mode, User user, long packetNo) {
		ipmAdapter.notify(mode, user, packetNo);
	}
}
