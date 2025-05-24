package com.zzc.CITO.analyzer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.zzc.CITO.Base.TClass;
import com.zzc.CITO.Base.TEdge;
import com.zzc.CITO.Base.rType;

import soot.Body;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.JDynamicInvokeExpr;
import soot.jimple.internal.JInterfaceInvokeExpr;
import soot.jimple.internal.JSpecialInvokeExpr;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;

public class Analyzer {
	private static Collection<SootClass> listOfClasses;	//SootClass列表
	private Map<String,TClass> MapOfVertex;		//类节点集合
	private Map<Integer,TClass> MapOfVertexById;
	private Set<TClass> SetOfClasses;
	private Set<TEdge> SetOfRet;	//边集合
	private Map<String,String> MapOfInherited;		//继承列表
	private Map<String,SootClass> MapOfInterfaces;		//实现列表
	private double[] NOF;		//记录特征数量
	
	public Analyzer() {
		this.MapOfVertex=new HashMap<String,TClass>();
		this.MapOfVertexById=new HashMap<Integer,TClass>();
		this.SetOfRet=new HashSet<TEdge>();
		this.SetOfClasses=new HashSet<TClass>();
		this.MapOfInherited = new HashMap<String, String>();
		this.MapOfInterfaces=new HashMap<String,SootClass>();
	}
	
	public static Collection<SootClass> getListOfClasses() {
		return listOfClasses;
	}

	public Map<String, TClass> getMapOfVertex() {
		return MapOfVertex;
	}

	public Set<TEdge> getSetOfRet() {
		return SetOfRet;
	}

	public Set<TClass> getSetOfClasses() {
		return SetOfClasses;
	}

	public Map<String, String> getMapOfInherited() {
		return MapOfInherited;
	}


	public Map<String, SootClass> getMapOfInterfaces() {
		return MapOfInterfaces;
	}


	public Map<Integer, TClass> getMapOfVertexById() {
		return MapOfVertexById;
	}
	//分析关系
	public void analysis(String fileName) throws Exception {
		listOfClasses =loadClassAndAnalysis(fileName);
		removeSystemClasses();
		NOF=new double [listOfClasses.size()];
		setClassNo();	//初始化类编号
		setInterfaces();	//添加实现关系
		setInherited();		//添加继承关系
		for (SootClass sClass : listOfClasses) {
			TClass c=MapOfVertex.get(sClass.getName());

			//成员变量和方法的总和NOF(特征总数)  涉及SootClass
			NOF[c.getcId()]=sClass.getFieldCount()+sClass.getMethodCount();		//设置特征数量
			analysisCouple(sClass);		//分析耦合
		}
		updateAg();		//添加聚合关系
		//setDynamciRet();	//添加动态依赖关系
		printInfo();
	}
	private void removeSystemClasses() {
		List<SootClass> removelist = new ArrayList<SootClass>();
		for (SootClass sootClass : listOfClasses) {
			if (ignore(sootClass)) {
				removelist.add(sootClass);
			}
		}
		for (SootClass removeClass : removelist) {
			if (listOfClasses.contains(removeClass)) {
				listOfClasses.remove(removeClass);
			}
		}
	}

	//加载程序
	private static Collection<SootClass> loadClassAndAnalysis(String fileName) throws Exception {
		System.out.println("你选择的系统是" + fileName);
		return SootOption.setOptions(fileName);
	}
	
	// 设置SootClass编号
	private void setClassNo(){
		int i = 0;//在类表中，为每一个添加序号，然后放入类节点集合中
		//MapOfVertexById按<Integer,TClass>格式添加，MapOfVertex按 <String,TClass>添加
		for(SootClass sc : listOfClasses){
			String name=sc.getName();
			TClass c=new TClass(i,name);
			MapOfVertexById.put(i,c);
			i++;
			MapOfVertex.put(name,c);
			SetOfClasses.add(c);
		}		
	}
	
	public double[] getNOF() {
		return NOF;
	}

	//初始化接口列表 若为接口，则加入Interface的列表，
	private void setInterfaces() throws Exception{
		for(SootClass sc:listOfClasses) {
			if(sc.isInterface())
				MapOfInterfaces.put(sc.getName(),sc);
		}
		//把si是接口 sc是类，如果sc继承si，则把sc和si合并为继承关系
		for(Map.Entry<String, SootClass> entry:MapOfInterfaces.entrySet()) {
			String si=entry.getKey();
			SootClass interfaceSoot=entry.getValue();
			for(SootClass sc:listOfClasses) {
				if(sc.implementsInterface(si)) {
					TClass parentClass=MapOfVertex.get(si);
					parentClass.setAbstract(true);
					addRet(sc.getName(),si,rType.II);		//合并为继承关系
					MapOfInherited.put(sc.getName(), si);
					addattrDeps(sc.getName(),parentClass.getcName(),interfaceSoot.getFieldCount());	//属性依赖
					addmethodDeps(sc.getName(),parentClass.getcName(),interfaceSoot.getMethodCount());//方法依赖
				}
			}
		}
	}
	// 初始化继承列表
	private void setInherited() throws Exception {
		for(SootClass sc:listOfClasses) {
			if(sc.hasSuperclass()) {
				SootClass parentSoot=sc.getSuperclass();
				if(MapOfVertex.containsKey(parentSoot.getName())) {
					TClass parentClass=MapOfVertex.get(parentSoot.getName());
					if(parentSoot.isAbstract())
						parentClass.setAbstract(true);
					addRet(sc.getName(),parentClass.getcName(),rType.II);
					MapOfInherited.put(sc.getName(), parentSoot.getName());
					addattrDeps(sc.getName(),parentSoot.getName(),parentSoot.getFieldCount());
					addmethodDeps(sc.getName(),parentClass.getcName(),parentSoot.getMethodCount());
				}
			}
		}
	}
	//判断是否继承
	private boolean isParent(String sourceName,String targetName) {
		if(!MapOfInherited.containsKey(sourceName))	return false;
		if(MapOfInherited.get(sourceName).equals(targetName))	return true;
		return false;
	}
	//分析耦合，包含属性和方法
	private void analysisCouple(SootClass sClass) throws Exception {
		analysisField(sClass);
		analysisMethod(sClass);
	}
	//添加关系
	private void addRet(String sourceName,String targetName,int rt) throws Exception {
		TClass sourceClass=MapOfVertex.get(sourceName);
		TClass targetClass=MapOfVertex.get(targetName);
		TEdge edge=new TEdge(sourceClass,targetClass,rt);
		SetOfRet.add(edge);
		sourceClass.addRelation(edge);
		targetClass.addRelation(edge);
	}
	//删除关系
	private void removeRet(String sourceName,String targetName,int rt) throws Exception {
		TClass sourceClass=MapOfVertex.get(sourceName);
		TClass targetClass=MapOfVertex.get(targetName);
		TEdge edge=new TEdge(sourceClass,targetClass,rt);
		SetOfRet.remove(edge);
		sourceClass.removeRelation(edge);
		targetClass.removeRelation(edge);
	}
	//分析属性耦合
	private void analysisField(SootClass sClass) throws Exception {
		for (SootField sfield : sClass.getFields()) {
			if (sfield != null && sfield.getType() instanceof RefType) {
				RefType type = (RefType) sfield.getType();
				SootClass targetSoot = type.getSootClass();
				String sourceName = sClass.getName();
				String targetName = targetSoot.getName();
				if (MapOfVertex.containsKey(targetName)
						&&!sourceName.equals(targetName)
						&&!isParent(sourceName,targetName)){
					addattrDeps(sourceName,targetName,1);
					addRet(sourceName,targetName,rType.IAS);
				}
			}
		}
	}
	//分析方法耦合，包含返回值、参数、方法调用
	private void analysisMethod(SootClass sClass) throws Exception {
		List<SootMethod> listOfMethod = new CopyOnWriteArrayList<>(sClass.getMethods());
		for (SootMethod m : listOfMethod) {
			analysisRet(sClass,m);	
			analysisParainMethod(sClass, m);
			if(m.isConcrete()) {
				analysismethod(sClass,m);
			}
		}
	}
	//分析返回值类型
	private void analysisRet(SootClass sClass,SootMethod sMethod) throws Exception {
		Type retType = sMethod.getReturnType();
		String retClassName = retType.toString();
		String className = sClass.getName();
		if (MapOfVertex.containsKey(retClassName)
				&&!retClassName.equals(className)&&!isParent(className,retClassName)) {
			addRet(className,retClassName,rType.IAS);
			addattrDeps(className,retClassName,1);
		}
	}
	//分析参数类型
	private void analysisParainMethod(SootClass sClass, SootMethod sMethod) throws Exception {
		List<?> types = sMethod.getParameterTypes();
		for (int i = 0; i < types.size(); i++) {
			Object paratype = types.get(i);
			String paraClassName = paratype.toString();
			String sourceName = sClass.getName();
			if (!paraClassName.equals(sourceName)
				&& MapOfVertex.containsKey(paraClassName)
				&&!isParent(sourceName,paraClassName)) {
					addRet(sourceName,paraClassName,rType.IAS);
					addattrDeps(sourceName, paraClassName,1);
				}
			}
	}
	//添加属性依赖关系
	private void addattrDeps(String sourceName, String targetName, int n) {
		TClass tClass=MapOfVertex.get(sourceName);
		Map<String,Integer> tmp=tClass.getAttrDeps();
		if(!tmp.isEmpty()&&tmp.containsKey(targetName)) {
			tmp.put(targetName, tmp.get(targetName)+n);
		}
		else {
			tmp.put(targetName, 1);
		}
		//System.out.println(tClass.getcName()+"-"+targetName+"="+tmp.get(targetName));
		tClass.setAttrDeps(tmp);
	}
	// 分析方法调用耦合
	private void analysismethod(SootClass sClass, SootMethod sMethod) throws Exception {
		Body body = sMethod.retrieveActiveBody();
		Collection<Unit> units = body.getUnits();
		for (Unit unit : units) {
			// Log.logInfo(unit.toString());
			int lineNo;
			for (ValueBox valueBox : unit.getUseBoxes()) {
				Value useValue = valueBox.getValue();
				switch (isInvokeExpr(useValue)) {
				case 1:
					lineNo = getLineNo(unit);
					analysisVirtualInvoke(useValue, sClass, sMethod, lineNo);
					break;
				case 2:
					lineNo = getLineNo(unit);
					analysisInterfaceInvoke(useValue, sClass, sMethod, lineNo);
					break;
				case 3:
					lineNo = getLineNo(unit);
					analysisDynamicInvoke(useValue, sClass, sMethod, lineNo);
					break;
				case 4:
					lineNo = getLineNo(unit);
					analysisSpecialInvoke(useValue, sClass, sMethod, lineNo);
					break;
				case 5:
					lineNo = getLineNo(unit);
					analysisStaticInvoke(useValue, sClass, sMethod, lineNo);
					break;
				default:
					break;
				}
			}
		}

	}

	// 获得行号
	private int getLineNo(Unit unit) {
		int num = 0;
		List<Tag> tagList = unit.getTags();
		for (Tag tag : tagList) {
			if (tag instanceof LineNumberTag) {
				num = ((LineNumberTag) tag).getLineNumber();
				break;
			}
		}
		return num;
	}

	// 静态调用StaticInvoke
	private void analysisStaticInvoke(Value useValue, SootClass sClass,
			SootMethod sMethod, int lineNo) throws Exception {

		JStaticInvokeExpr jStaticInvokeExpr = (JStaticInvokeExpr) useValue;
		String sourceCName = sClass.getName();
		SootClass targetClass = jStaticInvokeExpr.getMethod()
				.getDeclaringClass();
		String targetCName = targetClass.getName();

		if (MapOfVertex.containsKey(targetCName) && !isParent(sourceCName, targetCName)) {
			if (!sourceCName.equals(targetCName)) {		
				addRet(sourceCName,targetCName,rType.IAS);
				addmethodDeps(sourceCName, targetCName,1);
			}
		}
	}

	// SpecialInvoke
	private void analysisSpecialInvoke(Value useValue, SootClass sClass,
			SootMethod sMethod, int lineNo) throws Exception {

		JSpecialInvokeExpr jSpecialInvokeExpr = (JSpecialInvokeExpr) useValue;
		String sourceCName = sClass.getName();
		SootClass targetClass = jSpecialInvokeExpr.getMethod()
				.getDeclaringClass();
		String targetCName = targetClass.getName();

		if (MapOfVertex.containsKey(targetCName) && !isParent(sourceCName, targetCName)) {
			if (!sourceCName.equals(targetCName)) {		
				addRet(sourceCName,targetCName,rType.IAS);
				addmethodDeps(sourceCName, targetCName,1);
			}
		}
	}

	// DynamicInvoke
	private void analysisDynamicInvoke(Value useValue, SootClass sClass,
			SootMethod sMethod, int lineNo) throws Exception {

		JDynamicInvokeExpr jDynamicInvokeExpr = (JDynamicInvokeExpr) useValue;
		String sourceCName = sClass.getName();
		SootClass targetClass = jDynamicInvokeExpr.getMethod()
				.getDeclaringClass();
		String targetCName = targetClass.getName();

		if (MapOfVertex.containsKey(targetCName) && !isParent(sourceCName, targetCName)) {
			if (!sourceCName.equals(targetCName)) {		//��䷽������
				addRet(sourceCName,targetCName,rType.IAS);
				addmethodDeps(sourceCName, targetCName,1);
			}
		}
	}

	//InterfaceInvoke
	private void analysisInterfaceInvoke(Value useValue, SootClass sClass,
			SootMethod sMethod, int lineNo) throws Exception {

		JInterfaceInvokeExpr jInterfaceInvokeExpr = (JInterfaceInvokeExpr) useValue;
		String sourceCName = sClass.getName();
		SootClass targetClass = jInterfaceInvokeExpr.getMethod()
				.getDeclaringClass();
		String targetCName = targetClass.getName();

		if (MapOfVertex.containsKey(targetCName) && !isParent(sourceCName, targetCName)) {
			if (!sourceCName.equals(targetCName)) {		//��䷽������
				addRet(sourceCName,targetCName,rType.IAS);
				addmethodDeps(sourceCName, targetCName,1);
			}
		}
	}

	//VirtualInvoke
	private void analysisVirtualInvoke(Value useValue, SootClass sClass,
			SootMethod sMethod, int lineNo) throws Exception {

		JVirtualInvokeExpr jVirtualInvokeExpr = (JVirtualInvokeExpr) useValue;
		String sourceCName = sClass.getName();
		SootClass targetClass = jVirtualInvokeExpr.getMethod()
				.getDeclaringClass();
		String targetCName = targetClass.getName();

		if (MapOfVertex.containsKey(targetCName) && !isParent(sourceCName, targetCName)) {
			if (!sourceCName.equals(targetCName)) {		//��䷽������
				addRet(sourceCName,targetCName,rType.IAS);
				addmethodDeps(sourceCName, targetCName,1);
			}
		}
	}

	// 添加方法依赖
	private void addmethodDeps(String sourceName, String targetName,int n) {
		TClass tClass=MapOfVertex.get(sourceName);
		Map<String,Integer> tmp=tClass.getMethodDeps();
		if(!tmp.isEmpty()&&tmp.containsKey(targetName)) {
			tmp.put(targetName, tmp.get(targetName)+n);
		}
		else {
			tmp.put(targetName, 1);
		}
		//System.out.println(tClass.getcName()+"-"+targetName+"="+tmp.get(targetName));
		tClass.setMethodDeps(tmp);
	//	System.out.println(tmp.getClass());;
	}

	//判断调用类型
	private int isInvokeExpr(Value useValue) {
		int no = 0;
		if (useValue instanceof JVirtualInvokeExpr)
			no = 1;
		if (useValue instanceof JInterfaceInvokeExpr)
			no = 2;
		if (useValue instanceof JDynamicInvokeExpr)
			no = 3;
		if (useValue instanceof JSpecialInvokeExpr)
			no = 4;
		if (useValue instanceof JStaticInvokeExpr)
			no = 5;
		return no;

	}
	
	//更新Ag关系
	private void updateAg() throws Exception {
		//System.out.println("=============Ag=================");
		for(Map.Entry<String, TClass> entry:MapOfVertex.entrySet()) {
			TClass c=entry.getValue();
			Map<String,Integer> tmp=c.getMethodDeps();
			for(Map.Entry<String, Integer> se:c.getAttrDeps().entrySet()) {
				String s=se.getKey();
				if(se.getValue()>1&&!tmp.containsKey(s)) {
					addRet(c.getcName(),s,rType.IAG);
					removeRet(c.getcName(),s,rType.IAS);
					//System.out.println(c.getcName()+"--------------"+s);
				}
			}
		}
	}
	//设置动态依赖关系
	private void setDynamciRet() throws Exception {
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
					//System.out.println("childClassName"+childClassName+"deps"+e.getFromClass());
				}	
			}
		}
	}
	private static boolean ignore(SootClass sootClass) {
		String className = sootClass.getName();
		boolean ignore = false;
		ignore = ignore || className.startsWith("com.sun.");
		ignore = ignore || className.startsWith("com.javax.tools.doclets.");
		ignore = ignore || className.startsWith("com.jrockit.mc.");
		ignore = ignore || className.startsWith("java.");
		ignore = ignore || className.startsWith("javafx.");
		ignore = ignore || className.startsWith("javax.");
		ignore = ignore || className.startsWith("jdk.");
		ignore = ignore || className.startsWith("sun.");
		ignore = ignore || className.startsWith("sunw.");
		ignore = ignore || className.startsWith("com.oracle.");
		ignore = ignore || className.startsWith("oracle.jrockit.jfr.");
		ignore = ignore || className.startsWith("netscape.javascript.");
		ignore = ignore || className.startsWith("netscape.security.");
		ignore = ignore || className.startsWith("org.apache.commons.lang.");
		ignore = ignore || className.startsWith("org.apache.derby.");
		ignore = ignore || className.startsWith("org.apache.felix.");
		ignore = ignore || className.startsWith("org.apache.jasper.");
		ignore = ignore || className.startsWith("org.apache.lucene.");
		ignore = ignore || className.startsWith("com.ibm.icu.");
		ignore = ignore || className.startsWith("org.eclipse.");
		ignore = ignore || className.startsWith("org.glassfish.jsp.api.");
		ignore = ignore || className.startsWith("org.ietf.jgss.");
		ignore = ignore || className.startsWith("org.jcp.xml.dsig.internal.");
		ignore = ignore || className.startsWith("org.netbeans.");
		ignore = ignore || className.startsWith("org.omg.");
		ignore = ignore || className.startsWith("org.openide.");
		ignore = ignore || className.startsWith("org.osgi.");
		ignore = ignore || className.startsWith("org.relaxng.datatype.");
		ignore = ignore || className.startsWith("org.sat4j.");
		ignore = ignore || className.startsWith("org.w3c.dom.");
		ignore = ignore || className.startsWith("org.xml.sax.");
		ignore = ignore || className.startsWith("org.hamcrest.");
		ignore = ignore || className.startsWith("org.objenesis.");
		ignore = ignore || className.startsWith("junit.");
		ignore = ignore || className.startsWith("net.");
		ignore = ignore || className.startsWith("org.junit.");
		ignore = ignore || className.startsWith("com.fasterxml.jackson.databind.");
		ignore = ignore || className.startsWith("com.fasterxml.jackson.core.");
		ignore = ignore || className.startsWith("com.fasterxml.jackson.annotation.");
		return ignore;
	}
	public void printInfo() {
		/*for(Map.Entry<String, TClass> entry:MapOfVertex.entrySet()) {
			System.out.println(entry.getKey());
			System.out.println("NOF"+NOF[entry.getValue().getcId()]);
			TClass c=entry.getValue();
			System.out.println("����");
			for(Map.Entry<String, Integer> se:c.getAttrDeps().entrySet()) {
				System.out.println(se.getKey()+":"+se.getValue());
			}
			System.out.println("����");
			for(Map.Entry<String, Integer> se:c.getMethodDeps().entrySet()) {
				System.out.println(se.getKey()+":"+se.getValue());
			}
			System.out.println("===============================");
		}*/
		System.out.println("=============edge=========");
		for (TClass tClass:SetOfClasses){
			System.out.println("类"+tClass.getcId()+tClass.getcName());
		}
		for(TEdge edge:SetOfRet) {
			//if(edge.getRtype()==rType.Dy)
			System.out.println(edge.getFromClass().getcName()+"+"+edge.getToClass().getcName()+"="+edge.getRtype());
		}
		System.out.println(SetOfRet.size());
	}
}
