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
	private static String userid; // 로그인된 아이디
	private static String userpasswd; // 로그인된 패스워드

	public static void main(String[] args) {
		try {
			boolean loggedin = false;
			JSONObject data = new JSONObject();
			Scanner scan = new Scanner(System.in);

			while (true) {
				Socket client = new Socket("localhost", 6006);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

				if (!loggedin) { // 로그인 전
					System.out.print("1.입장 2.회원가입 3.종료  -->");
					int menu = scan.nextInt();
					if (menu == 1) {
						System.out.print("아이디: ");
						String id = scan.next();
						System.out.print("비밀번호: ");
						String passwd = scan.next();

						userid = id;
						userpasswd = passwd;

						data.put("command", "login");
						data.put("id", id);
						data.put("passwd", passwd);
					} else if (menu == 2) {
						System.out.print("아이디: ");
						String id = scan.next();
						System.out.print("비밀번호: ");
						String passwd = scan.next();
						System.out.print("이름: ");
						String name = scan.next();
						System.out.println("생년월일 6자리");
						String birthdate = scan.next();

						data.put("command", "register");
						data.put("id", id);
						data.put("passwd", passwd);
						data.put("name", name);
						data.put("birthdate", birthdate);
					} else if (menu == 3) {
						System.out.println("종료합니다");
						break;
					} else {
						System.out.println("입력오류");
					}

					out.println(data.toString());
					out.flush();

					JSONObject result = new JSONObject(in.readLine());

					String command = result.getString("command");
					boolean res = result.getBoolean("result");
					String fromMessage = result.getString("message");

					if (command.equals("login")) {
						if (res) {
							System.out.println("로그인 성공 ");
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
										System.out.print("사용가능  ");
									} else {
										System.out.print("사용불가  ");
									}
								}
								System.out.println();
							}
							while (true) {
								client.close();
								client = new Socket("localhost", 6006);
								in = new BufferedReader(new InputStreamReader(client.getInputStream()));
								out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

								System.out.print("캐비넷 번호를 선택해주세요(0~49) : ");
								int cabinet = scan.nextInt();
								if (cabinet <= 0 || cabinet >= 100) {
									System.out.println("캐비넷 번호를 다시 입력해주세요");
									continue;
								}

								data.put("command", "CabinetSelect");
								data.put("cabinet", cabinet);

								out.println(data.toString());
								out.flush();

								result = new JSONObject(in.readLine());

								if (result.getBoolean("result")) {
									System.out.println("캐비넷 선택이 완료되었습니다.");
									break;
								} else {
									System.out.println("이미 사용중인 자리입니다.");
								}
							}
						} else {
							System.out.println("로그인 실패 : " + fromMessage);
							userid = null;
							userpasswd = null;
						}
					} else if (command.equals("register")) {
						if (res) {
							System.out.println("회원가입 성공");
						} else {
							System.out.println("회원가입 실패 : " + fromMessage);
						}
					} else {
						System.out.println("올바르지 않은 명령어입니다.");
					}
				} else {// 로그인 후
					System.out.println("  사용할 메뉴 선택");
					System.out.print("1.수면실 2.매점  3.사용금액 확인  4.퇴장 -->");
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

						if (result.getBoolean("result")) { // 로그아웃 성공
							System.out.println("요금은 " + result.getInt("TotalPrice") + "원 입니다.");
							loggedin = false;
							userid = null;
							userpasswd = null;
						} else {
							System.out.println("퇴장 실패: " + result.getString("message"));
						}
					} else {
						System.out.println("입력 오류");
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

			System.out.println("   수면실 메뉴입니다.");
			System.out.print("1.현재 자리내역 출력   2.입실   3.퇴실  4.돌아가기 --> ");
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
							System.out.print("사용가능  ");
						} else {
							System.out.print("사용불가  ");
						}
					}
					System.out.println();
				}

			} else if (sleepmenu == 2) {
				while (true) {
					System.out.print("수면실 번호 입력(0~19): ");
					int SleepRoomNum = scan.nextInt();
					if (SleepRoomNum < 0 || SleepRoomNum > 50) {
						System.out.println("다시 입력해주세요");
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
					System.out.println("입실이 완료되었습니다.");
				} else {
					System.out.println("입실 실패: " + result.getString("message"));
				}
			} else if (sleepmenu == 3) {
				data.put("id", userid);
				data.put("passwd", userpasswd);

				out.println(data.toString());
				out.flush();

				JSONObject result = new JSONObject(in.readLine());

				if (result.getBoolean("result")) {
					System.out.println("퇴실이 완료되었습니다.");
				} else {
					System.out.println("퇴실 실패: " + result.getString("message"));
				}
			} else if (sleepmenu == 4) {
				client.close();
				return;
			} else {
				System.out.println("잘못 입력하셨습니다");
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

			System.out.println("   매점 이용 메뉴입니다.");
			System.out.println("메뉴 : 1.커피   2.식혜   3.삶은계란 4.컵라면 5.돌아가기");
			System.out.println("가격 : 2000원  3000원      1000원  2000원 ");
			int SnackMenu = scan.nextInt();
			data.put("SnackMenu", SnackMenu);
			if (SnackMenu == 5) {
				client.close();
				return;
			}

			System.out.print("개수를 입력하세요 --> ");
			int SnackCount = scan.nextInt();
			data.put("SnackCount", SnackCount);

			out.println(data.toString());
			out.flush();

			JSONObject result = new JSONObject(in.readLine());

			System.out.println("총 금액은 : " + result.getInt("price") + "입니다.");
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

		System.out.println("현재 사용금액은: " + result.getInt("price") + "원 입니다.");

		client.close();
	}
}
