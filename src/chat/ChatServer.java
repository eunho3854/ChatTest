package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ChatServer {

	private static final String TAG = "ChatServer : ";
	private ServerSocket serverSocket;
	private Vector<ClientInfo> vc; // ����� Ŭ���̾�Ʈ Ŭ����(����)�� ��� �÷���

	public ChatServer() {
		try {
			vc = new Vector<>();
			serverSocket = new ServerSocket(10000);
			System.out.println(TAG + "Ŭ���̾�Ʈ ���� �����...");
			
			// ���� �������� ����
			while (true) {
				Socket socket = serverSocket.accept(); // Ŭ���̾�Ʈ ���� ���
				System.out.println(TAG + "Ŭ���̾�Ʈ ���� ����");
				ClientInfo clientInfo = new ClientInfo(socket);
				clientInfo.start();
				vc.add(clientInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ClientInfo extends Thread {

		Socket socket;
		BufferedReader reader;
		PrintWriter writer; // BufferedWriter�� �ٸ� ���� �������� �Լ��� ����

		public ClientInfo(Socket socket) {
			this.socket = socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer = new PrintWriter(socket.getOutputStream(), true);
			} catch (Exception e) {
				System.out.println("���� ���� ���� : " + e.getMessage());
			}
		}

		// ���� : Ŭ���̾�Ʈ�� ���� ���� �޽����� ��� Ŭ���̾�Ʈ���� ������
		// �޽����� �о ���ֱ�
		@Override
		public void run() {
			String input = null;
			try {
				while((input = reader.readLine()) != null) {
					
					for (int i = 0; i < vc.size(); i++ ) {
						if(vc.get(i) != this) {
							ClientInfo clientInfo = vc.get(i);
							clientInfo.writer.println(input);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		new ChatServer();
	}
}