import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class Service extends Thread {
	private ArrayList<Member> members = new ArrayList<Member>();
	private FileModule fileModule = new FileModule();
	private int sales = 0; // 매출
	String[][] cabinets = new String[5][10]; // 캐비넷
	String[][] SleepRooms = new String[2][10]; // 수면실

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
				result.put("message", "회원가입에 성공하였습니다.");
				return result.toString();
			} else {
				result.put("result", false);
				result.put("message", "회원가입에 문제가 발생하였습니다(에러코드:2).");
				return result.toString();
			}
		} else {
			result.put("result", false);
			result.put("message", "이미 존재하는 아이디입니다.");
			return result.toString();
		}
	}

	// 매개변수로 들어온 member로 데이터베이스의 member를 찾는 메소드
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

			// 성인일때,미성년자일때 요금이 다름
			if ((Integer.parseInt(user.getBirthdate().substring(0, 2)) < 99)
					|| (user.getBirthdate().substring(0, 2).equals("00"))) { // 성인일때
				user.setPrice(10000);
			} else {
				user.setPrice(8000);

			}

			// 현재 캐비넷 상황을 문자열로 바꿔서 보내줌, 사용중 F 미사용중 T
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
			returnResult.put("message", "로그인에 성공하였습니다.");
		} else if (result == RESULT_ERR_PASSWD) {
			returnResult.put("result", false);
			returnResult.put("message", "비밀번호가 일치하지 않습니다");
		} else if (result == RESULT_ERR_ID) {
			returnResult.put("result", false);
			returnResult.put("message", "아이디가 존재하지 않습니다.");
		} else if (result == RESULT_ERR_ALREADY_LOGIN) {
			returnResult.put("result", false);
			returnResult.put("message", "이미 로그인한 유저입니다.");
		}

		return returnResult.toString();
	}

	public String checkout(Member member) {
		Member user = finduser(member);
		JSONObject returnResult = new JSONObject();

		if (member.getSleepRoom() == -1) {	//퇴장시 수면실이용중인지 확인 
			int end = Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
			int start = Integer.parseInt(user.getOccupiedDate());
			int price = (end - start) * 8000; // 날짜가 지나면 요금 추가
			price += user.getPrice();

			user.setLoggedin(false);
			user.setOccupiedDate("");
			user.setPrice(0);
			cabinets[user.getCabinet() / 10][user.getCabinet() % 10] = null; // 사용중인 캐비넷 비워줌
			

			sales += price; // 매출 추가
			returnResult.put("result", true);
			returnResult.put("message", "퇴실 완료");
			returnResult.put("TotalPrice", price);
		} else {
			returnResult.put("result", false);
			returnResult.put("message", "수면실을 퇴실해야합니다.");
		}

		return returnResult.toString();

	}

	// 로그인시 캐비넷을 선택하는 메소드
	public String CabinetSelect(Member member, int cabinet) {
		JSONObject returnResult = new JSONObject();
		Member user = finduser(member);

		if (cabinets[cabinet / 10][cabinet % 10] == null) {
			user.setCabinet(cabinet);
			returnResult.put("result", true);
			returnResult.put("message", "자리선택 성공");
			cabinets[cabinet / 10][cabinet % 10] = user.getId();

		} else {
			returnResult.put("result", false);
			returnResult.put("message", "사용중인 자리입니다");
		}
		return returnResult.toString();
	}

	// 현재 수면실 내역을 보내주는 메소드
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

	// 수면실 입실 메소드
	public String SleepRoomIn(Member member, int SleepRoomNumSelect) {
		JSONObject returnResult = new JSONObject();
		Member user = finduser(member);

		if (!user.isUsedSleepRoom()) {
			if (SleepRooms[SleepRoomNumSelect / 10][SleepRoomNumSelect % 10] != null) {// 선택한 방이 이미 사용중일때
				returnResult.put("result", false);
				returnResult.put("message", "사용중인 방입니다");
			} else {
				returnResult.put("result", true);
				SleepRooms[SleepRoomNumSelect / 10][SleepRoomNumSelect % 10] = user.getId();
				user.setSleepRoom(SleepRoomNumSelect);

			}
		} else { // 유저가 수면실을 이미 이용중일때
			returnResult.put("result", false);
			returnResult.put("message", "수면실을 이미 사용중입니다.");
		}

		return returnResult.toString();
	}

	// 수면실 퇴실 메소드
	public String SleepRoomOut(Member member) {
		JSONObject returnResult = new JSONObject();
		Member user = finduser(member);

		if (user.isUsedSleepRoom()) {
			SleepRooms[user.getSleepRoom() / 10][user.getSleepRoom() % 10] = null;
			user.setSleepRoom(-1);
			returnResult.put("result", true);
		} else {
			returnResult.put("result", false);
			returnResult.put("message", "수면실은 사용중이지 않습니다.");
		}

		return returnResult.toString();
	}

	// 매점 메소드
	public String SnackBar(Member member, int SnackMenu, int SnackCount) {
		JSONObject returnResult = new JSONObject();
		Member user = finduser(member);

		int[] SnackPrices = { 0, 2000, 3000, 1000, 2000 };

		int total = SnackPrices[SnackMenu] * SnackCount;
		user.setPrice(user.getPrice() + total);

		returnResult.put("price", total);

		return returnResult.toString();
	}

	// 유저의 현재 사용금액을 보내주는 메소드
	public String getPrice(Member member) {
		Member user = finduser(member);
		JSONObject returnResult = new JSONObject();
		returnResult.put("price", user.getPrice());

		return returnResult.toString();
	}

	// 서버 커맨드를 위한 메소드들
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
		// 1초마다 실행되도록
		while (true) {

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
