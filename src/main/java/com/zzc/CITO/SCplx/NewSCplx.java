package com.zzc.CITO.SCplx;

import java.text.DecimalFormat;
import java.util.Set;

import com.zzc.CITO.Base.TEdge;

public class NewSCplx {
	private static Set<TEdge> setOfEdges;
	private static double[][] R;
	private static double[][] P;
	private static double[] E;
	private static double[] W;
	private static double[] Z;
	
	public NewSCplx() {
		
	}
	
	
	public static double[] getW() {
		return W;
	}


	public static double[] getZ() {
		return Z;
	}

	private static void init(int m,int n) {
		R=new double[m][n];
		P=new double[m][n];
		E=new double[n];
		W=new double[n];
		Z=new double[m];
	}
	//计算重要性指标的权重
	public static void calculateEdgeW(double []Cir,double []Impo,int m,int n) {
		init(m,n);
		mergeFeature(Cir,Impo);
		normalize();
		calculateP();
		calculateE();
		calculateW();
		calculateZ();
		//printInfo();
	}
	
	//计算重要性指标的权重
	public static void calculateImpoW(double []NOF,double []CPL,int m,int n) {
		init(m,n);
		mergeFeature(NOF,CPL);
		normalize();//算Y 归一化
		calculateP();//算P 占比
		calculateE();//算E 信息熵
		calculateW();//算W 权重
		calculateZ();//算Z 评分
		printInfo();
	}
	//合并NOF和CPL
	private static void mergeFeature(double []NOF,double []CPL) {
		int n=NOF.length;
		System.out.println(n);
		for(int j=0;j<n;j++) {
			R[j][0]=NOF[j];
			System.out.println("R[j][0]:"+NOF[j]);
		}
		for(int j=0;j<n;j++) {
			R[j][1]=CPL[j];
			System.out.println("R[j][1]:"+CPL[j]);
		}
	}
	//归一化处理
	private static void  normalize() {
		double max,min;
		int times=R[0].length;
		int n=R.length;
		//System.out.println("==========normalize=========");
		for(int i=0;i<times;i++) {
			max=R[0][i];
			min=R[0][i];
			for(int j=0;j<n;j++) {
				max=Math.max(max, R[j][i]);
				min=Math.min(min, R[j][i]);
			}
			for(int j=0;j<n;j++) {
				P[j][i]=(R[j][i]-min)/(max-min);
				//System.out.println(P[j][i]);
			}
			//System.out.println("============");
		}
	}
	//计算p
	private static void calculateP() {
		double sum;
		int times=R[0].length;
		for(int i=0;i<times;i++) {
			sum=0;
			for(int j=0;j<R.length;j++) {
				sum+=P[j][i];
			}
			if(sum>0) {
				for(int j=0;j<R.length;j++) {
					P[j][i]=P[j][i]/sum;
				}
			}
		}	
	}
	//信息熵的计算
	private static void calculateE() {
		double sumMultiplylogP;
		double e;
		int times=R[0].length;
		int n=R.length;
		for (int i=0;i<times;i++) {
			sumMultiplylogP=0;
			e=0;
			for(int j=0;j<n;j++) {
				double a = P[j][i];
				if (a == 0.0) {
					sumMultiplylogP = sumMultiplylogP+0.0;
				} else {
					sumMultiplylogP = sumMultiplylogP+a*Math.log(a);
				}
			}
			e = Math.log(n)*sumMultiplylogP;
			E[i]=e;
		}
	}
	//计算权重
	public static void calculateW() {
		int times=R[0].length;
		double sum=0,w;
		for(int k=0;k<times;k++) {
			sum+=E[k];
		}
		for(int k=0;k<times;k++) {
			w=(1-E[k])/(times-sum);
			W[k]=w;
			System.out.println("熵权法######################"+W[k]);

		}
	}

	//计算得分
	private static void calculateZ() {
		int times=R[0].length;
		int n=R.length;
		for(int j=0;j<n;j++) {
			for(int i=0;i<times;i++) {
				Z[j]+=W[i]*R[j][i];
				//Z[j]+=0.5*R[j][i];
				//System.out.println("************************"+Z[j]);
				//Z[j]+=W[i]*R[j][i];
			}
			//System.out.println(Z[j]);
		}
	}
	private static void printInfo() {
		DecimalFormat df=new DecimalFormat("#.00");
		System.out.println("============R==============");
		for(int i=0;i<R.length;i++) {
			for(int j=0;j<R[0].length;j++) {
				System.out.print(df.format(R[i][j]));
			}
			System.out.println();
		}
		System.out.println("============P==============");
		for(int i=0;i<R.length;i++) {
			for(int j=0;j<R[0].length;j++) {
				System.out.print(df.format(P[i][j]));
			}
			System.out.println();
		}
		System.out.println("================E==============");
		for(int i=0;i<R[0].length;i++) {
			System.out.println(df.format(E[i]));
		}
		System.out.println("====================W===============");
		for(int i=0;i<R[0].length;i++) {
			System.out.println(df.format(W[i]));
		}
		System.out.println("====================Z==============="+Z.length);
		for(int i=0;i<R.length;i++) {
			System.out.println(df.format(Z[i]));
		}
	}
	public static void main(String []args) {
		/*R=new double[][] {
			{1.00,0.00,1.00,0.00,0.50,1.00,1.00,1.00,1.00},
			{1.00,1.00,0.00,1.00,0.50,1.00,1.00,1.00,1.00},
			{0.00,1.00,0.33,1.00,0.50,1.00,1.00,1.00,1.00},
			{1.00,1.00,0.00,1.00,0.50,1.00,0.87,1.00,1.00},
			{1.00,0.00,1.00,1.00,1.00,0.00,1.00,1.00,0.00},
			{1.00,1.00,1.00,1.00,0.50,1.00,1.00,0.00,1.00},
			{1.00,1.00,0.00,1.00,0.50,1.00,0.00,1.00,1.00},
			{0.50,1.00,0.33,1.00,1.00,1.00,1.00,1.00,1.00},
			{1.00,1.00,0.67,1.00,0.00,1.00,1.00,1.00,1.00},
			{1.00,0.00,1.00,1.00,1.00,1.00,1.00,1.00,1.00},
			{1.00,1.00,0.67,1.00,0.50,1.00,1.00,1.00,1.00}
		};
		
		R=new double[][] {
			
			{100,90,100,84,90,100,100},
			{100,100,78.6,100,90,100,100},
			{75,100,85.7,100,90,100,100},
			{100,100,78.6,100,90,100,94.4},
			{100,90,100,100,100,90,100},
			{100,100,100,100,90,100,100},
			{100,100,78.6,100,90,100,55.6},
			{87.5,100,85.7,100,100,100,100},
			{100,100,92.9,100,80,100,100},
			{100,90,100,100,100,100,100},
			{100,100,92.9,100,90,100,100}
		};
		*/
		R=new double[][] {
			{1,2},{1,2},{2,2},{0,1},{0,1}
		};
		W=new double[11];
		E=new double[11];
		Z=new double[11];
		P=new double[R.length][R[0].length];
		normalize();
		calculateP();
		calculateE();
		calculateW();
		calculateZ();
		printInfo();
	}
}

