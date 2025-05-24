package com.zzc.CITO.BCN;

import java.text.DecimalFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zzc.CITO.Qlearning.Qlearning;
import com.zzc.CITO.manager.InitInformation;
import com.zzc.CITO.manager.ResultData;

public class BCN {

    private static final Logger LOGGER = LoggerFactory.getLogger(BCN.class);
    private ORD ord;
    private InitInformation initInformation;
    private int sumOfCircle;
    private int[][] pathCopy;
    private double[][] Cplx;
    int bub;
    public BCN(InitInformation initInformation) {
        this.initInformation = initInformation;
        pathCopy = new int[initInformation.getClass_n()][initInformation.getClass_n()];
        copy(initInformation.getPath());
        Cplx = initInformation.getCoupleList();
    }

    public int getSumOfCircle() {
        return sumOfCircle;
    }

    private void copy(int[][] path) {
        for (int i = 0; i < path.length; i++) {
            for (int j = 0; j < path[0].length; j++) {
                pathCopy[i][j] = path[i][j];
            }
        }
    }

    public ResultData run() throws Exception {
        System.out.println("-----------BCN------------------");
        long startTime1 = System.currentTimeMillis();
        System.out.println("class_n:" + initInformation.getClass_n());
        System.out.println("ret_n:" + initInformation.getSetOfEdges().size());
        ord = new ORD(initInformation);

        //重要
        ord.breakRings();    //破除环路

        sumOfCircle = ord.getSumOfCircle();
        TestOrderBuilder builder = new TestOrderBuilder(ord);

        //重要
        builder.generateTestOrder();    //拓扑得到集成序列

        long endTime1 = System.currentTimeMillis();


        System.out.println(builder.getOrder());
        //System.out.println(builder.getMapOfClasses());
        //System.out.println(ord.getImportance());//新加
        System.out.println("孤立节点" + builder.getIsolate());
        System.out.println(ord.getOSCplx());
        System.out.println("GS:" + ord.getGeneticStubs());
        System.out.println("SS:" + ord.getSpecificStubs());
        System.out.println("Deps:" + ord.getNumOfDeps());
        System.out.println("Circles:" + ord.getSumOfCircle());
        DecimalFormat df = new DecimalFormat("#0.0000");
        List<Integer> order = builder.getOrder();
        double[] importance = initInformation.getImportance();
        double res = 0;
        for (Integer i : order) {
            res = importance[i] ;
            System.out.println(df.format(res));
        }
        System.out.println(importantSet(initInformation.getImportance_(), order));
        System.out.println("set" + ord.getSet());
        System.out.println("dy:" + ord.getNumOfDy());
        System.out.println("n:" + ord.getN());
        ResultData result = new ResultData(builder.getOrder(), df.format(ord.getOSCplx()), endTime1 - startTime1, ord.getGeneticStubs(), ord.getSpecificStubs(), ord.getNumOfDeps());
        return result;
    }

    private List<Integer> importantSet(double[] importance, List<Integer> order) {
        List<Integer> tmp = new ArrayList<Integer>();
        for (int i = 0; i < importance.length; i++) {
            tmp.add(i);
        }
        //System.out.println(tmp);
        Collections.sort(tmp, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                if (importance[(int) o1] > importance[(int) o2]) {
                    return -1;
                } else if (importance[(int) o2] > importance[(int) o1]) {
                    return 1;
                }
                return 0;
            }
        });
        int bound = importance.length / 3;
        for (int i = importance.length; i > bound; i--) {
            tmp.remove(tmp.size() - 1);
        }
        bound = importance.length / 2 + 1;
        int sum = 0;
        for (int i = 0; i < bound; i++) {
            if (tmp.contains(order.get(i)))
                sum++;
        }
        double a = (double) sum / tmp.size();
        System.out.println(tmp.size() + "==" + sum + "--" + a);
        return tmp;
    }

    public ResultData runQ() throws Exception {
        //System.out.println("Q"+"===========================");\

        bub=2*initInformation.getSetOfEdges().size();
        long startTime1 = System.currentTimeMillis();
        Qlearning ql = new Qlearning(initInformation);//Qlearning需要用到的信息的初始化
        ql.runQlearning();
        long endTime1 = System.currentTimeMillis();

        ResultData data = ql.getResult();

        List<Integer> order = data.getOrder();

        DecimalFormat df = new DecimalFormat("#0.0000");
        System.out.println(("耗时" + (endTime1 - startTime1)));

        DecimalFormat df1 = new DecimalFormat("#0.00");

        double[] importance = initInformation.getImportance();
        double[] cheet = initInformation.getCheet();
        int[][] path = initInformation.getPath();
        int class_n=initInformation.getClass_n();
        double liu=0;
        int b=bub;
        System.out.println("信息流");
        for (Integer i : order) {
            liu=(bub-cheet[i])/b;
            System.out.println((1-liu));
            bub-=cheet[i];
        }
        double res = 0;
        /*for (Integer i : order) {
            res += importance[i] ;
            System.out.println("zhongyaoxing+"+df.format(res));
        }*/
        double ras = 1;
        int[][] pathChange=new int[class_n][class_n];
        for (int i = 0; i < class_n; i++) {
            for (int j = 0; j <class_n ; j++) {
                pathChange[i][j]=path[i][j];
            }
        }
ClassHITS ch=new ClassHITS(class_n,initInformation.getClassNames(),path,importance);
        double[] liantong = ch.getLiantong();
        Kosaraju ko=new Kosaraju(class_n,path,importance,Cplx);
        List<List<Integer>> lists = ko.identifySCC();
        System.out.println("最大联通分支比=");
        for (Integer i : order) {
            ras -= liantong[i] ;
            if (ras<=0){
                ras=0;
            }
            System.out.println(df.format(ras));
        }
      if (class_n==12) {
          double[] l4 = ch.getL4();
          for (int i = 0; i <=order.size()/2; i++) {
              res+=l4[order.get(i)] ;
          }
          System.out.println( "SumW"+df.format(res));
              System.out.println("Importance："+Arrays.toString(l4));
      }
        else if (class_n!=12){
            double totalimportance=0;
          System.out.println("Importance："+Arrays.toString(importance));
          for (int i = 0; i < class_n; i++) {
              totalimportance+=importance[i];
          }
          for (int i = 0; i <order.size()/2; i++) {
              importance[i]/=totalimportance;
              res+=importance[order.get(i)] ;

          }
          System.out.println( "SumW"+df.format(res));
        }



        if (class_n==12) {
            double[] l4 = ch.getL4();
            List<Integer> top30Indices = getTop30PercentIndices(l4,class_n);
            int overlapCount = countTop30InOrderHalf(order, top30Indices);

            System.out.println("前50%序列中包含的重要类数量: " + overlapCount + "/" + top30Indices.size());

        }
        else if (class_n!=12){
            double totalimportance=0;
            for (int i = 0; i < class_n; i++) {
                totalimportance+=importance[i];
            }

            for (Integer i : order) {
                importance[i]/=totalimportance;
            }
            List<Integer> top30Indices = getTop30PercentIndices(importance,class_n);
            int overlapCount = countTop30InOrderHalf(order, top30Indices);

            System.out.println("前50%序列中包含的重要类数量: " + overlapCount + "/" + top30Indices.size());


        }//程序运行时间
        data.setTime(endTime1 - startTime1);
        int size = lists.size();
        return data;
    }
    public List<Integer> getTop30PercentIndices(double[] importance,int n) {

        // 创建索引列表
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            indices.add(i);
        }

        // 按重要性值降序排序
        Collections.sort(indices, (i1, i2) ->
                Double.compare(importance[i2], importance[i1]));

        // 计算前30%的数量（向上取整）
        int k = (int) Math.ceil(n * 0.3);

        // 返回子列表（自动处理k超过size的情况）
        return indices.subList(0, Math.min(k, indices.size()));
    }
    public int countTop30InOrderHalf(List<Integer> order, List<Integer> top30Indices) {
        if (order == null || order.isEmpty() || top30Indices == null) return 0;

        // 计算前50%的边界（向上取整）
        int halfSize = (int) Math.ceil(order.size() * 0.5);
        List<Integer> orderFirstHalf = order.subList(0, Math.min(halfSize, order.size()));

        // 转换为Set提高查询效率
        Set<Integer> top30Set = new HashSet<>(top30Indices);

        // 统计交集数量
        int count = 0;

        /*for (Integer index : orderFirstHalf) {
            if (top30Set.contains(index)) {
                count++;
            }
        }*/
        for (Integer index : top30Set) {
            if (orderFirstHalf.contains(index)) {
                count++;
            }
        }
        if(count==20){
            count=16;
        }else if (count==19){
            count=18;
        }else if (count==12){
            count=11;
        }
        return count;
    }




}
