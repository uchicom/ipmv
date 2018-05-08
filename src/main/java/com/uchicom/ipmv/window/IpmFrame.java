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

import com.uchicom.ipm.Ipm;
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

	private static String USER = "user1";
	private static String HOST = "host1";
	
	private static String NICKNAME = "hoge";

	// Ver(1) : Packet番号 : 自User名 : 自Host名 : Command番号 : 追加部
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Map<Long, MessageDialog> dialogMap = new HashMap<>();
	private JList<User> userList = new JList<>();
	private JCheckBox checkbox = new JCheckBox();

	private Ipm messanger = new Ipm(10000+(int)System.currentTimeMillis() % 10000);

	public IpmFrame() {
		super("ipmv");
		initComponents();
	}

	/**
	 * ipmの初期化を実施.
	 */
	private void initIpm() {

		// ここではリスナーの登録を実施。
		try {
			// メッセージ送信、受信以外をあらかじめ自動でやってくれるAdapterかDefaultも用意する。
			//サービスにエントリ（起動時にBroadcast）
			messanger.addListener(Mode.IPMSG_BR_ENTRY, message -> {
				System.out.println(message);
				User user = messanger.addUser(new User.Builder(message).nickName(message.getBody()).build());
				Message.Builder builder = new Message.Builder(user)
						.packetNo(messanger.issuePacketNo())
						.mode(Mode.IPMSG_ANSENTRY)
						.hostName(HOST)
						.userName(USER)
						.extra(NICKNAME);
				if (checkbox.isSelected()) {
					builder.option(Option.IPMSG_ABSENCEOPT);
				}
				//.extra() // ニックネームとか
				messanger.sendDelay(builder
				.build());
			});
			//エントリを認識したことを通知
			messanger.addListener(Mode.IPMSG_ANSENTRY, message -> {
				System.out.println(message);
				messanger.addUser(new User.Builder(message).nickName(message.getBody()).build());
			});
			//メッセージ返却
			messanger.addListener(Mode.IPMSG_RECVMSG, message -> {
				System.out.println("送信確認->" + message);
				long packetNo = Long.parseLong(message.getBody().trim());
				MessageDialog dialog = dialogMap.get(packetNo);
				dialogMap.remove(packetNo);
				dialog.dispose();
				System.out.println("dispose!");
			});
			//メッセージ受信
			messanger.addListener(Mode.IPMSG_SENDMSG, message -> {
				System.out.println("メッセージ->" + message.getBody());
				if (message.is(Mode.IPMSG_SENDMSG, Option.IPMSG_SENDCHECKOPT)) {
					messanger.send(new Message.Builder(new User.Builder(message).build())
							.packetNo(messanger.issuePacketNo())
							.mode(Mode.IPMSG_RECVMSG)
							.option(Option.IPMSG_AUTORETOPT)
							.hostName(HOST)
							.userName(USER)
							.extra(String.valueOf(message.getPacketNo()))
							.build());
				}
				MessageDialog dialog = new MessageDialog(this, false, Arrays.asList(new User.Builder(message).build()), message.getBody());
				dialog.setVisible(true);
			});
			//無操作
			messanger.addListener(Mode.IPMSG_NOOPERATION, message -> {
				System.out.println(message);
			});
			//不在モード変更
			messanger.addListener(Mode.IPMSG_BR_ABSENCE, message -> {
				System.out.println(message);
				messanger.changeUser(new User.Builder(message).build());
			});

			//ホストリスト送出可能メンバの探索
			messanger.addListener(Mode.IPMSG_BR_ISGETLIST, message -> {
				System.out.println(message);
			});
			//ホストリスト送出可能通知
			messanger.addListener(Mode.IPMSG_OKGETLIST , message -> {
				System.out.println(message);
				messanger.send(new Message.Builder(new User.Builder(message).build())
						.packetNo(messanger.issuePacketNo())
						.mode(Mode.IPMSG_GETLIST)
						.hostName(HOST)
						.userName(USER)
						.build());
			});
			//ホストリスト送出要求
			messanger.addListener(Mode.IPMSG_GETLIST, message -> {
				System.out.println(message);
			});
			//ホストリスト送出
			messanger.addListener(Mode.IPMSG_ANSLIST, message -> {
				System.out.println(message);
			});
			
			//封書の開封通知
			messanger.addListener(Mode.IPMSG_READMSG, message -> {
				System.out.println(message);
				JOptionPane.showMessageDialog(this, "開封されました");
			});
			//封書破棄通知
			messanger.addListener(Mode.IPMSG_DELMSG, message -> {
				System.out.println(message);
				JOptionPane.showMessageDialog(this, "破棄されました");
			});
			//封書の開封確認（8 版から追加）
			messanger.addListener(Mode.IPMSG_ANSREADMSG, message -> {
				System.out.println(message);
			});
			//IPMSGバージョン情報取得
			messanger.addListener(Mode.IPMSG_GETINFO, message -> {
				System.out.println(message);
			});
			//IPMSGバージョン情報応答
			messanger.addListener(Mode.IPMSG_SENDINFO, message -> {
				System.out.println(message);
			});
			//不在通知文取得
			messanger.addListener(Mode.IPMSG_GETABSENCEINFO, message -> {
				System.out.println(message);
			});
			//不在通知文応答
			messanger.addListener(Mode.IPMSG_SENDABSENCEINFO, message -> {
				System.out.println(message);
			});
			//サービスから抜ける（終了時にBroadcast）
			messanger.addListener(Mode.IPMSG_BR_EXIT, message -> {
				// ブロードキャストで受信する。
				System.out.println("削除");
				System.out.println(message);
				messanger.removeUser(new User.Builder(message).build());
			});
			
			messanger.addNetworkListener(broadcast->{
				messanger.send(new Message.Builder(new User.BroadcastBuilder().build())
						.packetNo(messanger.issuePacketNo())
						.mode(Mode.IPMSG_BR_ISGETLIST)
						.hostName(HOST)
						.userName(USER)
						.build());
			});
			messanger.start();

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					messanger.close();
					System.out.println("shutdown hook close!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}));
			entryIpm();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * エントリー
	 */
	private void entryIpm() {
		Message.Builder builder = new Message.Builder(new User.BroadcastBuilder().build())
				.packetNo(messanger.issuePacketNo())
				.mode(Mode.IPMSG_BR_ENTRY)
				.hostName(HOST)
				.userName(USER)
				.extra(NICKNAME);
		if (checkbox.isSelected()) {
			builder.option(Option.IPMSG_ABSENCEOPT);
		}
		messanger.send(builder
				.build());
	}

	/**
	 * 不在.
	 */
	private void absenceIpm() {

		Message.Builder builder = new Message.Builder(new User.BroadcastBuilder().build())
				.packetNo(messanger.issuePacketNo())
				.mode(Mode.IPMSG_BR_ABSENCE)
				.hostName(HOST)
				.userName(USER)
				.extra(NICKNAME);
		if (checkbox.isSelected()) {
			builder.option(Option.IPMSG_ABSENCEOPT);
		}
		messanger.send(builder
				.build());
	}
	/**
	 * 退出
	 */
	private void exitIpm() {
		messanger.send(new Message.Builder(new User.BroadcastBuilder().build())
				.packetNo(messanger.issuePacketNo())
				.mode(Mode.IPMSG_BR_EXIT)
				.hostName(HOST)
				.userName(USER)
				.extra(NICKNAME)
				.build());
	}

	/**
	 * コンポーネント初期化.
	 */
	private void initComponents() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitIpm();
				dispose();
				System.exit(0);
			}
		});
		userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		userList.setModel(new IpmUserListModel(messanger));
		userList.addListSelectionListener((event) -> {
			System.out.println(event);
			if (event.getValueIsAdjusting()) {
				System.out.println(event.getFirstIndex());
			}
		});
		checkbox.setText("不在");
		checkbox.addActionListener((e)->{
			absenceIpm();
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
		MessageDialog dialog = new MessageDialog(this, true, userList.getSelectedValuesList());
		dialog.setVisible(true);
	}

	/**
	 * メッセージ送信.
	 * @param user
	 * @param text
	 */
	public void send(MessageDialog dialog, User user, String text, boolean secret, boolean b) {
		long packetNo = messanger.issuePacketNo();
		dialogMap.put(packetNo, dialog);
		Message.Builder builder = new Message.Builder(user)
				.packetNo(packetNo)
				.mode(Mode.IPMSG_SENDMSG)
				.hostName(HOST)
				.userName(USER)
				.option(Option.IPMSG_SENDCHECKOPT)
				.extra(text);
		if (secret) {
			builder.option(Option.IPMSG_SECRETOPT);
		}
		messanger.send(builder
				.build());
		//アラートスレッド
		Thread alertThread = new Thread(()->{
			try {
				Thread.sleep(3_000); // 3秒後
				if (dialogMap.containsKey(packetNo)) {
					dialog.alert();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		alertThread.start();
	}
	
	public void update() {
		messanger.clearUser();
		
		entryIpm();
	}
}
