package tz.basis.query.core.mapper;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

/**
 *
 * 조회결과 확인을 위한 유틸
 *
 * @author TZ
 *
 */
public class PropertyUtils {

	public static String beanToString(Object bean) {
		if(bean == null){
			return "";
		}

		StringBuilder builder = new StringBuilder("QueryResult [");

		Field[] fields = bean.getClass().getDeclaredFields();

		for (Field field : fields) {
			ReflectionUtils.makeAccessible(field);
			builder.append(String.format(" %s=%s", field.getName(), String.valueOf(ReflectionUtils.getField(field, bean))));
		}

		builder.append(" ]");

		return builder.toString();
	}

	public static String mapToString(Map<String, Object> map) {
		if(map == null || map.isEmpty()){
			return "";
		}

		StringBuilder builder = new StringBuilder("QueryResult [");

		for(Map.Entry<String, Object> entry : map.entrySet())	{
			builder.append(String.format(" %s=%s", entry.getKey(), String.valueOf(entry.getValue())));
		}

		builder.append(" ]");

		return builder.toString();
	}

	public static String primitiveToString(Object value){
		StringBuilder builder = new StringBuilder("QueryResult [");
		builder.append(String.format(" value=%s", String.valueOf(value)));
		builder.append(" ]");

		return builder.toString();
	}
}
