import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;
import java.util.Scanner;

public class ClientMain {
	private static String userid; // �α��ε� ���̵�
	private static String userpasswd; // �α��ε� �н�����

	public static void main(String[] args) {
		try {
			boolean loggedin = false;
			JSONObject data = new JSONObject();
			Scanner scan = new Scanner(System.in);

			while (true) {
				Socket client = new Socket("localhost", 6006);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

				if (!loggedin) { // �α��� ��
					System.out.print("1.���� 2.ȸ������ 3.����  -->");
					int menu = scan.nextInt();
					if (menu == 1) {
						System.out.print("���̵�: ");
						String id = scan.next();
						System.out.print("��й�ȣ: ");
						String passwd = scan.next();

						userid = id;
						userpasswd = passwd;

						data.put("command", "login");
						data.put("id", id);
						data.put("passwd", passwd);
					} else if (menu == 2) {
						System.out.print("���̵�: ");
						String id = scan.next();
						System.out.print("��й�ȣ: ");
						String passwd = scan.next();
						System.out.print("�̸�: ");
						String name = scan.next();
						System.out.println("������� 6�ڸ�");
						String birthdate = scan.next();

						data.put("command", "register");
						data.put("id", id);
						data.put("passwd", passwd);
						data.put("name", name);
						data.put("birthdate", birthdate);
					} else if (menu == 3) {
						System.out.println("�����մϴ�");
						break;
					} else {
						System.out.println("�Է¿���");
					}

					out.println(data.toString());
					out.flush();

					JSONObject result = new JSONObject(in.readLine());

					String command = result.getString("command");
					boolean res = result.getBoolean("result");
					String fromMessage = result.getString("message");

					if (command.equals("login")) {
						if (res) {
							System.out.println("�α��� ���� ");
							loggedin = true;

							String cabinets = result.getString("cabinets");
							System.out.print("   ");
							for (int i = 0; i < 10; i++) {
								System.out.printf("%4d      ", i);
							}
							System.out.println();
							for (int i = 0; i < 5; i++) {
								System.out.printf("%2d ", i * 10);
								for (int j = 0; j < 10; j++) {
									if (cabinets.charAt(i * 10 + j) == 'T') {
										System.out.print("��밡��  ");
									} else {
										System.out.print("���Ұ�  ");
									}
								}
								System.out.println();
							}
							while (true) {
								client.close();
								client = new Socket("localhost", 6006);
								in = new BufferedReader(new InputStreamReader(client.getInputStream()));
								out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

								System.out.print("ĳ��� ��ȣ�� �������ּ���(0~49) : ");
								int cabinet = scan.nextInt();
								if (cabinet <= 0 || cabinet >= 100) {
									System.out.println("ĳ��� ��ȣ�� �ٽ� �Է����ּ���");
									continue;
								}

								data.put("command", "CabinetSelect");
								data.put("cabinet", cabinet);

								out.println(data.toString());
								out.flush();

								result = new JSONObject(in.readLine());

								if (result.getBoolean("result")) {
									System.out.println("ĳ��� ������ �Ϸ�Ǿ����ϴ�.");
									break;
								} else {
									System.out.println("�̹� ������� �ڸ��Դϴ�.");
								}
							}
						} else {
							System.out.println("�α��� ���� : " + fromMessage);
							userid = null;
							userpasswd = null;
						}
					} else if (command.equals("register")) {
						if (res) {
							System.out.println("ȸ������ ����");
						} else {
							System.out.println("ȸ������ ���� : " + fromMessage);
						}
					} else {
						System.out.println("�ùٸ��� ���� ��ɾ��Դϴ�.");
					}
				} else {// �α��� ��
					System.out.println("  ����� �޴� ����");
					System.out.print("1.����� 2.����  3.���ݾ� Ȯ��  4.���� -->");
					int menu = scan.nextInt();
					if (menu == 1) {
						client.close();
						SleepRoom();
					} else if (menu == 2) {
						client.close();
						SnackBar();
					} else if (menu == 3) {
						client.close();
						getPrice();
					} else if (menu == 4) {
						data.put("command", "checkout");
						data.put("id", userid);
						data.put("passwd", userpasswd);

						out.println(data.toString());
						out.flush();

						JSONObject result = new JSONObject(in.readLine());

						if (result.getBoolean("result")) { // �α׾ƿ� ����
							System.out.println("����� " + result.getInt("TotalPrice") + "�� �Դϴ�.");
							loggedin = false;
							userid = null;
							userpasswd = null;
						} else {
							System.out.println("���� ����: " + result.getString("message"));
						}
					} else {
						System.out.println("�Է� ����");
					}
				}
				client.close();
			} // while

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void SleepRoom() throws UnknownHostException, IOException {
		Scanner scan = new Scanner(System.in);

		while (true) {
			Socket client = new Socket("localhost", 6006);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
			JSONObject data = new JSONObject();

			System.out.println("   ����� �޴��Դϴ�.");
			System.out.print("1.���� �ڸ����� ���   2.�Խ�   3.���  4.���ư��� --> ");
			int sleepmenu = scan.nextInt();

			data.put("command", "sleeproom");
			data.put("sleepmenu", sleepmenu);

			if (sleepmenu == 1) {
				out.println(data.toString());
				out.flush();

				JSONObject result = new JSONObject(in.readLine());
				String sleeprooms = result.getString("sleeprooms");

				System.out.print("   ");
				for (int i = 0; i < 10; i++) {
					System.out.printf("%4d      ", i);
				}
				System.out.println();
				for (int i = 0; i < 2; i++) {
					System.out.printf("%2d ", i * 10);
					for (int j = 0; j < 10; j++) {
						if (sleeprooms.charAt(i * 10 + j) == 'T') {
							System.out.print("��밡��  ");
						} else {
							System.out.print("���Ұ�  ");
						}
					}
					System.out.println();
				}

			} else if (sleepmenu == 2) {
				while (true) {
					System.out.print("����� ��ȣ �Է�(0~19): ");
					int SleepRoomNum = scan.nextInt();
					if (SleepRoomNum < 0 || SleepRoomNum > 50) {
						System.out.println("�ٽ� �Է����ּ���");
					} else {
						data.put("SleepRoomNumSelect", SleepRoomNum);
						break;
					}
				}

				data.put("id", userid);
				data.put("passwd", userpasswd);

				out.println(data.toString());
				out.flush();

				JSONObject result = new JSONObject(in.readLine());

				if (result.getBoolean("result")) {
					System.out.println("�Խ��� �Ϸ�Ǿ����ϴ�.");
				} else {
					System.out.println("�Խ� ����: " + result.getString("message"));
				}
			} else if (sleepmenu == 3) {
				data.put("id", userid);
				data.put("passwd", userpasswd);

				out.println(data.toString());
				out.flush();

				JSONObject result = new JSONObject(in.readLine());

				if (result.getBoolean("result")) {
					System.out.println("����� �Ϸ�Ǿ����ϴ�.");
				} else {
					System.out.println("��� ����: " + result.getString("message"));
				}
			} else if (sleepmenu == 4) {
				client.close();
				return;
			} else {
				System.out.println("�߸� �Է��ϼ̽��ϴ�");
				continue;
			}
			client.close();
		}

	}

	public static void SnackBar() throws UnknownHostException, IOException {
		Scanner scan = new Scanner(System.in);

		while (true) {
			Socket client = new Socket("localhost", 6006);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
			JSONObject data = new JSONObject();

			data.put("command", "SnackBar");
			data.put("id", userid);
			data.put("passwd", userpasswd);

			System.out.println("   ���� �̿� �޴��Դϴ�.");
			System.out.println("�޴� : 1.Ŀ��   2.����   3.������� 4.�Ŷ�� 5.���ư���");
			System.out.println("���� : 2000��  3000��      1000��  2000�� ");
			int SnackMenu = scan.nextInt();
			data.put("SnackMenu", SnackMenu);
			if (SnackMenu == 5) {
				client.close();
				return;
			}

			System.out.print("������ �Է��ϼ��� --> ");
			int SnackCount = scan.nextInt();
			data.put("SnackCount", SnackCount);

			out.println(data.toString());
			out.flush();

			JSONObject result = new JSONObject(in.readLine());

			System.out.println("�� �ݾ��� : " + result.getInt("price") + "�Դϴ�.");
			client.close();
		}
	}

	public static void getPrice() throws UnknownHostException, IOException {
		Socket client = new Socket("localhost", 6006);
		BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
		JSONObject data = new JSONObject();

		data.put("command", "getPrice");
		data.put("id", userid);
		data.put("passwd", userpasswd);

		out.println(data.toString());
		out.flush();

		JSONObject result = new JSONObject(in.readLine());

		System.out.println("���� ���ݾ���: " + result.getInt("price") + "�� �Դϴ�.");

		client.close();
	}
}
