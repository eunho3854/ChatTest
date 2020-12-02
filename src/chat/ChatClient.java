package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame {

	private final static String TAG = "ChatClient : ";
	private ChatClient chatClient = this;
	private static final int PORT = 10000;
	private JButton btnConnect, btnSend;
	private JTextField tfHost, tfChat;
	private JTextArea taChatList;
	private ScrollPane scrollPane;

	private JPanel topPanel, bottomPanel;
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;

	public ChatClient() {
		init();
		setting();
		batch();
		listener();
		setVisible(true);
		
	}
	
	private void text() {
		File file = new File("D:\\workspace\\javatest\\chatProject\\output.text");
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(taChatList.getText());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void init() {
		btnConnect = new JButton("Connect");
		btnSend = new JButton("Send");
		tfHost = new JTextField("127.0.0.1", 20); // 뒤에는 사이즈 20글자까지 쓸 수 있음.
		tfChat = new JTextField(20);
		taChatList = new JTextArea(10, 30); // row, column
		scrollPane = new ScrollPane();
		topPanel = new JPanel();
		bottomPanel = new JPanel();
	}

	private void setting() {
		setTitle("채팅 다대다 클라이언트");
		setSize(350, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // 가운데로 배치.
		taChatList.setBackground(Color.ORANGE);
		taChatList.setForeground(Color.BLUE);
	}

	private void batch() {
		topPanel.add(tfHost);
		topPanel.add(btnConnect);
		bottomPanel.add(tfChat);
		bottomPanel.add(btnSend);
		scrollPane.add(taChatList);

		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void listener() {
		btnConnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}

		});

		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				text();
				System.exit(0);
			}
		});
	}

	// x 버튼 눌러서 창을 끌때 파일로 저장
	private void send() {
		String chat = tfChat.getText();
		// 1번 taChatList 뿌리기
		taChatList.append("[내 메시지] " + chat + "\n"); // setText()는 문자가 계속 쌓임, append는 뒤에 문자가 옴
		// 2번 서버로 전송
		String input = tfChat.getText();
		writer.write(input + "\n");
		writer.flush();
		// 3번 tfChat 비우기
		tfChat.setText("");
		
	}

	private void connect() {
		String host = tfHost.getText();
		try {
			socket = new Socket(host, PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
			ReaderThread rt = new ReaderThread();
			rt.start();
		} catch (Exception e1) {
			System.out.println(TAG + "서버 연결 에러 : " + e1.getMessage());
		}
	}

	class ReaderThread extends Thread {

		// while을 돌면서 서버로 부터 메시지를 받아서 taChatList에 뿌리기 (reader.readline)
		@Override
		public void run() {

			
			try {
				while (true) {
					String input = null;
					if((input = reader.readLine()) != null) {
						taChatList.append("[상대방 메시지] " + input + "\n");
					}
					System.out.println("출력");
				}
			} catch (IOException e) {
				System.out.println(TAG + "안됨");
			}

		}
	}

	public static void main(String[] args) {
		new ChatClient();	
	}
}
