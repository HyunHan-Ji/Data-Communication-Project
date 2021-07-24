import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class Service extends Thread {
	private ArrayList<Member> members = new ArrayList<Member>();
	private FileModule fileModule = new FileModule();
	private int sales = 0; // ����
	String[][] cabinets = new String[5][10]; // ĳ���
	String[][] SleepRooms = new String[2][10]; // �����

	public Service() {
		members = fileModule.loadMemberInfo();
	}

	private boolean checkMember(String id) {
		boolean result = false;

		for (Member member : members) {
			if (member.getId().equals(id)) {
				result = true;
				break;
			}
		}

		return result;
	}

	public static final int RESULT_OK = 0;
	public static final int RESULT_ERR_PASSWD = 1;
	public static final int RESULT_ERR_ID = 2;
	public static final int RESULT_ERR_ALREADY_LOGIN = 3;

	private int checkLogin(Member member) {
		int result = RESULT_ERR_ID;

		synchronized (members) {
			for (Member m : members) {
				if (m.getId().equals(member.getId())) {
					if (m.getPasswd().equals(member.getPasswd())) {
						if (m.isLoggedin()) {
							result = RESULT_ERR_ALREADY_LOGIN;
						} else {
							result = RESULT_OK;
						}
					} else {
						result = RESULT_ERR_PASSWD;
					}
					break;
				}
			}
		}

		return result;
	}

	public String register(Member member) {
		JSONObject result = new JSONObject();
		result.put("command", "register");

		if (!checkMember(member.getId())) {
			members.add(member);

			boolean res = fileModule.saveMemberInfo(members);

			if (res) {
				result.put("result", true);
				result.put("message", "ȸ�����Կ� �����Ͽ����ϴ�.");
				return result.toString();
			} else {
				result.put("result", false);
				result.put("message", "ȸ�����Կ� ������ �߻��Ͽ����ϴ�(�����ڵ�:2).");
				return result.toString();
			}
		} else {
			result.put("result", false);
			result.put("message", "�̹� �����ϴ� ���̵��Դϴ�.");
			return result.toString();
		}
	}

	// �Ű������� ���� member�� �����ͺ��̽��� member�� ã�� �޼ҵ�
	public Member finduser(Member member) {
		synchronized (members) {
			for (int i = 0; i < members.size(); ++i) {
				if (members.get(i).getId().equals(member.getId())) {
					return members.get(i);
				}
			}
		}
		return null;
	}

	public String login(Member member) {
		int result = checkLogin(member);

		JSONObject returnResult = new JSONObject();
		returnResult.put("command", "login");

		if (result == RESULT_OK) {
			Member user = finduser(member);

			user.setLoggedin(true);
			user.setOccupiedDate(new SimpleDateFormat("dd").format(new Date()));

			// �����϶�,�̼������϶� ����� �ٸ�
			if ((Integer.parseInt(user.getBirthdate().substring(0, 2)) < 99)
					|| (user.getBirthdate().substring(0, 2).equals("00"))) { // �����϶�
				user.setPrice(10000);
			} else {
				user.setPrice(8000);

			}

			// ���� ĳ��� ��Ȳ�� ���ڿ��� �ٲ㼭 ������, ����� F �̻���� T
			String returncabinets = "";
			for (int i = 0; i < cabinets.length; i++) {
				for (int j = 0; j < cabinets[i].length; j++) {
					if (cabinets[i][j] == null) {
						returncabinets += "T";
					} else {
						returncabinets += "F";
					}
				}
			}

			returnResult.put("cabinets", returncabinets);
			returnResult.put("result", true);
			returnResult.put("message", "�α��ο� �����Ͽ����ϴ�.");
		} else if (result == RESULT_ERR_PASSWD) {
			returnResult.put("result", false);
			returnResult.put("message", "��й�ȣ�� ��ġ���� �ʽ��ϴ�");
		} else if (result == RESULT_ERR_ID) {
			returnResult.put("result", false);
			returnResult.put("message", "���̵� �������� �ʽ��ϴ�.");
		} else if (result == RESULT_ERR_ALREADY_LOGIN) {
			returnResult.put("result", false);
			returnResult.put("message", "�̹� �α����� �����Դϴ�.");
		}

		return returnResult.toString();
	}

	public String checkout(Member member) {
		Member user = finduser(member);
		JSONObject returnResult = new JSONObject();

		if (member.getSleepRoom() == -1) {	//����� ������̿������� Ȯ�� 
			int end = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
			int start = Integer.parseInt(user.getOccupiedDate());
			int price = (end - start) * 8000; // ��¥�� ������ ��� �߰�
			price += user.getPrice();

			user.setLoggedin(false);
			user.setOccupiedDate("");
			user.setPrice(0);
			cabinets[user.getCabinet() / 10][user.getCabinet() % 10] = null; // ������� ĳ��� �����
			

			sales += price; // ���� �߰�
			returnResult.put("result", true);
			returnResult.put("message", "��� �Ϸ�");
			returnResult.put("TotalPrice", price);
		} else {
			returnResult.put("result", false);
			returnResult.put("message", "������� ����ؾ��մϴ�.");
		}

		return returnResult.toString();

	}

	// �α��ν� ĳ����� �����ϴ� �޼ҵ�
	public String CabinetSelect(Member member, int cabinet) {
		JSONObject returnResult = new JSONObject();
		Member user = finduser(member);

		if (cabinets[cabinet / 10][cabinet % 10] == null) {
			user.setCabinet(cabinet);
			returnResult.put("result", true);
			returnResult.put("message", "�ڸ����� ����");
			cabinets[cabinet / 10][cabinet % 10] = user.getId();

		} else {
			returnResult.put("result", false);
			returnResult.put("message", "������� �ڸ��Դϴ�");
		}
		return returnResult.toString();
	}

	// ���� ����� ������ �����ִ� �޼ҵ�
	public String returnSleepRooms() {
		String ReturnSleepRooms = "";
		for (int i = 0; i < SleepRooms.length; i++) {
			for (int j = 0; j < SleepRooms[i].length; j++) {
				if (SleepRooms[i][j] == null) {
					ReturnSleepRooms += "T";
				} else {
					ReturnSleepRooms += "F";
				}
			}
		}
		JSONObject returnResult = new JSONObject();
		returnResult.put("sleeprooms", ReturnSleepRooms);

		return returnResult.toString();
	}

	// ����� �Խ� �޼ҵ�
	public String SleepRoomIn(Member member, int SleepRoomNumSelect) {
		JSONObject returnResult = new JSONObject();
		Member user = finduser(member);

		if (!user.isUsedSleepRoom()) {
			if (SleepRooms[SleepRoomNumSelect / 10][SleepRoomNumSelect % 10] != null) {// ������ ���� �̹� ������϶�
				returnResult.put("result", false);
				returnResult.put("message", "������� ���Դϴ�");
			} else {
				returnResult.put("result", true);
				SleepRooms[SleepRoomNumSelect / 10][SleepRoomNumSelect % 10] = user.getId();
				user.setSleepRoom(SleepRoomNumSelect);

			}
		} else { // ������ ������� �̹� �̿����϶�
			returnResult.put("result", false);
			returnResult.put("message", "������� �̹� ������Դϴ�.");
		}

		return returnResult.toString();
	}

	// ����� ��� �޼ҵ�
	public String SleepRoomOut(Member member) {
		JSONObject returnResult = new JSONObject();
		Member user = finduser(member);

		if (user.isUsedSleepRoom()) {
			SleepRooms[user.getSleepRoom() / 10][user.getSleepRoom() % 10] = null;
			user.setSleepRoom(-1);
			returnResult.put("result", true);
		} else {
			returnResult.put("result", false);
			returnResult.put("message", "������� ��������� �ʽ��ϴ�.");
		}

		return returnResult.toString();
	}

	// ���� �޼ҵ�
	public String SnackBar(Member member, int SnackMenu, int SnackCount) {
		JSONObject returnResult = new JSONObject();
		Member user = finduser(member);

		int[] SnackPrices = { 0, 2000, 3000, 1000, 2000 };

		int total = SnackPrices[SnackMenu] * SnackCount;
		user.setPrice(user.getPrice() + total);

		returnResult.put("price", total);

		return returnResult.toString();
	}

	// ������ ���� ���ݾ��� �����ִ� �޼ҵ�
	public String getPrice(Member member) {
		Member user = finduser(member);
		JSONObject returnResult = new JSONObject();
		returnResult.put("price", user.getPrice());

		return returnResult.toString();
	}

	// ���� Ŀ�ǵ带 ���� �޼ҵ��
	public void showUserList() {
		for (int i = 0; i < members.size(); ++i) {
			System.out.println(members.get(i).getId());
		}
	}

	public int getSales() {
		return sales;
	}

	public String[][] getSleepRoom() {
		return SleepRooms;
	}

	public String[][] getCabinet() {
		return cabinets;
	}

	@Override
	public void run() {
		// 1�ʸ��� ����ǵ���
		while (true) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
