package jpathwrapper;

import java.util.List;

import com.jayway.jsonpath.JsonPath;

public class Jpath {
	public static <T> T jp(String json, String jp) {
		return JsonPath.read(json, jp);
	}
	

	public static List<String> jps(String json, String jp) {
		return JsonPath.read(json, jp);
	}
}
