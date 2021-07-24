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
		System.out.println("서버가 실행됩니다.");

		try {
			socket = new ServerSocket(SERVER_PORT);

		} catch (IOException e) {
			System.err.println(SERVER_PORT + " 가 사용중입니다.");
			System.exit(-1);
		}

		while (true) {
			try {
				System.out.println("사용자의 접속을 기다립니다. ");
				Socket client = socket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

				String clientIp = client.getInetAddress().getHostAddress();

				System.out.println(clientIp + " 님이 접속하였습니다.");

				String messageFromClient = in.readLine();

				if (messageFromClient == null) {
					System.out.println(clientIp + " 님이 종료하였습니다.");
				} else {
					System.out.println(clientIp + " 사용자로부터 받은 메시지 : " + messageFromClient);

					// 클라이언트로부터 받은 메시지가 JSON 포맷의 String 인 경우, 아래의 방법으로 JSON 객체로 변환이 가능.
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
						// 올바르지 않은 경우 에러를 리턴한다.1
						out.println("에러메시지/올바르지 않은 포맷의 메시지 입니다.");
						out.flush();
					}
				}

				client.close();

				System.out.println(clientIp + " 와의 접속을 종료합니다.");
			} catch (IOException e) {
			}
		}
	}

	// Service 에 바로 접근할 수 없기 때문에 호출하는 함수를 별도로 생성.
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
