package com.zzc.CITO.BCN;

import java.io.IOException;
import java.util.*;

import com.zzc.CITO.Base.TClass;
import com.zzc.CITO.Base.TEdge;
import com.zzc.CITO.Base.rType;
import com.zzc.CITO.manager.InitInformation;


public class ORD {

	//private static final Logger LOGGER = LoggerFactory.getLogger(ORD.class);
	private int class_n;
	private Set<TClass> setOfClasses;
	private Map<Integer,TClass> MapOfVertexById;
	private Set<TEdge> setOfEdges;
	private Map<Integer,Integer> setOfRemove;
	
	private int[][] circle;    
	private double[][] weight;    
	private int[][] path;
	private double[] importance;
	private double OSCplx;
	private double[][] Cplx;
	
	private int sumOfCircle;
	private int geneticStubs;
	private int specificStubs;
	private int numOfDeps;
	private boolean [] GS;
	double[] in = new double[class_n];
	
	public ORD(InitInformation initInformation) {
		class_n=initInformation.getClass_n();
		setOfClasses=initInformation.getSetOfClasses();
		MapOfVertexById=initInformation.getMapOfClassesById();
		setOfEdges=initInformation.getSetOfEdges();
		setOfRemove=new HashMap<Integer,Integer>();
		weight=new double [class_n][class_n];
		circle=new int[class_n][class_n];
		path=initInformation.getPath();
		importance =initInformation.getImportance();
		Cplx=initInformation.getCoupleList();
		OSCplx=0;
		sumOfCircle=0;
		geneticStubs=0;
		specificStubs=0;
		numOfDeps=0;
		GS=new boolean [class_n];
	}
	
	public int getGeneticStubs() {
		return geneticStubs;
	}


	public int getNumOfDeps() {
		return numOfDeps;
	}

	public int getSpecificStubs() {
		return specificStubs;
	}


	public double getOSCplx() {
		return OSCplx;
	}

	public int getSumOfCircle() {
		return sumOfCircle;
	}


	public double[] getImportance() {
		return importance;
	}

	public int getClass_n() {
		return class_n;
	}

	public Map<Integer, TClass> getMapOfVertexById() {
		return MapOfVertexById;
	}

	public int[][] getPath() {
		return path;
	}

	public Set<TClass> getSetOfClasses() {
		return setOfClasses;
	}

	//打破环路
	public void breakRings() throws IOException {
		Kosaraju ko=new Kosaraju(class_n,path,importance,Cplx);
		//SCplx scplx=new SCplx(setOfClasses);
		//scplx.setMaxValue();
		boolean hasSCC=true;
		boolean First=true;
		while(hasSCC) {
			hasSCC=false;
			//找到所有强连通分量
			List<List<Integer>> SCCs=ko.identifySCC();
			for(List<Integer> scc:SCCs) {
				if(scc.size()==1)	continue;
				hasSCC=true;
				//找到强连通分量中的所有环路
				List<List<Integer>> rings=ko.identifyRing(scc);
				//System.out.println("=========Rings==========="+rings);
				weight=ko.getWeight();
				if(First) {
					int ringSize=rings.size();
					sumOfCircle+=ringSize;
				}
				//printMatrix();
				
				System.out.println("SCC"+scc);
				//打破环路
				breakRingsInSCC(scc,ko);
				
				//更新状态
				init();
				
				weight=ko.getWeight();
				
			}
			First=false;
		}
		
		//printMatrix();
	}
	
	private void init() {
		//importance=HITS();
		//System.out.println("importance:" + Arrays.toString(importance));
		for(int i=0;i<class_n;i++) {
			for(int j=0;j<class_n;j++) {
				weight[i][j]=0;
				circle[i][j]=0;
				path[i][j]=rType.NONE;
			}
		}
		/*for(int i=0;i<class_n;i++) {
			for(int j=0;j<class_n;j++) {
				if (path[i][j] > 0) {
					in[j] += 1;
				}
			}
		}
		//importance = in;
		System.out.println("动态authority:" + Arrays.toString(in));
		动态度量*/

		for(TEdge edge:setOfEdges) {
			path[edge.getFromIndex()][edge.getToIndex()]=edge.getRtype();
		}
		//InitInformation iif=new InitInformation();
	}

	//打破环路
	private void breakRingsInSCC(List<Integer> scc,Kosaraju ko) {
		TEdge edge=null;	//删除的边
		for(Integer from:scc) {
			for(Integer to:scc) {
				TEdge e=getEdge(from,to);	//该边
				if(e==null)	continue;
				//强相关边跳过
				if(e.getRtype()==rType.IAG||e.getRtype()==rType.II) continue;
				if(edge==null) {
					edge=e;
					continue;
				}
				int f=edge.getFromIndex(),t=edge.getToIndex();
				//System.out.println(weight[from][to]);
				//TEdge edge1=getEdge(f,t);加入另外一条边
				//System.out.println("另外一条边"+edge1);
				//------------------------------------------------------
				if(weight[from][to]>weight[f][t]) {
					edge=e;
					//System.out.println("边"+from+"----"+to+"==="+weight[from][to]);每个环路中每条边的权重
				}
				if(weight[from][to]==weight[f][t]) {
					if(Cplx[from][to]<Cplx[f][t]) {
						edge=e;
					}
					if(Cplx[from][to]==Cplx[f][t]) {
						if(importance[to]>importance[edge.getToIndex()])
							edge=e;
					}
				}//--------------------------------------------------------
				/*if(importance[to]>importance[edge.getToIndex()]){
					edge=e;}
				if(importance[to]==importance[edge.getToIndex()]){
					if(Cplx[from][to]<Cplx[f][t]) {
						edge=e;
					}
					if(Cplx[from][to]==Cplx[f][t]) {
						if(weight[from][to]>weight[f][t]) {
							edge=e;
					}
				}
//					System.out.println("边"+from+"----"+to+"==="+weight[from][to]);每个环路中每条边的权重
				}先节点，再复杂度最后边权值*/
			/*	if(importance[to]>importance[edge.getToIndex()]){
					edge=e;}
				if(importance[to]==importance[edge.getToIndex()]){
					if(weight[from][to]>weight[f][t]) {
						edge=e;
					}
					if(weight[from][to]==weight[f][t]) {
						if(Cplx[from][to]>Cplx[f][t]) {
							edge=e;
						}
					}
//					System.out.println("边"+from+"----"+to+"==="+weight[from][to]);每个环路中每条边的权重
				} 节点 --- 边权值 -- 复杂度 */
			/*	if(weight[from][to]>weight[f][t]) {
					edge=e;
					//System.out.println("边"+from+"----"+to+"==="+weight[from][to]);每个环路中每条边的权重
				}
				if(weight[from][to]==weight[f][t]) {
					if(importance[to]>importance[edge.getToIndex()]) {
						edge=e;
					}
					if(importance[to]==importance[edge.getToIndex()]) {
						if(Cplx[from][to]<Cplx[f][t])
							edge=e;
					}}*/
			}
		}
		//System.out.println(edge.getFromIndex()+"--"+edge.getToIndex());
		/*else if (importance[to]==importance[edge.getToIndex()]){
							Random random=new Random();
							boolean r = random.nextBoolean();
							System.out.println("布尔值为"+r);
							if(r){
								edge=e;
							}
							else edge=edge1;
						}
						else if(importance[to]<importance[edge.getToIndex()]){
							edge=edge1;
						}*///当都相等时 随机选择一条边
		if(edge==null)	return ;
		//动态依赖边
		if(edge.getRtype() == rType.Dy) {
	/*		TEdge edgeCause = edge.getCause();
			if(edgeCause!=null&&edgeCause.getRtype() == rType.IAS) {
				//AS导向，且不是抽象类，删除该边
				if(!edge.getToClass().isAbstract()) {
					removePath(edge);
				}
				//抽象类，删除AS
				else {
					removePath(edgeCause);
				}
				//removePath(edge);
			}
			//AG导向DY，删除自己
			else {
				removePath(edge);
			}*/
			removePath(edge);
		}
		//关联边，直接删除自己
		if(edge.getRtype() == rType.IAS) {
			removePath(edge);
		}
		//更新状态
		init();
	}
	private Set<Integer> set=new HashSet<>();
	private int numOfDy=0;
	public Set<Integer> getSet() {
		return set;
	}

	public int getNumOfDy() {
		return numOfDy;
	}
	private void removePath(TEdge edge) {
		System.out.println("删除边"+"["+edge.getFromIndex()+","+edge.getToIndex()+"]");
		//System.out.println("新加的"+"("+edge.getFromIndex()+","+edge.getToIndex()+")");
		set.add(edge.getToIndex());
		if(edge.getRtype()==rType.Dy)
			numOfDy++;
		//System.out.println(set);//测试桩
		double s=Cplx[edge.getFromIndex()][edge.getToIndex()];
		OSCplx+=s;
		System.out.println("splx:"+s+"   "+"O:"+OSCplx);//复杂度
		specificStubs++;
		String toName=edge.getToClass().getcName();
		TClass fromClass=edge.getFromClass();
		if(fromClass.getAttrDeps().containsKey(toName)) {
			numOfDeps+=fromClass.getAttrDeps().get(toName);
			System.out.println("属性依赖="+fromClass.getAttrDeps().get(toName));
		}
		else {
			System.out.println("属性依赖=0");
		}
		if(fromClass.getMethodDeps().containsKey(toName)) {
			numOfDeps+=fromClass.getMethodDeps().get(toName);
			System.out.println("方法依赖="+fromClass.getMethodDeps().get(toName));
		}else {
			System.out.println("方法依赖=0");
		}
		if(!GS[edge.getToIndex()])	{
			GS[edge.getToIndex()]=true;
			geneticStubs++;
		}
		//System.out.println(edge.getFromIndex()+"--"+edge.getToIndex());
		if(edge.getRtype() == rType.Dy) {
			doRemovePath(edge);
		}
		if(edge.getRtype() == rType.IAS) {
			List<TEdge> edgesShouldRemove = new ArrayList<TEdge>();
			edgesShouldRemove.add(edge);
			for(TEdge e : edge.getToClass().getInRelation()) {
				if(e.getRtype() == rType.II) {
					TEdge tmp = getEdge(edge.getFromIndex(), e.getFromIndex());
					if(tmp != null) {
						if(tmp.getRtype() == rType.Dy) {
							edgesShouldRemove.add(tmp);
						}
					}
				}
			}
			for(TEdge e : edgesShouldRemove) {
				doRemovePath(e);
			}
		}
	}
	private int n=0;
	public int getN() {
		return n;
	}
	private void doRemovePath(TEdge edge) {
		n++;
		//System.out.println(edge.getFromIndex()+"=="+edge.getToIndex()+"n"+n);
		edge.getFromClass().getOutRelation().remove(edge);
		edge.getToClass().getInRelation().remove(edge);
		setOfEdges.remove(edge);

		setOfRemove.put(edge.getFromIndex(),edge.getToIndex());
		System.out.println("remove " + edge.toString());
	}
	private TEdge getEdge(Integer from, Integer to) {
		TClass fromClass=MapOfVertexById.get(from);
		for(TEdge edge:fromClass.getOutRelation()) {
			if(edge.getToClass().getcId()==to)
				return edge;
		}
		return null;
	}
	private void printMatrix() {
		for(int i=0;i<circle.length;i++) {
			for(int j=0;j<circle[0].length;j++)
				System.out.print(circle[i][j]+" ");
			System.out.println();
		}
		System.out.println("========================");
		for(int i=0;i<circle.length;i++) {
			for(int j=0;j<circle.length;j++)
				System.out.print(weight[i][j]+" ");
			System.out.println();
		}
	}
	public double[] HITS() {
		newCLassHITS hits=new newCLassHITS(class_n,path);	//hits迭代
		//importance=hits.getResultPage();
		importance=hits.getResultPageplus();
		return importance;
	}

}
