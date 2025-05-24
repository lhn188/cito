package com.zzc.CITO.BCN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Kosaraju {
//static final Logger LOGGER = LoggerFactory.getLogger(Kosaraju.class);
	
	private int count;		//类的数量
	private int[][] path;	//path矩阵
	private double[][] weight;	//权值矩阵ֵ
	private int[][] circle;	//环路数量
	private double[] CircleSum;
	private int maxCircle;
	private double[]importance;	//类的重要性
	private double[][]Cplx;
	
	public Kosaraju(int n, int[][] pathMatrix,double []importance,double[][] Cplx) {
		count = n; 
		path = pathMatrix;
		weight = new double[count][count];
		circle = new int[count][count];
		CircleSum=new double[count];
		maxCircle=0;
		this.importance=importance;
		this.Cplx=Cplx;
		tuple=new double[2];
	}
	
	private boolean[] visit;
	private List<Integer> order;
	private double[] tuple;
	
	public double[][] getWeight() {
		return weight;
	}

	private void dfs1(int i) {
		visit[i] = true;
		for(int t = 0; t < count; t++) {
			if(path[i][t] != 0) {
				if(!visit[t]) {
					dfs1(t);
				}
			}
		}
		order.add(i);
	}
	
	private void dfs2(int i, List<Integer> scc) {
		visit[i] = true;
		scc.add(i);
		for(int t = 0; t < count; t++) {
			if(path[t][i] != 0) {
				if(!visit[t]) {
					dfs2(t, scc);
				}
			}
		}
	}
	/*
	 * 识别强连通分量
	 */
	public List< List<Integer> > identifySCC() {
		visit = new boolean[count];
		order = new ArrayList<Integer>();	//序列
		Arrays.fill(visit, false);
		
		// 正向深度优先遍历
		for(int i = 0; i < count; i++) {
			if(!visit[i]) {
				dfs1(i);
			}
		}
		Arrays.fill(visit, false);
		
		List<List<Integer>> sccs = new ArrayList<List<Integer>>(); 

		// 反向深度优先遍历
		for(int i = order.size() - 1; i >= 0; i--) {
			if(!visit[order.get(i)]) {
				List<Integer> scc = new ArrayList<Integer>();
				dfs2(order.get(i), scc);
				sccs.add(scc);
			}
		}
		return sccs;
	}
	
	/*
	 * 更新环路数量
	 */
	private void updateInCircleCount(List<Integer> r) {
		int size = r.size();
		// assert ( size >= 3 )
		for(int i = 1; i < size; i++) {
			circle[r.get(i - 1)][r.get(i)]++;
			CircleSum[r.get(i - 1)]++;
			CircleSum[r.get(i)]++;
			//LOGGER.debug("updateInCircleCount circle[" + r.get(i - 1) + "][" + r.get(i) + "]=" + circle[r.get(i - 1)][r.get(i)]);
		}
	}
	
	/*
	 * 添加环路
	 */
	private void addRing(List<Integer> r, List<List<Integer>> rings) {
		int rsize = r.size();
		if (rsize <= 1) {
			return;
		}
		int first = r.get(0);
		for (List<Integer> ring : rings) {
			if (rsize != ring.size()) {
				continue;
			}
			int i = 0;
			for (; i < rsize; i++) {
				if (first == ring.get(i)) {
					break;
				}
			}
			if (i == rsize) {
				continue;
			}
			i++;
			boolean bMatch = true;
			for (int j = 1; j < rsize - 1; j++) {
				if (r.get(j) != ring.get(i)) {
					bMatch = false;
					break;
				}
				// rsize - 1 ???
				i = (i + 1) % (rsize - 1);
			}
			if (bMatch) {
				return;
			}
		}

		//LOGGER.debug("dfs3 search to addRing:" + r.toString());
		rings.add(new ArrayList<Integer>(r));
		updateInCircleCount(r);
	}
	
	/*
	 * 深度优先遍历找到所有环路
	 */
	private void dfs3(int ind, List<Integer> scc, List<Integer> r, List<List<Integer>> rings) {
		visit[ind] = true;
		r.add(ind);
		for(int i = 0; i < count; i++) {
			if (path[ind][i] == 0) {
				continue;
			}
			if (scc.indexOf(i) < 0) {
				continue;
			}
			//LOGGER.debug("dfs3 search to i:" + i);
			
			if (visit[i]) {
				if (i == r.get(0)) {
					r.add(i);
					addRing(r, rings);	//添加环路
					r.remove(r.size() - 1);
				}
			}
			else {
				setWeight(r.get(0),i);
				//System.out.println("**********刚加入******************");
				//System.out.println("update weight[" + r.get(0) + "][" + i + "]=" + weight[r.get(0)][i]);
				
				dfs3(i, scc, r, rings);
			}
		}
		r.remove(r.size() - 1);
		visit[ind] = false;
	}
	
	/*
	 * 识别强连通分量中的环路
	 */
	public List<List<Integer>> identifyRing(List<Integer> scc) {
		tuple[0]=0.5;
		tuple[1]=0.5;
		List<List<Integer>> retVal = new ArrayList<List<Integer>>(); 
		Arrays.fill(visit, false);
		setMaxCircle();
		//printMatrix();
		for (Integer i : scc) {
			//LOGGER.debug("identifyRing i:" + i);
			dfs3(i, scc, new ArrayList<Integer>(), retVal);
		}
		//printMatrix();
		return retVal;
	}
	/*
	 * 设置边权值
	 */
	public void setWeight(int i,int j) {
		
		if(path[i][j]!=0) {
			double scp=Cplx[i][j];
			double impo=importance[i];
			double cir=circle[i][j]/maxCircle;
			//weight[i][j]=impo/scp;
			//weight[i][j]=(tuple[0]*cir+tuple[1]*impo)/(scp);
			weight[i][j]=(tuple[0]*cir+tuple[1]*impo)/(scp);
			//weight[i][j]=1/(scp);


			/*System.out.println("=========Weight===============");
			System.out.println(weight[i][j]);*/
		}	
	}
	private void setMaxCircle() {
		for(int i=0;i<count;i++) {
			for(int j=0;j<count;j++) {
				maxCircle=maxCircle>circle[i][j]?maxCircle:circle[i][j];
			}
		}
		maxCircle=maxCircle==0?1:maxCircle;
	}
	private void printMatrix() {
		System.out.println("=========Circle===============");
		for(int i=0;i<circle.length;i++) {
			for(int j=0;j<circle[0].length;j++)
				System.out.print(circle[i][j]+" ");
			System.out.println();
		}
		System.out.println("=========Weight===============");
		for(int i=0;i<circle.length;i++) {
			for(int j=0;j<circle.length;j++)
				System.out.print(weight[i][j]+" ");
			System.out.println();
		}
	}
}
