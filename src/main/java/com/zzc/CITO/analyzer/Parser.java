package com.zzc.CITO.analyzer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zzc.CITO.Base.TClass;
import com.zzc.CITO.Base.TEdge;
import com.zzc.CITO.Base.rType;
import com.zzc.CITO.util.ExcelUtil;
import com.zzc.CITO.util.XMLReader;

import soot.SootClass;

public class Parser {

//	private static final String path="C:\\Users\\hp\\Desktop\\input\\";
	private static final String path="D:\\cito_important\\cito_modified\\input\\";
    //private static final String path="F:\\CITO\\CITOProject\\input_analysis\\";
	private static Map<String,TClass> MapOfVertex;		//类节点集合
	private static Map<Integer,TClass> MapOfVertexById;
	private static Set<TClass> SetOfClasses;	
	private static Set<TEdge> SetOfRet;	//边集合			
	private static Map<String,String> MapOfInherited;		//继承列表
	private Map<String,SootClass> MapOfInterfaces;		//实现列表
	private static double[] NOF;		//记录特征数量
	
	public Parser() {
		this.MapOfVertex=new HashMap<String,TClass>();
		this.MapOfVertexById=new HashMap<Integer,TClass>();
		this.SetOfRet=new HashSet<TEdge>();
		this.SetOfClasses=new HashSet<TClass>();
		this.MapOfInherited = new HashMap<String, String>();
		this.MapOfInterfaces=new HashMap<String,SootClass>();
	}
	
	
	public static Map<String, TClass> getMapOfVertex() {
		return MapOfVertex;
	}


	public static Map<Integer, TClass> getMapOfVertexById() {
		return MapOfVertexById;
	}


	public static Set<TClass> getSetOfClasses() {
		return SetOfClasses;
	}


	public static Set<TEdge> getSetOfRet() {
		return SetOfRet;
	}


	public static double[] getNOF() {
		return NOF;
	}


	public static void parse(String fileName) throws Exception {
		String methodDeps,attrDeps;
		ExcelUtil poi = new ExcelUtil();
		SetOfClasses=new HashSet<TClass>();
		MapOfVertex=new HashMap<String,TClass>();
		MapOfVertexById=new HashMap<Integer,TClass>();
		MapOfInherited = new HashMap<String, String>();
        List<List<String>> list = poi.read(path+fileName+".xlsx");
        NOF=new double[list.size()-1];
        list.remove(0);
        if (list != null) {
        	for (int i = 0; i < list.size(); i++) {
            	TClass c=new TClass(i,list.get(i).get(0));
            	SetOfClasses.add(c);
            	MapOfVertex.put(c.getcName(), c);
            	MapOfVertexById.put(c.getcId(), c);
        	}
            for (int i = 0; i < list.size(); i++) {
                List<String> cellList = list.get(i);
                if(cellList.get(0)==null)	break;
                for (int j = 1; j < cellList.size(); j++) {
                	if(j==1) {
                		attrDeps=cellList.get(j);
                    	if(attrDeps.length()>10)
                    	{
                    		JSONObject json=JSONArray.parseObject(attrDeps.substring(12, attrDeps.length()-1));
                    		for(TClass cd:SetOfClasses) {
                    			if(json.containsKey(cd.getcName())) {
                    				MapOfVertex.get(cellList.get(0)).getAttrDeps().put(cd.getcName(), json.getInteger(cd.getcName()));
                    				MapOfVertexById.get(i).getAttrDeps().put(cd.getcName(), json.getInteger(cd.getcName()));
                    				NOF[i]+=json.getInteger(cd.getcName());
                    			}
                    		}
                    	}
                    }
                    else {
                    	methodDeps=cellList.get(j);
                    	if(methodDeps.length()>10)
                    	{
                    		JSONObject json=JSONArray.parseObject(methodDeps.substring(14, methodDeps.length()-1));
                    		for(TClass cd:SetOfClasses) {
                    			if(json.containsKey(cd.getcName())) {
                    				MapOfVertex.get(cellList.get(0)).getMethodDeps().put(cd.getcName(), json.getInteger(cd.getcName()));
                    				MapOfVertexById.get(i).getMethodDeps().put(cd.getcName(), json.getInteger(cd.getcName()));
                    				NOF[i]+=json.getInteger(cd.getcName());
                    			}
                    		}
                    	}
                    }
                }
                //System.out.println(MapOfVertexById.get(i)+":"+NOF[i]);
            }
        }
        XMLReader reader=new XMLReader(MapOfVertexById);
        SetOfRet=reader.readXML(path+fileName+".txt");
        //System.out.println(SetOfRet);
        for(TEdge edge:SetOfRet) {
        	TClass from=edge.getFromClass();
        	TClass to=edge.getToClass();
        	from.addRelation(edge);
        	to.addRelation(edge);
        	if(edge.getRtype()==rType.II) {
        		MapOfInherited.put(from.getcName(), to.getcName());
        	}
        }
        System.out.println(SetOfRet.size());
		//setDynamciRet();	//添加动态依赖关系
		System.out.println(SetOfRet.size());
	}
	//设置动态依赖关系
	private static void setDynamciRet() throws Exception {
		String childClassName,parentClassName;
		TClass childClass,parentClass;
		int value = 0;
		for(Map.Entry<String, String> entry:MapOfInherited.entrySet()) {
			childClassName=entry.getKey();
			parentClassName=entry.getValue();
			parentClass=MapOfVertex.get(parentClassName);
			childClass=MapOfVertex.get(childClassName);
			for(TEdge e:parentClass.getInRelation()) {
				if(e.getRtype()==rType.IAG||e.getRtype()==rType.IAS) {
					addRet(e.getFromClass().getcName(),childClassName,rType.Dy);
					//removeRet(e.getFromClass().getcName(),childClassName,rType.IAS);
					if(e.getFromClass().getAttrDeps().containsKey(parentClassName))
						value=e.getFromClass().getAttrDeps().get(parentClassName);
					e.getFromClass().getAttrDeps().put(childClassName, value);
					if(e.getFromClass().getMethodDeps().containsKey(parentClassName))
						value=e.getFromClass().getMethodDeps().get(parentClassName);
					if(value==0)
						continue;
					e.getFromClass().getMethodDeps().put(childClassName, value);
					System.out.println("childClassName"+childClassName+"value"+value);
				}	
			}
		}
	}
	//添加关系
	private static void addRet(String sourceName,String targetName,int rt) throws Exception {
		TClass sourceClass=MapOfVertex.get(sourceName);
		TClass targetClass=MapOfVertex.get(targetName);
		TEdge edge=new TEdge(sourceClass,targetClass,rt);
		SetOfRet.add(edge);
		sourceClass.addRelation(edge);
		targetClass.addRelation(edge);
	}
}
