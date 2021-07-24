import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileModule {

	private static final String DATABASE_FILE = "database.txt";

	public boolean saveMemberInfo(ArrayList<Member> info) {
		PrintWriter out = null;

		try {
			out = new PrintWriter(new FileWriter(new File(DATABASE_FILE)));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		for (Member member : info) {
			out.println(member.toString());
			out.flush();
		}
		out.close();

		return true;
	}

	public ArrayList<Member> loadMemberInfo() {
		ArrayList<Member> memberList = new ArrayList<Member>();

		BufferedReader in = null;

		try {
			in = new BufferedReader(new FileReader(new File(DATABASE_FILE)));
			String line = null;

			while ((line = in.readLine()) != null) {
				String[] datas = line.split("/");
				if (datas.length != 4) {
					memberList.clear();
					break;
				}

				// 아이디/비밀번호/이름/생년월일 포맷
				String id = datas[0];
				String passwd = datas[1];
				String name = datas[2];
				String birthdata = datas[3];

				Member member = new Member(id, passwd, name, birthdata);
				memberList.add(member);
			}
		} catch (Exception e) {
			System.out.println("현재 데이터베이스가 비어있습니다. 저장된 회원정보가 없습니다.");
			memberList.clear();
		}

		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
			}
		}

		return memberList;
	}
}
