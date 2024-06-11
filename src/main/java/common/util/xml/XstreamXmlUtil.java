package common.util.xml;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class XstreamXmlUtil {
	
	private XstreamXmlUtil() {
		super();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> converterXmlStrToMap(String xml, String root) {
		Map<String, Object> map = null;
		
		XStream xStream = new XStream();
		Mapper mapper = xStream.getMapper();
		
		xStream.alias(root, Map.class);
		xStream.registerConverter(new MapEntryConverter(mapper));
		
		map = (Map<String, Object>) xStream.fromXML(xml);
		return map;
	}
	
	public static String convertMapToXmlStr(Map<String, Object> map, String root) {
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
			return type.equals(HashMap.class);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void marshal(Object value, HierarchicalStreamWriter writer , MarshallingContext context) {
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
