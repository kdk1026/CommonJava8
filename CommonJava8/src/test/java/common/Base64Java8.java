package common;

import java.util.Base64;

import org.junit.Test;

public class Base64Java8 {

	@Test
	public void test() {
		String plain = "Hello";
		String encoding = Base64.getEncoder().encodeToString(plain.getBytes());
		System.out.println(encoding);
		
		String decoding = new String(Base64.getDecoder().decode(encoding));
		System.out.println(decoding);
	}
	
}
