import java.text.SimpleDateFormat;
import java.util.Date;

public class Member {
	private String id;
	private String passwd;
	private String name;
	private String birthdate; //생년월일
	private boolean loggedin; // 로그인 여부
	private String occupied_date; // 입실 시간
	private int price; // 사용 요금
	private int cabinet; // 캐비넷 번호
	private int SleepRoom; // 사용중인 수면실 번호(미사용시 -1)

	public static final int GRADE_DEFAULT = 0;
	public static final int GRADE_SPECIAL = 1;
	public static final int GRADE_ADMIN = 2;

	public Member(String id, String passwd) {
		this.id = id;
		this.passwd = passwd;
	}

	public Member(String id, String passwd, String name, String birthdate) {
		this.id = id;
		this.passwd = passwd;
		this.name = name;
		this.birthdate = birthdate;

		loggedin = false;
		occupied_date = " ";
		price = 0;
		cabinet = -1;
		SleepRoom = -1;
	}

	public String getId() {
		return id;
	}

	public String getPasswd() {
		return passwd;
	}

	public String getName() {
		return name;
	}

	public void setOccupiedDate(String occupied_date) {
		this.occupied_date = occupied_date;
	}

	public String getOccupiedDate() {
		return occupied_date;
	}

	public void setPrice(int balance) {
		this.price = balance;
	}

	public int getPrice() {
		return price;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public boolean isLoggedin() {
		return loggedin;
	}

	public void setLoggedin(boolean loggedin) {
		this.loggedin = loggedin;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public int getCabinet() {
		return cabinet;
	}

	public void setCabinet(int cabinet) {
		this.cabinet = cabinet;
	}

	public boolean isUsedSleepRoom() {
		if (SleepRoom == -1) {
			return false;
		} else {
			return true;
		}
	}

	public void setSleepRoom(int SleepRoom) {
		this.SleepRoom = SleepRoom;
	}

	public int getSleepRoom() {
		return SleepRoom;
	}

	@Override
	public String toString() {
		return String.format("%s/%s/%s/%s", id, passwd, name, birthdate);
	}
}
