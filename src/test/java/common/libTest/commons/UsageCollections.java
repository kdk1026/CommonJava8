package common.libTest.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import common.libTest.PersonOrg;

public class UsageCollections {
	
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

		List<Person> listPerson = new ArrayList<Person>();
		Collections.addAll(listPerson, p1, p2, p3);

		@SuppressWarnings("unchecked")
		Collection<String> collectionAge = CollectionUtils.collect(listPerson, new Transformer() {
			@Override
			public Object transform(Object obj) {
				return ((Person) obj).getAge();
			}
		});

		System.out.println(  collectionAge );
	}
	
}
