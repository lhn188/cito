package com.zzc.CITO.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.zzc.CITO.Base.TClass;
import com.zzc.CITO.Base.TEdge;
import com.zzc.CITO.Base.rType;


public class XMLReader {
	private static Map<Integer,TClass> MapOfVertexById;
	
	public XMLReader(Map<Integer,TClass> MapOfVertexById) {
		this.MapOfVertexById=MapOfVertexById;
	}

	public static Set<TEdge> readXML(String fileName) {
		try
		{
			Set<TEdge> set=new HashSet<>();
			//读取xml文件
			SAXReader reader = new SAXReader();
			Document doc = reader.read(fileName);

			List<Element> listRelation = doc.selectNodes("/org/relations/relation");
			
			for (Element element : listRelation)
			{
				Node node = element.selectSingleNode("from");
				String cfrom = node.getStringValue();
				
				node = element.selectSingleNode("to");
				String cto = node.getStringValue();
				
				node = element.selectSingleNode("type");
				String rtype = node.getStringValue();

				//关系解析
				int rt;
				rtype = rtype.toLowerCase();
				rtype.trim();
				if(rtype.equals("inherit")) {
					rt = rType.II;
				}
				else if(rtype.equals("aggregation")){
					rt = rType.IAG;
				}
				else if(rtype.equals("association")) {
					rt = rType.IAS;
				}
				else {
					rt=0;
					System.out.println("from"+cfrom+"to"+cto);
				}
				TEdge edge=new TEdge(MapOfVertexById.get(Integer.valueOf(cfrom)-1),MapOfVertexById.get(Integer.valueOf(cto)-1),rt);
				set.add(edge);
			}
			return set;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
