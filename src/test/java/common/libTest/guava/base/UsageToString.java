package common.libTest.guava.base;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import common.libTest.PersonOrg;

public class UsageToString {
	
	static class Person extends PersonOrg {

		private Person() {
			super();
		}
		
		private Person(String name, int age) {
			super(name, age);
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(this.getName(), this.getAge());
		}
		
		@Override
		public boolean equals(Object obj) {
			Person other = (Person) obj;
			return Objects.equal(this.getName(), other.getName())
					&&	Objects.equal(this.getAge(), other.getAge());
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("name", this.getName())
					.add("age", this.getAge())
					.toString();
		}
	}

	public static void main(String[] args) {
		Person p = new Person();
		p.setName("gildong");
		p.setAge(30);
		
		System.out.println(p);
		// Person{name=gildong, age=30}
		
		System.out.println(p.hashCode());
		// 1007161169
		
		Person p1 = new Person("John", 29);
		Person p2 = new Person("John", 29);
		
		System.out.println(p1.equals(p2));
	}
}
