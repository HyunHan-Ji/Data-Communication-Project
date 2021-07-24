import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String getCurrentDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
		return df.format(new Date());
	}
}
