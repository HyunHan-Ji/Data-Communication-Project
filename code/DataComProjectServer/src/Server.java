import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

public class Server extends Thread {
	private Service service = new Service();
	private ServerSocket socket = null;
	private int SERVER_PORT = 6006;

	public Server(int port) {
		SERVER_PORT = port;
		service.start();
	}

	@Override
	public void run() {
		System.out.println("������ ����˴ϴ�.");

		try {
			socket = new ServerSocket(SERVER_PORT);

		} catch (IOException e) {
			System.err.println(SERVER_PORT + " �� ������Դϴ�.");
			System.exit(-1);
		}

		while (true) {
			try {
				System.out.println("������� ������ ��ٸ��ϴ�. ");
				Socket client = socket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

				String clientIp = client.getInetAddress().getHostAddress();

				System.out.println(clientIp + " ���� �����Ͽ����ϴ�.");

				String messageFromClient = in.readLine();

				if (messageFromClient == null) {
					System.out.println(clientIp + " ���� �����Ͽ����ϴ�.");
				} else {
					System.out.println(clientIp + " ����ڷκ��� ���� �޽��� : " + messageFromClient);

					// Ŭ���̾�Ʈ�κ��� ���� �޽����� JSON ������ String �� ���, �Ʒ��� ������� JSON ��ü�� ��ȯ�� ����.
					JSONObject messages = new JSONObject(messageFromClient);

					String command = messages.getString("command");

					if (command.equals("register")) {
						String id = messages.getString("id");
						String passwd = messages.getString("passwd");
						String name = messages.getString("name");
						String birthdate = messages.getString("birthdate");

						String result = service.register(new Member(id, passwd, name, birthdate));

						out.println(result);
						out.flush();
					} else if (command.equals("login")) {
						String id = messages.getString("id");
						String passwd = messages.getString("passwd");

						String result = service.login(new Member(id, passwd));

						out.println(result);
						out.flush();
					} else if (command.equals("checkout")) {
						String id = messages.getString("id");
						String passwd = messages.getString("passwd");

						String result = service.checkout(new Member(id, passwd));

						out.println(result);
						out.flush();
					} else if (command.equals("CabinetSelect")) {
						String id = messages.getString("id");
						String passwd = messages.getString("passwd");
						int cabinet = messages.getInt("cabinet");

						String result = service.CabinetSelect(new Member(id, passwd), cabinet);

						out.println(result);
						out.flush();
					} else if (command.equals("sleeproom")) {
						String result = null;
						if (messages.getInt("sleepmenu") == 1) {
							result = service.returnSleepRooms();
						}

						else if (messages.getInt("sleepmenu") == 2) {
							String id = messages.getString("id");
							String passwd = messages.getString("passwd");
							int SleepRoomNumSelect = messages.getInt("SleepRoomNumSelect");

							result = service.SleepRoomIn(new Member(id, passwd), SleepRoomNumSelect);
						}

						else if (messages.getInt("sleepmenu") == 3) {
							String id = messages.getString("id");
							String passwd = messages.getString("passwd");

							result = service.SleepRoomOut(new Member(id, passwd));
						}

						out.println(result);
						out.flush();
					} else if (command.equals("SnackBar")) {
						String id = messages.getString("id");
						String passwd = messages.getString("passwd");
						int SnackMenu = messages.getInt("SnackMenu");
						int SnackCount = messages.getInt("SnackCount");

						out.println(service.SnackBar(new Member(id, passwd), SnackMenu, SnackCount));
						out.flush();
					} else if (command.equals("getPrice")) {
						String id = messages.getString("id");
						String passwd = messages.getString("passwd");

						out.println(service.getPrice(new Member(id, passwd)));
						out.flush();
					} else {
						// �ùٸ��� ���� ��� ������ �����Ѵ�.1
						out.println("�����޽���/�ùٸ��� ���� ������ �޽��� �Դϴ�.");
						out.flush();
					}
				}

				client.close();

				System.out.println(clientIp + " ���� ������ �����մϴ�.");
			} catch (IOException e) {
			}
		}
	}

	// Service �� �ٷ� ������ �� ���� ������ ȣ���ϴ� �Լ��� ������ ����.
	public void showUserList() {
		service.showUserList();
	}

	public int getSales() {
		return service.getSales();
	}

	public String[][] getSleepRoom() {
		return service.getSleepRoom();
	}
	public String[][] getCabinet(){
		return service.getCabinet();
	}
}
