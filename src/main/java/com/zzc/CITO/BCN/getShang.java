package com.zzc.CITO.BCN;

import com.zzc.CITO.manager.InitInformation;

public class getShang {
    public  double[] W;


    public getShang() {
    }

    public double[] getW() {
        InitInformation initInformation=new InitInformation(0.5,0.5);
        W=initInformation.getW();
        for (double v : W) {
            System.out.println("1.5熵权法"+v);
        }
        return W;
    }
}

