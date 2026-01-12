package common.util.xml;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

 /**
 * <pre>
 * 2024.11.07 이후 업데이트 없어서 군장 안함
 * - jackson-dataformat-xml 권장
 * </pre>
 *
 * @author 김대광
 */
public class XstreamXmlUtil {

	private XstreamXmlUtil() {
		super();
	}

	private static class ExceptionMessage {

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

		public static String isNullOrEmpty(String paramName) {
	        return String.format("'%s' is null or empty", paramName);
	    }

	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> converterXmlStrToMap(String xml, String root) {
		if ( StringUtils.isBlank(xml) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("xml"));
		}

		if ( StringUtils.isBlank(root) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("root"));
		}

		Map<String, Object> map = null;

		XStream xStream = new XStream();
		Mapper mapper = xStream.getMapper();

		xStream.alias(root, Map.class);
		xStream.registerConverter(new MapEntryConverter(mapper));

		map = (Map<String, Object>) xStream.fromXML(xml);
		return map;
	}

	public static String convertMapToXmlStr(Map<String, Object> map, String root) {
		if ( map == null || map.isEmpty() ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("map"));
		}

		if ( StringUtils.isBlank(root) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNullOrEmpty("root"));
		}

		XStream xStream = new XStream();
		Mapper mapper = xStream.getMapper();

		xStream.alias(root, Map.class);
		xStream.registerConverter(new MapEntryConverter(mapper));

		String xml = "";
		xml = xStream.toXML(map);
		return xml;
	}

	private static class MapEntryConverter extends AbstractCollectionConverter implements Converter {

		public MapEntryConverter(Mapper mapper) {
			super(mapper);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean canConvert(Class type) {
			Objects.requireNonNull(type, ExceptionMessage.isNull("type"));

			return type.equals(HashMap.class);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void marshal(Object value, HierarchicalStreamWriter writer , MarshallingContext context) {
			Objects.requireNonNull(value, ExceptionMessage.isNull("value"));
			Objects.requireNonNull(writer, ExceptionMessage.isNull("writer"));
			Objects.requireNonNull(context, ExceptionMessage.isNull("context"));

			AbstractMap map = (AbstractMap) value;
			Entry entry = null;
			Object entryKey = null;
			Object entryVal = null;

			for (Object obj : map.entrySet()) {
				entry = (Entry) obj;
				entryKey = entry.getKey();
				entryVal = entry.getValue();

				if (entryVal instanceof Map) {
					writer.startNode(entryKey.toString());
	                marshal(entryVal, writer, context);
	                writer.endNode();
	                continue;
				}

				writer.startNode(entryKey.toString());
				writer.setValue(entryVal.toString());
				writer.endNode();
			}
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader , UnmarshallingContext context) {
			Objects.requireNonNull(reader, ExceptionMessage.isNull("reader"));
			Objects.requireNonNull(context, ExceptionMessage.isNull("context"));

			Map<String, Object> map = new HashMap<>();

			while(reader.hasMoreChildren()) {
				reader.moveDown();

				 if (reader.hasMoreChildren()) {
					 Map<String, Object> childMap = new HashMap<>();
					 map.put(reader.getNodeName(), childMap);
	                 unmarshalHierarchical(reader, context, childMap);
	                 reader.moveUp();
	                 continue;
				 }

				 map.put(reader.getNodeName(), reader.getValue());
	             reader.moveUp();
			}

			return map;
		}

		private void unmarshalHierarchical(HierarchicalStreamReader reader, UnmarshallingContext context
				, Map<String, Object> map) {

			while(reader.hasMoreChildren()) {
				reader.moveDown();

				if (reader.hasMoreChildren()) {
					Map<String, Object> childMap = new HashMap<>();
					map.put(reader.getNodeName(), childMap);
					unmarshalHierarchical(reader, context, childMap);
					reader.moveUp();
					continue;
	            }

				map.put(reader.getNodeName(), reader.getValue());
	            reader.moveUp();
			}
		}
	}

}
