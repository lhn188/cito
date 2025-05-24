package com.zzc.CITO.manager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zzc.CITO.BCN.ClassHITS;
import com.zzc.CITO.Base.C;
import com.zzc.CITO.Base.E;
import com.zzc.CITO.Base.TClass;
import com.zzc.CITO.Base.TEdge;
import com.zzc.CITO.SCplx.NewSCplx;
import com.zzc.CITO.SCplx.SCplx;
import com.zzc.CITO.analyzer.Analyzer;
import com.zzc.CITO.analyzer.Parser;


public class InitInformation {
	private Map<String,TClass> mapOfClasses; // 类名查找
	private static Map<Integer,TClass> mapOfClassesById;	//编号查找
	private String[] classNames;	//类名列表
	private int[] classId;
	private static Set<TEdge> setOfEdges;	//边集合
	private static Set<TClass> setOfClasses;	//类集合
	
	private static int [][]path;		//记录可达
	private static double []importance;	//类的重要性
	private double [] cheet;	//类的重要性
	public double[] getCheet() {
		return cheet;
	}




	private static double[] importance_;
	private int class_n;		//类数量
	private static double []couple;		//耦合
	private static double [][]coupleList;	//耦合矩阵
	private static double []NOF;			//类中特征的数量
	private List<C> simpleC;
	private List<E> simpleE;
	
	private long time;
	
	public InitInformation(double a2, double b2) {
		
	}

	public static double[][] getCoupleList() {
		return coupleList;
	}
	public static double[] getW(){
		return W;
	}

	public long getTime() {
		return time;
	}

	public List<C> getSimpleC() {
		return simpleC;
	}

	public List<E> getSimpleE() {
		return simpleE;
	}

	public Set<TEdge> getSetOfEdges() {
		return setOfEdges;
	}
	private  static double[] W ={0.48,0.52};
	private static double[]Aij = new double[74] ;//属性依赖
	private static double[]Mij =new double[74];//方法依赖
	public Set<TClass> getSetOfClasses() {
		return setOfClasses;
	}
	
	public int[][] getPath() {
		return path;
	}
	public int[] getCid(){return classId;}//获取cid
	public double[] getImportance() {
		return importance;
	}

	public String[] getClassNames() {
		return classNames;
	}

	public int getClass_n() {
		return class_n;
	}


	public Map<Integer, TClass> getMapOfClassesById() {
		return mapOfClassesById;
	}

	//初始化
	public void initData(Analyzer analyzer) throws Exception{
		mapOfClasses=analyzer.getMapOfVertex();
		setOfEdges=analyzer.getSetOfRet();
		setOfClasses=analyzer.getSetOfClasses();
		mapOfClassesById=analyzer.getMapOfVertexById();
		class_n=mapOfClasses.size();
		importance=new double[class_n];
		path=new int[class_n][class_n];
		couple=new double[class_n];
		coupleList=new double[class_n][class_n];
		classNames=new String[class_n];
		simpleC=new ArrayList<>();
		simpleE=new ArrayList<>();
		importance_=new double[class_n];
		//classId=new int[class_n];
		this.NOF=analyzer.getNOF();
		initMatrix();		//初始化path矩阵
	}
	//初始化
	public void initData(Parser parser) throws Exception{
		mapOfClasses=parser.getMapOfVertex();
		setOfEdges=parser.getSetOfRet();
		setOfClasses=parser.getSetOfClasses();
		mapOfClassesById=parser.getMapOfVertexById();
		class_n=mapOfClasses.size();
		importance=new double[class_n];
		path=new int[class_n][class_n];
		couple=new double[class_n];
		coupleList=new double[class_n][class_n];
		classNames=new String[class_n];
		classId=new int[class_n];
		simpleC=new ArrayList<>();
		simpleE=new ArrayList<>();
		this.NOF=parser.getNOF();
		importance_=new double[class_n];
		initMatrix();		//初始化path矩阵
	}
	//用于前端展示的精简表示
	private void initSimplList() {
		DecimalFormat df=new DecimalFormat("#0.00");
		for(TEdge edge:setOfEdges) {
			E e=new E(edge.fromIndex,edge.getToIndex(),edge.getRtype(),df.format(coupleList[edge.getFromIndex()][edge.getToIndex()]));
			simpleE.add(e);
		}
		for(TClass cl:setOfClasses) {
			C c=new C(cl.getcId(),cl.getcName(),df.format(importance[cl.getcId()]));
			simpleC.add(c);
		}
	}

	//矩阵初始化
	private void initMatrix() {
		initPath();		//初始化path矩阵，计算耦合度
		long startTime=System.currentTimeMillis();
		calculateW();//为了获得importance，用于赋予IC、CC初始值

		calculateImportance();	//计算重要性（倒不如说是ICi+CCi(类影响力+类复杂性)）

		long endTime=System.currentTimeMillis();
		time=endTime-startTime;
		initSimplList();		//初始化展示列表
	}

	private static void calculateW() {
		NewSCplx Cpl=new NewSCplx();
		//p17
		Cpl.calculateImpoW(Aij, Mij,Aij.length,2);
		W=Cpl.getW();
		/*for (int i = 0; i < W.length; i++) {
			System.out.println("1.5熵权法权值"+W[i]);
		}*/
		//importance=Cpl.getZ();//计算
	}
/*	public static double[] getW() {
		NewSCplx Cpl=new NewSCplx();
		//p17
		Cpl.calculateImpoW(NOF, couple,NOF.length,2);
		W=Cpl.getW();
		*//*for (int i = 0; i < W.length; i++) {
			System.out.println("1.5熵权法权值"+W[i]);
		}*//*
		return W;
		//importance=Cpl.getZ();//计算
	}*/
	private void calculateCouple(int i,int j,int k,double w1,double w2) {
		SCplx scplx=new SCplx(setOfClasses);
		scplx.setMaxValue();
		double []am ;
		//scplx.getAttr();
		//System.out.println(classNames[i]+"--"+"Mscplx"+scplx.getMaxMethod()+"---"+"Ascplx"+scplx.getMaxAttr());
//		System.out.println("Ascplx"+scplx.getMaxAttr());
		am=scplx.calculateAM(i,j);
		Aij[k]=am[0];
		Mij[k]=am[1];
		System.out.println("aij: "+Aij[k]+" mij: "+Mij[k]);
		coupleList[i][j]=scplx.calculateSCplx(i, j,w1,w2);
		/*for (double[] doubles : coupleList) {
			System.out.println(doubles);
		}*/
		couple[i]+=coupleList[i][j];
		//couple[i]++;
	}
	
	//初始化path矩阵
	private void initPath() {
		int ss=0;
		for(Map.Entry<String, TClass> entry:mapOfClasses.entrySet()) {

			TClass c=entry.getValue();
			classNames[c.getcId()]=c.getcName();
			int from=c.getcId();
			for(TEdge edge:c.getOutRelation()) {
				int to=edge.getToClass().getcId();
				path[from][to]=edge.getRtype();
				calculateCouple(from,to,ss,W[0],W[1]);
				ss=ss+1;
			}
		}
	}
	
	//计算节点重要性
	private void calculateImportance() {
		double maxC=0,maxN=0;
		for(int i=0;i<class_n;i++) {
			maxC=Math.max(maxC, couple[i]);
			maxN=Math.max(maxN, NOF[i]);
		}

		HITS();
		//System.out.println(importance_.length);
		for(int i=0;i<class_n;i++) {
			//importance[i]=0.5*couple[i]/maxC+0.5*NOF[i]/maxN;
			//importance_[i]=importance[i];
			//importance[i]=1;
			System.out.println(classNames[i]+"--"+importance[i]);
		}
		//HITS();
		/*for(int i=0;i<class_n;i++) {
			//importance[i]=0.5*couple[i]/maxC+0.5*NOF[i]/maxN;
			importance_[i]=importance[i];
			importance[i]=0;
			System.out.println(classNames[i]+"--"+importance[i]);
		}*/
	}
	//HITS收敛
	public void HITS() {
		ClassHITS hits=new ClassHITS(class_n,classNames,path,importance);	//hits迭代
		//importance=hits.getResultPage();
		importance=hits.getResultPageplus();
		cheet=hits.getCheet();
	}

	public static double[] getImportance_() {
		return importance_;
	}


	
}
