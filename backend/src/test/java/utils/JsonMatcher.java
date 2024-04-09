package utils;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Map;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;

/**
 * E2ETest의 ResultMatcher, JsonPath를 편하게 사용하기 위해 추상화한 Util 클래스입니다.
 */
public class JsonMatcher {

	private final static String JSON_ROOT = "$";
	private final StringBuilder sb = new StringBuilder();

	private JsonMatcher(String defaultPath) {
		this.sb.append(defaultPath);
	}

	public static JsonMatcher create() {
		return new JsonMatcher(JSON_ROOT);
	}

	/**
	 * 복사된 JsonMatcher를 반환합니다.
	 * <p>
	 * 특정 지점까지의 JsonMatcher를 재사용할 때 사용합니다.
	 *
	 * @return 복사된 JsonMatcher
	 */
	public JsonMatcher clone() {
		return new JsonMatcher(sb.toString());
	}

	/**
	 * property를 추가합니다.
	 *
	 * @param property 추가할 property의 이름
	 * @return sb에 해당하는 Property가 추가된 JsonMatcher
	 */
	public JsonMatcher get(String property) {
		sb.append(".").append(property);
		return this;
	}

	public JsonMatcher at(int index) {
		sb.append("[").append(index).append("]");
		return this;
	}

	/**
	 * property에 대한 JsonPathResultMatchers를 반환합니다. 사용예시 : exists()
	 *
	 * @return JsonPathResultMatchers
	 */
	public JsonPathResultMatchers is() {
		JsonPathResultMatchers jsonPathResultMatchers = jsonPath(sb.toString());
		clear();
		return jsonPathResultMatchers;
	}

	/**
	 * 지금까지 추가된 property를 기준으로 expected와 일치하는지에 대한 ResultMatcher를 반환합니다.
	 *
	 * @param expected 예상되는 값
	 * @return ResultMatcher
	 */
	public ResultMatcher isEquals(Object expected) {
		String result = sb.toString();
		clear();
		return jsonPath(result).value(expected);
	}

	/**
	 * 지금까지 추가된 property를 기준으로 Map의 key를 get(), value와 일치하는지에 대한 ResultMatcher를 반환합니다.
	 *
	 * @param propertyExpectedMap 예상되는 값(key: property, value: expected)
	 * @return ResultMatcher[]
	 */
	public ResultMatcher[] isEquals(Map<String, Object> propertyExpectedMap) {
		ResultMatcher[] matchers = propertyExpectedMap.entrySet().stream().map(entry ->
						this.clone().get(entry.getKey()).isEquals(entry.getValue()))
				.toArray(ResultMatcher[]::new);
		clear();
		return matchers;
	}

	/**
	 * 재활용을 위해 '$'를 제외하고 모든 문자열을 제거합니다.
	 */
	private void clear() {
		sb.delete(1, sb.length());
	}

}
