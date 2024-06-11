package common.libTest.commons;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import common.libTest.PersonOrg;

public class UsageBeanUtils {
	
	static class Person extends PersonOrg {
		
	}
	
	public static void populate() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "John");
		map.put("age", 29);

		Person person = new Person();
		try {
			BeanUtils.populate(person, map);

			// 파라미터를 Bean 객체로 받기
			// BeanUtils.populate(person, request.getParameterMap());
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(person);
	}
	
	public static void describe() {
		Person person = new Person();
		person.setName("John");
		person.setAge(29);

		Map<String, String> map = new HashMap<String, String>();
		try {
			map = BeanUtils.describe(person);
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.remove("class");

		System.out.println(map);
	}
	
}