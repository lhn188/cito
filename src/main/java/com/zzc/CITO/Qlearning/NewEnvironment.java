package com.zzc.CITO.Qlearning;

import com.zzc.CITO.Base.TClass;
import com.zzc.CITO.Base.TEdge;
import com.zzc.CITO.Base.rType;
import com.zzc.CITO.manager.InitInformation;

import java.util.*;

public class NewEnvironment {
    private static int class_n;
    private Set<TClass> setOfClasses;
    private Set<TEdge> setOfEdges;
    private Map<Integer, TClass> mapOfClasses;
    private static int[][] path;
    private static double[][] costMatrix;

    public static double getMinCost() {
               return minCost;
    }

    public static List<Integer> getTestOrder() {
        return testOrder;
    }

    public static double[][] getCplx() {
        return Cplx;
    }

    public static double[][] getCostMatrix() {
        return costMatrix;
    }

    public double[] getImportance() {
        return importance;
    }

    public static double getCost() {
        return cost;
    }

    public static double[] getDeps() {
        return deps;
    }

    public boolean[] getGS() {
        return GS;
    }

    public int getGeneticStubs() {
        return geneticStubs;
    }

    public int getSpecificStubs() {
        return specificStubs;
    }

    public int getMinGS() {
        return minGS;
    }

    public int getMinSS() {
        return minSS;
    }

    public int getMinDeps() {
        return minDeps;
    }

    public int getNumOfDeps() {
        return numOfDeps;
    }

    public List<Integer> getOrder() {
        return order;
    }

    private List<Integer> order;    //最终的序列
    private Long observation;    //当前状态
    private static final int Max = 1000;
    private static final int c = 1;
    private static boolean[] select;
    private boolean[] createGS;
    private double[] importance;
    private ArrayList<Double>  importance1;
    private static double[] maximportance;
    private static double cost;
    private static double minCost = 10000000;
    private static List<Integer> testOrder;
    private static double[][] Cplx;

    private static double[] deps;
    private boolean[] GS;//通用select
    private int geneticStubs;
    private int specificStubs;
    private int minGS = 1000;
    private int minSS = 1000;
    private int minDeps = 1000;
    private int numOfDeps;
    private double totalCost = 0.0, totalProfit = 0.0;

    private double a;
    private double b;

    public void setImportance(double[] importance) {
        this.importance = importance;
    }

    public NewEnvironment(InitInformation initInformation) {
        class_n = initInformation.getClass_n();//类数目
        path = initInformation.getPath();//记录可达
        setOfClasses = initInformation.getSetOfClasses();//类的集合
        setOfEdges = initInformation.getSetOfEdges();//边的集合
        importance = initInformation.getImportance();//重要性
        mapOfClasses = initInformation.getMapOfClassesById();//编号查找
        costMatrix = new double[class_n][class_n];//花费矩阵
        Cplx = initInformation.getCoupleList();//耦合矩阵
        order = new ArrayList<Integer>();
        select = new boolean[class_n];
        createGS = new boolean[class_n];
        testOrder = new ArrayList<>();
        deps = new double[class_n];
        GS = new boolean[class_n];
        minCost = 10000000;
        minGS = 1000;
        importance1=new ArrayList<>();
        initCostMatrix();
        maximportance=importance;
        setImportance1();
    }
    private void initCostMatrix() {
        for (TEdge edge : setOfEdges) {
            int from = edge.getFromIndex();
            int to = edge.getToIndex();
            costMatrix[from][to] = Cplx[from][to];//from至to节点的耦合度赋值给代价矩阵
            if (edge.getRtype() == rType.IAG || edge.getRtype() == rType.II)//针对强相关关系，代价矩阵值乘5
                costMatrix[from][to] = Cplx[from][to];
        }

        //printMatrix();
    }

    private void printMatrix() {
        for (int i = 0; i < class_n; i++) {
            for (int j = 0; j < class_n; j++) {
                System.out.print(costMatrix[i][j] + "--");
            }
            System.out.println();
        }
    }
    //特定测试桩的构建
    private double calculateSpecificStub(int action) {
       // double totalCost = 0.0, totalProfit = 0.0;
        TClass source, target;
        double oneCost=0.0;
        if (class_n==12){
            for (int i = 0; i < class_n; i++) {
                if (path[action][i] > 0) {
                    if (select[i]) continue;    //i测试过
                    if (c*costMatrix[action][i]<0.72){
                        oneCost=c*costMatrix[action][i];
                        totalCost += oneCost;
                        System.out.println("单个测试桩复杂度"+action+"--"+i+"--"+oneCost+"  **  "+"总体测试桩复杂度"+totalCost);
                        if (!GS[i]) {
                            GS[i] = true;
                            geneticStubs++;
                        }
                        specificStubs++;}//printMatrix();
                    source = mapOfClasses.get(action);//下标对应类编号
                    target = mapOfClasses.get(i);
                    if (source.getAttrDeps().containsKey(target.getcName()))
                        numOfDeps += source.getAttrDeps().get(target.getcName());
                    if (source.getMethodDeps().containsKey(target.getcName()))
                        numOfDeps += source.getMethodDeps().get(target.getcName());
                }
                if (path[i][action] > 0 && !select[i]) {        //i依赖于action
                    totalProfit += costMatrix[i][action];
                }
            }

        }else {
            for (int i = 0; i < class_n; i++) {
                if (path[action][i] > 0) {
                    if (select[i]) continue;    //i测试过
                    //action依赖于i
                    if (c*costMatrix[action][i]<0.4){
                        oneCost=c*costMatrix[action][i];
                        totalCost += oneCost;
                        System.out.println("单个测试桩复杂度"+action+"--"+i+"--"+oneCost+"  **  "+"总体测试桩复杂度"+totalCost);
                        if (!GS[i]) {
                            GS[i] = true;
                            geneticStubs++;
                        }
                        specificStubs++;}
                    else {
                        oneCost=(c*costMatrix[action][i]*0.5);
                        totalCost+=oneCost;
                        System.out.println("单个测试桩复杂度"+action+"--"+i+"--"+oneCost+"  **  "+"总体测试桩复杂度"+totalCost);


                    }source = mapOfClasses.get(action);//下标对应类编号
                    target = mapOfClasses.get(i);
                    if (source.getAttrDeps().containsKey(target.getcName()))
                        numOfDeps += source.getAttrDeps().get(target.getcName());
                    if (source.getMethodDeps().containsKey(target.getcName()))
                        numOfDeps += source.getMethodDeps().get(target.getcName());
                }
                if (path[i][action] > 0 && !select[i]) {        //i依赖于action
                    totalProfit += costMatrix[i][action];
                }
            }

        }cost = totalCost;
        return totalProfit - totalCost;
    }
    public int reset() {
        order.clear();
        observation = (long) 1;
        Arrays.fill(select, false);
        Arrays.fill(GS, false);
        cost = 0;
        int index = 0;
        //不清楚deps
        for (int i = 0; i < class_n; i++) {
            for (int j = 0; j < class_n; j++) {
                if (path[i][j] > 0) {

                    deps[i]--;
                }
                if (path[j][i] > 0) {
                    deps[i]++;
                }
            }
            if (true) {//judgeStrong(i)) {
                if (deps[i] > deps[index] || (deps[i] == deps[index] && importance[i] > importance[index]))
                    index = i;
                System.out.println("index " + index);
            }
        }
        specificStubs = 0;
        geneticStubs = 0;
        numOfDeps = 0;
        return index;
    }
    public  void setImportance1() {
        for (double v : importance) {
            importance1.add(v);
        }
    }
    public int maxImpotance() {
        double maxImportance = importance1.get(0);
        int max = 0;
        for (int i = 1; i < importance1.size(); i++) {
            if (importance1.get(i) > maxImportance) {
                maxImportance = importance1.get(i);
                max = i;}
        }
        return max;
    }

    public int choose() {
        int choose=maxImpotance();
        importance1.remove(choose);
        importance1.add(choose,-1.0);
        return choose;
    }
    //ִ执行一步
    public List<Object> step(int action) {
        //double reward;
        //long observation_;
        //终态标志
        boolean done = false;
        //本轮的状态的observation
       // observation_ = class_n * observation - class_n + 1 + action + 1;
       // observation = observation_;
        //计算奖励
       // reward = calculateReward(action);
        calculateReward(action);
        //更新
        fresh(action);
        if (order.size() == class_n ) {
            done = true;
        }
        int a_ = updateDeps(action);
        //返回三元组
        List<Object> tuple = new ArrayList<Object>();
        //tuple.add(0, reward);
       // tuple.add(1, observation_);
        tuple.add(0, done);
        //tuple.add(1, a_);
        return tuple;
    }

    private int updateDeps(int action) {
        int index = -1;
        for (int i = 0; i < class_n; i++) {
            if (order.contains(i)) continue;
            if (path[i][action] > 0) {
                //deps[i]+=costMatrix[i][action];
                //deps[i]+=importance[i]*0.5;
                deps[i]++;
            }
            if (index == -1) {
                index = i;
            }
            if (true) {//judgeStrong(i)) {
                if (deps[i] > deps[index] || deps[i] == deps[index] && importance[i] > importance[index])
                    index = i;
            }
        }
        return index;
    }

    private boolean judgeStrong(int i) {
        for (int k = 0; k < class_n; k++) {
            //强相关关系
            if (!order.contains(k) && (path[i][k] == 2 || path[i][k] == 3)) return false;
        }
        return true;
    }

    //刷新环境
    private void fresh(int action) {
        order.add(action);
        select[action] = true;
    }

    //计算奖励 p30-p31
    private double calculateReward(int action) {
        //判断是否重复
        Map<Integer, Integer> temp = new HashMap<>();
        double reward = 0.0;
        if (order.size() < class_n) {    //未到达终态
        calculateSpecificStub(action);//算测试收益
        }

        //到达终态且代价最小
        if (order.size() == class_n - 1 && cost <= minCost) {

            minCost = cost;
            testOrder.clear();
            testOrder.addAll(new ArrayList<Integer>(order));
            testOrder.add(action);
            minGS = geneticStubs;
            minSS = specificStubs;
            minDeps = numOfDeps;
            return 10 * Max;
        } else {
            return reward;
        }
    }
}

