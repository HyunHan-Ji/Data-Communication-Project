import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ServerMain {
	public static void main(String[] args) {
		Server server = new Server(6006);
		server.start();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			try {
				String command = in.readLine();
				System.out.println("�Է��� Ŀ��� : " + command);

				if (command.equals("/list")) {
					server.showUserList();
				} else if (command.equals("/sales")) {
					System.out.println("���� ������ " + server.getSales() + "�� �Դϴ�");
					// ������ �մ��� ����� �ջ��
				} else if (command.equals("/sleeproom")) {
					String[][] SleepRooms = server.getSleepRoom();

					for (int i = 0; i < SleepRooms.length; i++) {
						for (int j = 0; j < SleepRooms[i].length; j++) {
							if (SleepRooms[i][j] == null) {
								System.out.print("������� ");
							} else {
								System.out.printf("%s ", SleepRooms[i][j]);
							}
						}
						System.out.println();
					}
				}else if(command.equals("/cabinet")) {
					String[][] cabines=server.getCabinet();
					
					for (int i = 0; i < cabines.length; i++) {
						for (int j = 0; j < cabines[i].length; j++) {
							if (cabines[i][j] == null) {
								System.out.print("������� ");
							} else {
								System.out.printf("%s ", cabines[i][j]);
							}
						}
						System.out.println();
					}
				}
			} catch (Exception e) {
			}
		}
	}
}
