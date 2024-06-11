package common.libTest.guava;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import common.libTest.PersonOrg;

public class UsageCollect {
	
	static class Person extends PersonOrg {
		private Person() {
			super();
		}

		private Person(String name, int age) {
			super(name, age);
		}
	}

	public static void transform_test() {
		Person p1 = new Person("John", 29);
		Person p2 = new Person("James", 28);
		Person p3 = new Person("Sam", 30);

		List<Person> listPerson = Lists.newArrayList(p1, p2, p3);
		
		// TODO: 20.0 이후 버전 Function
		Function<Object, Object> function = new Function<Object, Object>() {
			@Override
			public Object apply(Object input) {
				return ((Person) input).getAge();
			}
		};
		Collection<Object> collectionAge = Collections2.transform(listPerson, function);

		System.out.println(  collectionAge );
	}
	
	public static void listToMap_test() {
		Person p1 = new Person("John", 29);
		Person p2 = new Person("James", 28);
		Person p3 = new Person("Sam", 30);

		List<Person> listPerson = Lists.newArrayList(p1, p2, p3);

		Map<Object, Person> ageMap = Maps.uniqueIndex(listPerson,
			new Function<Object, Object>() {
				public Object apply(Object obj) {
					return ((Person) obj).getName();
				}
			});
		System.out.println(  ageMap );
	}
}
