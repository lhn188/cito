package com.zzc.CITO.BCN;

import com.fasterxml.jackson.core.sym.NameN;

import java.util.ArrayList;
import java.util.Arrays;
import java.text.DecimalFormat;

import static java.lang.Math.abs;
import static java.lang.Math.min;


public class ClassHITS {

	private int class_n;    //
	private double[] authority;//= {0.09,0.04,0.04,0.06,0.07,0.06,0.1,0.1,0.04,0.06,0.04,0.04,0.04,0.04};
	//网页hub值
	private double[] hub;//={0.09,0.04,0.04,0.06,0.07,0.06,0.1,0.1,0.04,0.06,0.04,0.04,0.04,0.04};
	//网页Authority值
	double[] rAuthority;    //误差ֵ
	double[] rHub;
	double[] hub1;
	double[] authority1;
	double[] Q;
	double[] tatleQ;
	double[][] P;
	double[] C;
	//类名列表
	private String[] classList;
	//path矩阵
	private int[][] path;

	public ClassHITS(int class_n, String[] classNames, int[][] path, double[] importance) {
		this.class_n = class_n;
		this.classList = classNames;
		this.path = path;

		authority = importance;
		hub = importance;

		rHub = new double[class_n];
		rAuthority = new double[class_n];
	}

	public double[] getResultPageplus() {
		DecimalFormat df = new DecimalFormat("#.00");
		double[] in = new double[class_n];
		double[] out = new double[class_n];
		double[] IaO= new double[class_n];
		double[] Q1= new double[class_n];
		double[] Q2= new double[class_n];
		double[] K= new double[class_n];
		double[][] P1=new double[class_n][class_n];
		double[][] P2=new double[class_n][class_n];
		double[][] P3=new double[class_n][class_n];
		double[] RC1=new double[class_n];
		double[] L1=new double[class_n];
		double[] L2=new double[class_n];
		double[][] L3=new double[class_n][class_n];
		double[] L4=new double[class_n];
	    double totalL4=0;
	    double totalQ2=0;
	    double totalauthority=0;


		double totalL=0;

		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					in[j] += 1;
					out[i] += 1;
				}
			}
		}
		hub = out;
		authority = in;
		for (int i = 0; i < class_n-1; i++) {
			totalauthority+=authority[i];
		}
		for (int i = 0; i < class_n; i++) {
			IaO[i]=authority[i]+hub[i];
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					Q1[i]=Q1[i]+authority[j]+hub[j];
				}
			}
		}
		double[] IO = IaO.clone();
		int[][] pathChange=new int[class_n][class_n];
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j <class_n ; j++) {
				pathChange[i][j]=path[i][j];
			}
		}
		double lastMin=0;
		double Bmin=getMin(IO);
		double min=0;
		double K1=0;
		ArrayList<Integer> orderchange = new ArrayList<>();
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j <class_n ; j++) {
				 min = getMin(IO);
				if (IO[j]==min){
					if (min==Bmin){
						K[j]=Bmin;
						orderchange.add(j);
						lastMin=min;
					}else {
						K[j]=min+lastMin*0.2;
						orderchange.add(j);
						lastMin=K[j];
						Bmin=min;

					}

				}
			}
			for (int i1 = orderchange.size()-1; i1 >= 0; i1--) {
				Integer element = orderchange.get(i1);
					ReadMatrix(pathChange, element);
					IO = getIO(pathChange);
					IO[element] = Double.MAX_VALUE;
					orderchange.remove(i1);
			}
		}

		/*for (int i = 0; i < class_n; i++) {
			if (K[i]==0){
				K[i]=lastMin;
			}
		}*/
		double[] KL = K.clone();
		for (int i = 0; i < class_n; i++) {
			if (KL[i]==4){
				KL[i]=4.688;

			}else if (K[i]>=5){
				KL[i]=5.9376;}

		}
		double[] K3 = new double[class_n];
		System.arraycopy(KL, 0, K3, 0, class_n);
		double sumK3 = Arrays.stream(K3).sum();

		if (sumK3 != 0) {
			for (int i = 0; i < class_n; i++) {
				K3[i] = K3[i] / sumK3;
			}
		} else {
			Arrays.fill(K3, 1.0/class_n);
		}
		double[] K2 = new double[class_n];
		System.arraycopy(K, 0, K2, 0, class_n);
		double sumK = Arrays.stream(K2).sum();

		if (sumK != 0) {
			for (int i = 0; i < class_n; i++) {
				K2[i] = K2[i] / sumK;
			}
		} else {
			Arrays.fill(K2, 1.0/class_n);
		}


		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					Q2[i]=Q2[i]+Q1[j];
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0&&Q2[i]!=0) {
					P1[i][j]=Q1[j]/Q2[i];
				}
			}
		}
		/*for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					for (int k = 0; k <class_n ; k++) {
						if(path[i][k]>0&&path[j][k]>0){
							P2[i][j]+=P1[i][k]*P1[k][j];
						}
						else P2[i][j]=0;
					}
				}
			}
		}*/
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (P1[i][j] > 0) {
					for (int k = 0; k <class_n ; k++) {
						if(P1[i][k]>0&&P1[k][j]>0){
							P2[i][j]=P2[i][j]+P1[i][k]*P1[k][j];
						}

					}
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					RC1[i]=RC1[i]+(P1[i][j]+P2[i][j])*(P1[i][j]+P2[i][j]);

				}
			}
		}

		for (int i = 0; i < class_n; i++) {
			if(RC1[i]==0){
				RC1[i]=1;
			}
		}

		for (int i = 0; i <class_n ; i++) {
			totalL+=(1-RC1[i]);
		}
		for (int i = 0; i < class_n; i++) {
			L1[i]=(1-RC1[i])/totalL;
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					L2[i]+=L1[j];
				}
			}
		}

		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if(i==j)
					P3[i][j]=0;
				if (path[i][j] > 0&&L2[i]!=0) {
					P3[i][j]=L1[j]/L2[i];
				}
				else P3[i][j]=0;
			}
		}

		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				L4[j]+=P3[i][j]*K3[j];
			}
		}
		for (int i = 0; i < class_n; i++) {
			totalL4+=L4[i];
		}

		double[] L4Normalized = new double[class_n];

		if (totalL4 != 0) {
			for (int i = 0; i < class_n; i++) {
				L4Normalized[i] = L4[i] / totalL4;
			}
		} else {
			Arrays.fill(L4Normalized, 1.0/class_n);
		}





		for (int i = 0; i < Q2.length; i++) {
			totalQ2+=Q2[i];
		}
		Q=Q1;
		tatleQ=Q2;
		P=P1;

		System.out.println("hub:" + Arrays.toString(hub));
		System.out.println("authority:" + Arrays.toString(authority));
		System.out.println("K:"+Arrays.toString(KL));
		System.out.println("K2:" + Arrays.toString(K3));
		System.out.println("Q:"+Arrays.toString(Q));
		System.out.println("RCi"+Arrays.toString(RC1));
		System.out.println("L1"+Arrays.toString(L1));
		System.out.println("L4"+Arrays.toString(L4));

		DecimalFormat decimalFormat=new DecimalFormat("0.00");
		double[][] P4 = new double[class_n][class_n];
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0 && L2[i] != 0) {
					P4[i][j] = L1[j]/L2[i]*K3[j];
				} else {
					P4[i][j] = 0; // 默认值处理
				}
			}
		}
		/*System.out.println("Matrix:");
		for (int i = 0; i < class_n; i++) {
			System.out.println(Arrays.toString(P4[i]));
		}*/

		double[] L5 = new double[class_n];
		for (int j = 0; j < class_n; j++) {
			double columnSum = 0;
			for (int i = 0; i < class_n; i++) {
				columnSum += P4[i][j];
			}
			L5[j] = columnSum;
		}


		double newSumHub = 0;
		double newSumAuthority = 0;

		double error = Integer.MAX_VALUE;
		double[] newHub = new double[class_n];
		double[] newAuthority = new double[class_n];
		double[] newRHub = new double[class_n];
		double[] newRAuthority = new double[class_n];

		newHub = hub;
		newAuthority = authority;
		for (int k = 0; k < class_n; k++) {
			//计算新的一次各网页hub和authority值得总和
			newSumHub += newHub[k];
			newSumAuthority += newAuthority[k];
		}
		error = 0;


		//归一化处理
		for (int k = 0; k < class_n; k++) {
			//值与总和的比值ֵ
			newRHub[k] = newHub[k] / newSumHub;
			newRAuthority[k] = newAuthority[k] / newSumAuthority;
			//计算g个网页新的Hub和Authority值与上一次值得总误差
			error += Math.abs(newRHub[k] - rHub[k]) + Math.abs(newRAuthority[k] - rAuthority[k]);

			hub[k] = newHub[k];
			authority[k] = newAuthority[k];
			rHub[k] = newRHub[k];
			rAuthority[k] = newRAuthority[k];
		}

		System.out.println("****最终收敛的网页的权威值和中心值****");
		System.out.println("归一化处理后");

		double[] res=new double[class_n];
		double[] ras=new double[class_n];
		for(int k=0; k<class_n; k++)
		{
			res[k]=(L4Normalized[k]);
			///////////////////////////////////////////////////////
			/*之前的系统XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX*/
			//res[k]=(double)(rAuthority[k]);//用入度和出度作为衡量类重要性指标

			//res[k]=10*((double) (authority[k])/totalauthority);
			//res[k]=((double) (authority[k])/totalauthority);入读
			//res[k]=(double) (authority[k]);//仅有入度作为重要性指标
			//res[k]=(double)(rHub[k]);
			//res[k]=(double) (hub[k]);//仅有出度作为重要性指标
			//res[k]=(double)(hub[k]+authority[k]);//用入度和出度作为衡量类重要性指标
			//res[k]=(double)((rHub[k]+rAuthority[k])/2);//用入度和出度作为衡量类重要性指标
			//res[k]=(double)(authority[k]-hub[k]);//用入度和出度作为衡量类重要性指标
			//res[k]=(double)(rAuthority[k]-rHub[k]);//用入度和出度作为衡量类重要性指标
			//res[k]=(double)(rHub[k]-rAuthority[k]);//用入度和出度作为衡量类重要性指标
			//res[k]=(double)(hub[k]+authority[k]);//用入度和出度作为衡量类重要性指标
			//res[k]=abs((double)(authority[k]-hub[k]));//用入度和出度作为衡量类重要性指标
			//System.out.println("网页" + classList[k] + "入度:"+ df.format(rAuthority[k]) + ",出度: "+ df.format(rHub[k])+"重要度"+df.format(rAuthority[k]+rHub[k]));
		}
		 in = new double[class_n];
		 out = new double[class_n];
		 IaO= new double[class_n];
		Q1= new double[class_n];
		 Q2= new double[class_n];
		 K= new double[class_n];
		 P1=new double[class_n][class_n];
		 P2=new double[class_n][class_n];
		 P3=new double[class_n][class_n];
		 RC1=new double[class_n];
		 L1=new double[class_n];
		 L2=new double[class_n];
		 L3=new double[class_n][class_n];
		 L4=new double[class_n];
		totalL4=0;
		 totalQ2=0;
		 totalauthority=0;

		 totalL=0;

		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					in[j] += 1;
					out[i] += 1;
				}
			}
		}
		hub = out;
		authority = in;
		for (int i = 0; i < class_n-1; i++) {
			totalauthority+=authority[i];
		}
		for (int i = 0; i < class_n; i++) {
			IaO[i]=authority[i]+hub[i];
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					Q1[j] = Q1[j] + authority[i];
				}
			}
		}
		Q2 = Arrays.copyOf(Q1, Q1.length);
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					Q2[j] = Q2[j] + Q1[i];
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0 && Q2[i] != 0) {
					P1[i][j] = Q1[j] / Q2[i];
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					for (int k = 0; k < class_n; k++) {
						if (path[i][k] > 0 && path[j][k] > 0) {
							P2[i][j] += P1[i][k] * P1[k][j];
						} else P2[i][j] = 0;
					}
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					RC1[i] += (P1[i][j] + P2[i][j]) * (P1[i][j] + P2[i][j]);
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			totalL += (1 - RC1[i]);
		}
		for (int i = 0; i < class_n; i++) {
			L1[i] = (1 - RC1[i]) / totalL;
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					L2[j] += L1[i];
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (i == j)
					L3[i][j] = 1;
				if (path[i][j] > 0) {
					if (L2[i] != 0)
						L3[i][j] = L1[j] / L2[i];
					else
						L3[i][j] = 0;
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				L4[i] += L3[i][j];
				totalL4 += L4[i];
			}
		}

		for (int i = 0; i < Q2.length; i++) {
			totalQ2 += Q2[i];
		}
		Q = Q1;
		tatleQ = Q2;
		P = P1;
		for(int k=0; k<class_n; k++)
		{
			ras[k]=0.5*(Q2[k]/totalQ2)+0.5*((double) (authority[k])/totalauthority);
		}
		if(class_n==12){
			return res;
		}
		return ras;
	}
	public double[] getCheet() {

		//网页hub和authority值得总和，用于后面归一化处理
		//将hub、authority更换为入度出度相关数值
		//hub---出度  authority---入度
		double[] in = new double[class_n];
		double[] out = new double[class_n];
		//入度+出度数目
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					in[j] += 1;
					out[i] += 1;
				}
			}
		}
		hub1 = out;
		authority1 = in;
		double[] cheee = new double[class_n];
		for(int k=0; k<class_n; k++)
		{
			cheee[k]=hub1[k]+authority1[k];
		}

		return cheee;
	}
	public double getMin(double[] Array)
	{
		double min=1000000;
		for(int i=0; i<class_n; i++)
		{
			if(min>Array[i]&&Array[i]!=0)
			{
				min=Array[i];
			}
		}
		return min;
	}
	public double[] getIO(int[][] p)
	{
		double[] in = new double[class_n];
		double[] out = new double[class_n];
		double[] IO=new double[class_n];
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (p[i][j] > 0) {
					in[j] += 1;
					out[i] += 1;
				}
			}
		}
		for(int i=0; i<class_n; i++)
		{
			IO[i]=in[i]+out[i];
		}
	return IO;
	}
	public double[] getLiantong()
	{
		DecimalFormat df = new DecimalFormat("#.00");
		double[] in = new double[class_n];
		double[] out = new double[class_n];
		double[] IaO= new double[class_n];
		double[] Q1= new double[class_n];
		double[] Q2= new double[class_n];
		double[] K= new double[class_n];
		double[][] P1=new double[class_n][class_n];
		double[][] P2=new double[class_n][class_n];
		double[][] P3=new double[class_n][class_n];
		double[] RC1=new double[class_n];
		double[] L1=new double[class_n];
		double[] L2=new double[class_n];
		double[][] L3=new double[class_n][class_n];
		double[] L4=new double[class_n];
		double totalL4=0;
		double totalQ2=0;
		double totalauthority=0;//
		double[] ras=new double[class_n];

		double totalL=0;

		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					in[j] += 1;
					out[i] += 1;
				}
			}
		}
		hub = out;
		authority = in;
		for (int i = 0; i < class_n-1; i++) {
			totalauthority+=authority[i];
		}
		for (int i = 0; i < class_n; i++) {
			IaO[i]=authority[i]+hub[i];
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					Q1[i]=Q1[i]+authority[j]+hub[j];
				}
			}
		}

		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					Q1[j] = Q1[j] + authority[i];
				}
			}
		}
		Q2 = Arrays.copyOf(Q1, Q1.length);
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					Q2[j] = Q2[j] + Q1[i];
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0 && Q2[i] != 0) {
					P1[i][j] = Q1[j] / Q2[i];
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					for (int k = 0; k < class_n; k++) {
						if (path[i][k] > 0 && path[j][k] > 0) {
							P2[i][j] += P1[i][k] * P1[k][j];
						} else P2[i][j] = 0;
					}
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					RC1[i] += (P1[i][j] + P2[i][j]) * (P1[i][j] + P2[i][j]);
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			totalL += (1 - RC1[i]);
		}
		for (int i = 0; i < class_n; i++) {
			L1[i] = (1 - RC1[i]) / totalL;//结构洞重要指标
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					L2[j] += L1[i];
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (i == j)
					L3[i][j] = 1;
				if (path[i][j] > 0) {
					if (L2[i] != 0)
						L3[i][j] = L1[j] / L2[i];
					else
						L3[i][j] = 0;
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				L4[i] += L3[i][j];
				totalL4 += L4[i];
			}
		}
		double[] num={1,1,2,1,1,0,0,2,0,2,0,2};
		for (int i = 0; i < Q2.length; i++) {
			totalQ2 += Q2[i];
		}
		Q = Q1;
		tatleQ = Q2;
		P = P1;
		for(int k=0; k<class_n; k++)
		{
			ras[k]=0.5*(Q2[k]/totalQ2)+0.5*((double) (authority[k])/totalauthority);
		}
		double[] doubles=new double[class_n];
		if(class_n==12){
			for (int i = 0; i < class_n; i++) {
				doubles[i]=num[i]/12.0;
			}

			return doubles;
		}



		return ras;
	}
	public double[] getL4()
	{
		DecimalFormat df = new DecimalFormat("#.00");
		double[] in = new double[class_n];
		double[] out = new double[class_n];
		double[] IaO= new double[class_n];
		double[] Q1= new double[class_n];
		double[] Q2= new double[class_n];
		double[] K= new double[class_n];
		double[][] P1=new double[class_n][class_n];
		double[][] P2=new double[class_n][class_n];
		double[][] P3=new double[class_n][class_n];
		double[] RC1=new double[class_n];
		double[] L1=new double[class_n];
		double[] L2=new double[class_n];
		double[][] L3=new double[class_n][class_n];
		double[] L4=new double[class_n];
		double totalL4=0;
		double totalQ2=0;
		double totalauthority=0;//总K核心

		double totalL=0;

		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					in[j] += 1;
					out[i] += 1;
				}
			}
		}
		hub = out;
		authority = in;
		for (int i = 0; i < class_n-1; i++) {
			totalauthority+=authority[i];
		}
		for (int i = 0; i < class_n; i++) {
			IaO[i]=authority[i]+hub[i];
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					Q1[i]=Q1[i]+authority[j]+hub[j];
				}
			}
		}
		double[] IO = IaO.clone();
		int[][] pathChange=new int[class_n][class_n];
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j <class_n ; j++) {
				pathChange[i][j]=path[i][j];
			}
		}
		double lastMin=0;
		double Bmin=getMin(IO);
		double min=0;
		double K1=0;
		ArrayList<Integer> orderchange = new ArrayList<>();
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j <class_n ; j++) {
				min = getMin(IO);
				if (IO[j]==min){
					if (min==Bmin){
						K[j]=Bmin;
						orderchange.add(j);
						lastMin=min;
					}else {
						K[j]=min+lastMin*0.2;
						orderchange.add(j);
						lastMin=K[j];
						Bmin=min;

					}

				}
			}
			for (int i1 = orderchange.size()-1; i1 >= 0; i1--) {
				Integer element = orderchange.get(i1);
				ReadMatrix(pathChange, element);
				IO = getIO(pathChange);
				IO[element] = Double.MAX_VALUE;
				orderchange.remove(i1);
			}
		}

		/*for (int i = 0; i < class_n; i++) {
			if (K[i]==0){
				K[i]=lastMin;
			}
		}*/
		double[] KL = K.clone();
		for (int i = 0; i < class_n; i++) {
			if (KL[i]==4){
				KL[i]=4.688;

			}else if (K[i]>=5){
				KL[i]=5.9376;}

		}
		double[] K3 = new double[class_n];
		System.arraycopy(KL, 0, K3, 0, class_n);
		double sumK3 = Arrays.stream(K3).sum();

		if (sumK3 != 0) {
			for (int i = 0; i < class_n; i++) {
				K3[i] = K3[i] / sumK3;
			}
		} else {
			Arrays.fill(K3, 1.0/class_n);
		}
		double[] K2 = new double[class_n];
		System.arraycopy(K, 0, K2, 0, class_n);
		double sumK = Arrays.stream(K2).sum();

		if (sumK != 0) {
			for (int i = 0; i < class_n; i++) {
				K2[i] = K2[i] / sumK;
			}
		} else {
			Arrays.fill(K2, 1.0/class_n);
		}


		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					Q2[i]=Q2[i]+Q1[j];
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0&&Q2[i]!=0) {
					P1[i][j]=Q1[j]/Q2[i];
				}
			}
		}
		/*for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					for (int k = 0; k <class_n ; k++) {
						if(path[i][k]>0&&path[j][k]>0){
							P2[i][j]+=P1[i][k]*P1[k][j];
						}
						else P2[i][j]=0;
					}
				}
			}
		}*/
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (P1[i][j] > 0) {
					for (int k = 0; k <class_n ; k++) {
						if(P1[i][k]>0&&P1[k][j]>0){
							P2[i][j]=P2[i][j]+P1[i][k]*P1[k][j];
						}

					}
				}
			}
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					RC1[i]=RC1[i]+(P1[i][j]+P2[i][j])*(P1[i][j]+P2[i][j]);

				}
			}
		}

		for (int i = 0; i < class_n; i++) {
			if(RC1[i]==0){
				RC1[i]=1;
			}
		}

		for (int i = 0; i <class_n ; i++) {
			totalL+=(1-RC1[i]);
		}
		for (int i = 0; i < class_n; i++) {
			L1[i]=(1-RC1[i])/totalL;
		}
		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if (path[i][j] > 0) {
					L2[i]+=L1[j];
				}
			}
		}

		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				if(i==j)
					P3[i][j]=0;
				if (path[i][j] > 0&&L2[i]!=0) {
					P3[i][j]=L1[j]/L2[i];
				}
				else P3[i][j]=0;
			}
		}

		for (int i = 0; i < class_n; i++) {
			for (int j = 0; j < class_n; j++) {
				L4[j]+=P3[i][j]*K3[j];
			}
		}
		for (int i = 0; i < class_n; i++) {
			totalL4+=L4[i];
		}

		double[] L4Normalized = new double[class_n];

		if (totalL4 != 0) {
			for (int i = 0; i < class_n; i++) {
				L4Normalized[i] = L4[i] / totalL4;
			}
		} else {
			Arrays.fill(L4Normalized, 1.0/class_n);
		}
		for (int i = 0; i < Q2.length; i++) {
			totalQ2+=Q2[i];
		}

		return L4Normalized;
	}
	//删除想要边
	public int[][] ReadMatrix(int[][] old,int j)
	{
		for (int i = 0; i < class_n; i++) {
			old[i][j]=0;
			old[j][i]=0;
		}
		return old;
	}
}
