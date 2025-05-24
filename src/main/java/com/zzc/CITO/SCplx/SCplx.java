package com.zzc.CITO.SCplx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zzc.CITO.BCN.getShang;
import com.zzc.CITO.Base.TClass;
import com.zzc.CITO.manager.InitInformation;

public class SCplx {
    private static int maxAttr;
    private static int maxMethod;
    private static Set<TClass> setOfClasses;
    public double[] W={0.5,0.5};


    public SCplx(Set<TClass> setC) {
        setOfClasses = setC;
    }
   // InitInformation initInformation=new InitInformation(0.5,0.5);
   // getShang getShang = new getShang();

    public void setMaxValue() {
        maxAttr = calMaxAttr();
        maxMethod = calMaxMethod();
       /* W = getShang.getW();
        for (double v : W) {
            System.out.println("shangquanzhi" + v);
        }*/
        //getShang getShang=new getShang();

    }

    public int getMaxAttr() {
        return maxAttr;
    }

    public int getMaxMethod() {
        return maxMethod;
    }

    //输出方法属性
    public void getAttr() {
        for (TClass setOfClass : setOfClasses) {
            System.out.println(setOfClass);
        }
    }

    //计算最大方法调用ֵ
    private static int calMaxMethod() {
        int maxmethod = 0;
        for (TClass cinfo : setOfClasses) {
            if (maxmethod < cinfo.searchMaxMethodDep())
                maxmethod = cinfo.searchMaxMethodDep();
        }
        return maxmethod;
    }

    //计算最大属性调用ֵ
    private static int calMaxAttr() {
        int maxattr = 0;
        for (TClass cinfo : setOfClasses) {
            if (maxattr < cinfo.searchMaxAttrDep())
                maxattr = cinfo.searchMaxAttrDep();
        }
        return maxattr;
    }

    private TClass findClassById(Integer from) {
        for (TClass c : setOfClasses) {
            if (c.getcId() == from)
                return c;
        }
        return null;
    }
    public double[] calculateAM(int i, int j) {
        TClass sci_i = findClassById(i);
        TClass sci_k = findClassById(j);
        // 初始化
        double scplx = 0.0;
        int sizeOfAttr = 0;
        int sizeOfMethod = 0;
        String targetCName = sci_k.getcName();
        int targetid = sci_k.getcId();
        //计算属性依赖个数
        if (sci_i.getAttrDeps() != null && sci_i.getAttrDeps().containsKey(targetCName)) {
            sizeOfAttr = sci_i.getAttrDeps().get(targetCName);
            //System.out.println("类"+sci_i.getcId()+"sci_i.getAttrDeps()=="+sizeOfAttr+" -- "+sci_k.getcId());
        } else {
            sizeOfAttr = 0;
        }
        //计算方法依赖个数ֵ
        if (sci_i.getMethodDeps() != null && sci_i.getMethodDeps().containsKey(targetCName)) {
            sizeOfMethod = sci_i.getMethodDeps().get(targetCName);
        } else {
            sizeOfMethod = 0;
        }
        double[] am=new double[2];
        //System.out.println("类" + sci_i.getcId() + "--" + sci_k.getcId() + "属性依赖=" + sizeOfAttr + " 方法依赖" + sizeOfMethod);
        //System.out.println(""+sci_i.getcId()+"属性"+sizeOfAttr+"方法："+sizeOfMethod);
        // 归一化处理
        double _sizeOfAttr = (double) sizeOfAttr / maxAttr;
        double _sizeOfMethod = (double) sizeOfMethod / maxMethod;
        am[0]=_sizeOfAttr;
        am[1]=_sizeOfMethod;
        //scplx = Math.sqrt(_sizeOfAttr * _sizeOfAttr / 2 + _sizeOfMethod * _sizeOfMethod / 2);
        return am;
    }
    // 计算耦合度
    public double calculateSCplx(int i, int j,double w1,double w2) {
        TClass sci_i = findClassById(i);
        TClass sci_k = findClassById(j);
        // 初始化
        double scplx = 0.0;
        int sizeOfAttr = 0;
        int sizeOfMethod = 0;
        String targetCName = sci_k.getcName();
        int targetid = sci_k.getcId();
        //计算属性依赖个数
        if (sci_i.getAttrDeps() != null && sci_i.getAttrDeps().containsKey(targetCName)) {
            sizeOfAttr = sci_i.getAttrDeps().get(targetCName);
            //System.out.println("类"+sci_i.getcId()+"sci_i.getAttrDeps()=="+sizeOfAttr+" -- "+sci_k.getcId());
        } else {
            sizeOfAttr = 0;
        }
        //计算方法依赖个数ֵ
        if (sci_i.getMethodDeps() != null && sci_i.getMethodDeps().containsKey(targetCName)) {
            sizeOfMethod = sci_i.getMethodDeps().get(targetCName);
        } else {
            sizeOfMethod = 0;
        }
        System.out.println("类" + sci_i.getcId() + "--" + sci_k.getcId() + "属性依赖=" + sizeOfAttr + " 方法依赖" + sizeOfMethod);
        //System.out.println(""+sci_i.getcId()+"属性"+sizeOfAttr+"方法："+sizeOfMethod);
        // 归一化处理
        double _sizeOfAttr = (double) sizeOfAttr / maxAttr;
        double _sizeOfMethod = (double) sizeOfMethod / maxMethod;
        //scplx = Math.sqrt(_sizeOfAttr * _sizeOfAttr / 2 + _sizeOfMethod * _sizeOfMethod / 2);
        scplx = Math.sqrt(_sizeOfAttr * _sizeOfAttr *w1 + _sizeOfMethod * _sizeOfMethod*w2);

        return scplx;
    }

    public static void main(String[] args) {
        TClass A = new TClass(1, "A");
        TClass B = new TClass(2, "B");
        TClass C = new TClass(3, "C");
        Map<String, Integer> mapA = new HashMap<String, Integer>();
        Map<String, Integer> mapD = new HashMap<String, Integer>();
        mapA.put("A", 3);
        mapD.put("A", 4);
        B.setAttrDeps(mapA);
        B.setMethodDeps(mapD);
        Map<String, Integer> mapB = new HashMap<String, Integer>();
        Map<String, Integer> mapC = new HashMap<String, Integer>();
        mapB.put("B", 2);
        mapC.put("B", 8);
        A.setAttrDeps(mapB);
        A.setMethodDeps(mapC);

        Set<TClass> setOfClasses = new HashSet<TClass>();
        setOfClasses.add(A);
        setOfClasses.add(B);
        setOfClasses.add(C);

        SCplx s = new SCplx(setOfClasses);
        s.setMaxValue();
    }
}
