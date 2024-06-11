package common.libTest;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TestJson {

	@Test
	public void gsonTest() {
		JsonObject obj = new JsonObject();
		obj.addProperty("aa", 123);
		obj.addProperty("bb", 456);

		System.out.println( "GsonObj :: " + obj.toString() );

		JsonArray array = new JsonArray();
		array.add(obj);

		obj = new JsonObject();
		obj.addProperty("cc", 789);
		array.add(obj);

		System.out.println( "GsonArray :: " + array.toString() );
	}

	@Test
	public void jacksonTest() {
		// Map 변환은 JacksonUtil 참고
		ObjectMapper mapper = new ObjectMapper();

		ObjectNode obj = mapper.createObjectNode();
		obj.put("aa", 123);
		obj.put("bb", 456);

		System.out.println( "JacksonObj :: " + obj.toString() );

		ArrayNode array = mapper.createArrayNode();
		array.add(obj);

		obj = mapper.createObjectNode();
		obj.put("cc", 789);
		array.add(obj);

		System.out.println( "JacksonArray :: " + array.toString() );

		//
	}

}
