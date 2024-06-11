package common.libTest.commons.lang;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
			return HashCodeBuilder.reflectionHashCode(this);
		}
		
		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}
		
		@Override
		public String toString() {
			// ToStringStyle.MULTI_LINE_STYLE
			return ToStringBuilder.reflectionToString(
					this, ToStringStyle.SHORT_PREFIX_STYLE
				);
		}
	}

	public static void main(String[] args) {
		Person p = new Person();
		p.setName("gildong");
		p.setAge(30);
		
		System.out.println(p);
		// UsageToString.Person[name=gildong,age=30]
		
		System.out.println(p.hashCode());
		// 1202117709
		
		Person p1 = new Person("John", 29);
		Person p2 = new Person("John", 29);
		
		System.out.println(p1.equals(p2));
	}
	
}
