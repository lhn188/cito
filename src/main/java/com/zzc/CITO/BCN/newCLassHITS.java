package com.zzc.CITO.BCN;

import java.text.DecimalFormat;
import java.util.Arrays;

public class newCLassHITS {private int class_n;    //
    private double[] authority;//= {0.09,0.04,0.04,0.06,0.07,0.06,0.1,0.1,0.04,0.06,0.04,0.04,0.04,0.04};
    //网页hub值
    private double[] hub;//={0.09,0.04,0.04,0.06,0.07,0.06,0.1,0.1,0.04,0.06,0.04,0.04,0.04,0.04};
    //网页Authority值
    double[] rAuthority;    //误差ֵ
    double[] rHub;
    //类名列表
    private String[] classList;
    //path矩阵
    private int[][] path;

    public newCLassHITS(int class_n, int[][] path) {
        this.class_n = class_n;
        this.path = path;

        //authority(IC,类影响力)、hub（CC,类复杂性）初始化
       /* authority = importance;
        hub = importance;*/

        rHub = new double[class_n];
        rAuthority = new double[class_n];
    }

    public double[] getResultPageplus() {
        DecimalFormat df = new DecimalFormat("#.00");
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
        hub = out;
        authority = in;
        //System.out.println("hub:" + Arrays.toString(hub));
        System.out.println("authority:" + Arrays.toString(authority));

        double newSumHub = 0;
        double newSumAuthority = 0;

        //误差值，用于收敛判断
        double error = Integer.MAX_VALUE;
        double[] newHub = new double[class_n];
        double[] newAuthority = new double[class_n];
        double[] newRHub = new double[class_n];
        double[] newRAuthority = new double[class_n];

        //-----入度出度数目
        newHub = hub;
        newAuthority = authority;




        for (int k = 0; k < class_n; k++) {
            //计算新的一次各网页hub和authority值得总和
            newSumHub += newHub[k];
            newSumAuthority += newAuthority[k];
        }
        error = 0;


        //归一化处理
		/*for (int k = 0; k < class_n; k++) {
			//值与总和的比值ֵ
			newRHub[k] = newHub[k] / newSumHub;
			newRAuthority[k] = newAuthority[k] / newSumAuthority;
			//计算g个网页新的Hub和Authority值与上一次值得总误差
			error += Math.abs(newRHub[k] - rHub[k]) + Math.abs(newRAuthority[k] - rAuthority[k]);

			hub[k] = newHub[k];
			authority[k] = newAuthority[k];
			rHub[k] = newRHub[k];
			rAuthority[k] = newRAuthority[k];
		}*/

        //System.out.println("****最终收敛的网页的权威值和中心值****");
       // System.out.println("归一化处理后");
        double[] res=new double[class_n];
        for(int k=0; k<class_n; k++)
        {
            res[k]=(double) (authority[k]);
            //res[k]=(double)(rAuthority[k]);//仅有入度作为重要性指标
            //res[k]=(double)(rHub[k]+rAuthority[k]);//用入度和出度作为衡量类重要性指标
            //System.out.println("网页" + classList[k] + "入度:"+ df.format(rAuthority[k]) + ",出度: "+ df.format(rHub[k])+"重要度"+df.format(rAuthority[k]+rHub[k]));
        }
        return res;
    }
}
