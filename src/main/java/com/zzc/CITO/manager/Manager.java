package com.zzc.CITO.manager;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zzc.CITO.Base.TClass;
import com.zzc.CITO.Base.TEdge;
import org.springframework.stereotype.Repository;

import com.zzc.CITO.BCN.BCN;
import com.zzc.CITO.Base.C;
import com.zzc.CITO.Base.E;
import com.zzc.CITO.analyzer.Analyzer;
import com.zzc.CITO.analyzer.Parser;

@Repository
public class Manager {
	private static InitInformation initInformation;
	private static int sumOfCircle;
	//静态分析
	public static void staticAnalysisAnalyze(String fileName) throws Exception {
		Analyzer analyzer = new Analyzer();
		analyzer.analysis(fileName);
		initInformation = new InitInformation(0.5,0.5);
		initInformation.initData(analyzer);
	}
	public static void staticAnalysisParse(String fileName) throws Exception {
		Parser parser=new Parser();
		parser.parse(fileName);
		initInformation = new InitInformation(0.5,0.5);
		initInformation.initData(parser);
	}
	//静态分析
	public static void staticAnalysis(String fileName) throws Exception {
		if(fileName.equals("ANT")||fileName.equals("ATM")||fileName.equals("DNS")||fileName.equals("SPM")) {
			staticAnalysisParse(fileName);
		}
		else {
			staticAnalysisAnalyze(fileName);
		}
	}
	//获取系统属性
	public static SystemInfo getInfo() {
		SystemInfo info=new SystemInfo(initInformation.getClass_n(),sumOfCircle,initInformation.getSetOfEdges().size());
		return info;
	}
	//
	public static ResultData runBy(int method) throws Exception {
		BCN bcn=new BCN(initInformation);
		if(method==1) {
			/*long startTime=System.currentTimeMillis();
			ResultData resultQ=bcn.runQ();
			long endTime=System.currentTimeMillis();
			resultQ.setTime(endTime-startTime);
			return resultQ;*/
			ResultData result=bcn.runQ();
			sumOfCircle=bcn.getSumOfCircle();
			return result;
		}
			
		else {
			/*ResultData result=bcn.run();
			sumOfCircle=bcn.getSumOfCircle();
			return result;*/
			long startTime=System.currentTimeMillis();
			ResultData resultQ=bcn.runQ();
			long endTime=System.currentTimeMillis();
			resultQ.setTime(endTime-startTime);
			return resultQ;
		}
	}
	public List<E> getEdges() {
		return initInformation.getSimpleE();
	}
	public List<C> getClasses(){
		return initInformation.getSimpleC();
	}
	public static void test(String fileName) throws Exception {
		staticAnalysisAnalyze(fileName);		//使用soot自动分析类间关系的
		//staticAnalysisParse(fileName);			//直接使用分析好的csv
		//exportCSVfrominfo(initInformation,fileName);
		run(fileName,initInformation);
		//exportCSVfrominfo(initInformation,fileName);

	}
	public static void run(String fileName,InitInformation initInformation) throws Exception {
		//exportCSV csv=new exportCSV(System.getProperty("user.dir")+"\\CSV\\a.csv",initInformation.getSetOfEdges());
		System.out.println("****************************************************************************************************8");
		System.out.println(System.getProperty("user.dir"));
		//csv.run();
		BCN bcn=new BCN(initInformation);
		//bcn.runQ();
		//SystemInfo info=new SystemInfo(initInformation.getClass_n(),initInformation.getSetOfEdges().size(),0);
		/**
		 * 修改部分
		 */
		long startTime=System.currentTimeMillis();

		//bcn.runQ();//修改的地方

		ResultData res=bcn.run();

		long endTime=System.currentTimeMillis();
		long time=endTime-startTime;
		System.out.println("time:"+time);
		System.out.println(res.toString());
		//info.setCircle_n(bcn.getSumOfCircle());
		/**
		 *
		 */
	}

	public static void exportCSVfrominfo(InitInformation info,String filename){
		//String savepath="F:\\CITO\\CITOProject\\input_analysis\\";
		String savepath="D:\\CITO\\CITOProject\\input\\";
		//String savepath="F:\\CITO\\CITOProject\\input_analysis_2\\";
		String saveparentpath=savepath+filename;
		File file=new File(saveparentpath);
		if(!file.exists()){
			file.mkdir();
		}
	/*	for(TClass cl:info.getSetOfClasses()) {
			cl.getcId()
		}*/
		//输出CId_Name.csv
		exportcid_name(info.getSetOfClasses(),saveparentpath,filename);
		System.out.println(filename+" CId_Name.csv OUTPUT SUCCESS!");
		//输出CId_importance.csv
		System.out.println("11111111111111111111111111111111111111111111111111111");
		exportcid_importance(info.getImportance(),saveparentpath);
		System.out.println(filename+" CId_importance.csv OUTPUT SUCCESS!");
		//输出deps_type.csv
		exportdeps_type(info.getSetOfEdges(),saveparentpath);
		System.out.println(filename+" deps_type.csv OUTPUT SUCCESS!");
		//输出Couple_List.csv
		exportCouple_List(info.getCoupleList(),saveparentpath,info.getClass_n());
		System.out.println(filename+" Couple_List.csv OUTPUT SUCCESS!");
		//输出Attr_Method_deps.csv
		exportAttr_Method_deps(info.getSetOfClasses(),saveparentpath,filename);
		System.out.println(filename+" Attr_Method_deps.csv OUTPUT SUCCESS!");
	}

	public static String simplifyCName(String cname,String filename){
		String[] strname=cname.split("."+filename+".");
		int index=strname.length;
		String simplifyname=strname[index-1];
		return simplifyname;
	}
	public static void exportAttr_Method_deps(Set<TClass> tcl,String spath,String proname){
		String fpath=spath+"\\Attr_Method_deps.csv";
		try{
			File f=new File(fpath);
			if(f.exists()){
				f.delete();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write("Id,Name,Set_of_Attrdeps,Set_of_Methoddeps");
			bw.newLine();
			for(TClass ptcl:tcl){
				Map<String,Integer> attrDeps=ptcl.getAttrDeps();
				Map<String,Integer> methodDeps=ptcl.getMethodDeps();


				String[] splname=ptcl.getcName().split("\\."+proname+"\\.");
				int index=splname.length;
				System.out.println(ptcl.getcName());
				//System.out.println(index);
				String simplename=splname[index-1];
				System.out.println(simplename);
				String str1=ptcl.getcId()+","+simplename;

				String str2;
				String str3;
				if(attrDeps.size()==0){
					str2="null";
				}else{

					str2="\"{attrDeps:{";
					for(Map.Entry<String, Integer> entry:attrDeps.entrySet()){
						System.out.println(entry.getKey()+" "+entry.getValue().toString());
						str2+=(simplifyCName(entry.getKey(),proname)+":"+entry.getValue().toString()+",");
					}
					str2=str2.substring(0,str2.length()-1)+"}}\"";
					System.out.println(str2);
					//str2=str2.substring(str2.length()-1)+"}}\"";
				}
				if(methodDeps.size()==0){
					str3="null";
				}else{
					str3="\"{methodDeps:{";
					for(Map.Entry<String, Integer> entry:methodDeps.entrySet()){
						str3+=(simplifyCName(entry.getKey(),proname)+":"+entry.getValue().toString()+",");
					}

					str3=str3.substring(0,str3.length()-1)+"}}\"";
					System.out.println(str3);
				}

				String str=str1+","+str2+","+str3;
				System.out.println(str);

				bw.write(str);
				bw.newLine();
			}
			bw.close();
		}catch (FileNotFoundException e) {
			// File对象的创建过程中的异常捕获
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriter在关闭对象捕捉异常
			e.printStackTrace();
		}
	}

	public static void exportCouple_List(double[][] couple,String spath,int class_n){
		String fpath=spath+"\\Couple_List.csv";
		try {
			File f = new File(fpath);
			if (f.exists()) {
				f.delete();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write("From,To,Couple_value");
			bw.newLine();
			for(int i=0;i<class_n;i++){
				for(int j=0;j<class_n;j++){

						String str=(new Integer(i)).toString()+","+(new Integer(j)).toString()+","+(new Double(couple[i][j])).toString();
						System.out.println(str);
						bw.write(str);
						bw.newLine();

				}
			}
			bw.close();

		}catch (FileNotFoundException e) {
			// File对象的创建过程中的异常捕获
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriter在关闭对象捕捉异常
			e.printStackTrace();
		}
	}

	public static void exportdeps_type(Set<TEdge> ted,String spath){
		String fpath=spath+"\\deps_type.csv";
		try{
			File f=new File(fpath);
			if(f.exists()){
				f.delete();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write("From,To,Type,Stype");
			bw.newLine();
			for(TEdge pted:ted){
				String strft=(new Integer(pted.getFromIndex())).toString()+","+(new Integer(pted.getToIndex())).toString()+","+(new Integer(pted.getRtype())).toString();
				int type=pted.getRtype();
				String strst=(type<=2)?((type<=1)?((type<=0)?("None"):"As"):"Ag"):((type<=3)?"I":"Dy");
				String str=strft+","+strst;
				System.out.println(str);
				bw.write(str);
				bw.newLine();
			}
			bw.close();
			/*
			public static final int NONE = 0;
			public static final int IAS = 1;	//依赖、简单的聚合、相关标记为AS
			public static final int IAG = 2;	//组合，严格生命周期限制的聚合，标记为AG
			public static final int II = 3;		//继承
			public static final int Dy = 4;		//动态依赖
*/
		}catch (FileNotFoundException e) {
			// File对象的创建过程中的异常捕获
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriter在关闭对象捕捉异常
			e.printStackTrace();
		}
	}

	public static void exportcid_importance(double[] impor,String spath){
		String fpath=spath+"\\CId_importance.csv";
		try{
			File f=new File(fpath);
			if(f.exists()){
				f.delete();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write("Id,Importance");
			bw.newLine();
			int len=impor.length;
			for(int i=0;i<len;i++){
				String str=(new Integer(i)).toString()+","+(new Double(impor[i])).toString();
				System.out.println(str);
				bw.write(str);
				bw.newLine();
			}
			bw.close();
		}catch (FileNotFoundException e) {
			// File对象的创建过程中的异常捕获
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriter在关闭对象捕捉异常
			e.printStackTrace();
		}

	}
	public static void exportcid_name(Set<TClass> tcl,String spath,String proname){
		String fpath=spath+"\\CId_Name.csv";
		try{
			File f=new File(fpath);
			if(f.exists()){
				f.delete();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write("Id,Name");
			bw.newLine();
			for(TClass ptcl:tcl){
				String[] splname=ptcl.getcName().split("\\."+proname+"\\.");
				int index=splname.length;
				System.out.println(ptcl.getcName());
				//System.out.println(index);
				String simplename=splname[index-1];
				System.out.println(simplename);
				String str=ptcl.getcId()+","+simplename;
				bw.write(str);
				bw.newLine();
			}
			bw.close();
		}catch (FileNotFoundException e) {
			// File对象的创建过程中的异常捕获
			e.printStackTrace();
		} catch (IOException e) {
			// BufferedWriter在关闭对象捕捉异常
			e.printStackTrace();
		}

	}

	//主函数入口
	public static void main(String []args) {
		try {
			System.out.println("============ant==================");
			test("Ant");
			System.out.println("============ant==================");
			//test("atm");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
* (8,11,15)(6,12,18,21)(10,5,3,22,9)(4,1,2)(14,13)*/