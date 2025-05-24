package com.zzc.CITO.Qlearning;

import com.zzc.CITO.manager.InitInformation;
import com.zzc.CITO.manager.ResultData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OldQlearning {
    private Environment env;
    private CITOAgent agent;
    private int class_n;
    private List<Integer> order;

    public OldQlearning(InitInformation initInformation) {
        class_n=initInformation.getClass_n();
        env=new Environment(initInformation);
    }
    public void runQlearning() {
        long startTime=System.currentTimeMillis();
        long endTime;
        List<Integer> actions=new ArrayList<>();//
        for(int i=0;i<class_n;i++)//动作初始化
            actions.add(0);
        //agent=new CITOAgent(0.01,0.9,0.85,actions);
        //System.out.println(env.reset());
        endTime=System.currentTimeMillis();
        //if(endTime-startTime>80000)	break;
        //System.out.println(i);
        //环境初始化
        Long observation=(long) 1;
        int a_=env.reset();
        int action=-1;
        List<Object> step;
        while(true) {
            order=env.getOrder();
            //选择动action=agent.choose_action(observation,order,a_);
            //action=env.choose();
            action=env.choose();
            //与环境交互，执行一步
            step=env.step(action);
            //step包含计算奖励reward（0）、本轮的状态的observation_（1）、终态标志done(2)、a_新index(3)
            //得到下一状态，奖励，结束标记
            Long state_=(Long)step.get(1);
            double r=(double)step.get(0);
            boolean done=(boolean)step.get(2);
            a_=(int)step.get(3);
            //学习 observation上一个状态序号 上一个状态选的动作action r奖励 state_本轮状态的序号 done终态标志
            //agent.learn(observation, action,r ,state_ ,done);

            observation=state_;
            if(done) {
                //System.out.println(i+"=="+env.getMinCost());
                //System.out.println(env.getOrder());
                //System.out.println(env.getCost());
                //printQtable(agent.getQ_table());
                break;
            }
        }


        System.out.println("--------------Q-----------------");
        System.out.println(env.getTestOrder());
        System.out.println(env.getMinCost());
        System.out.println("GS:"+env.getGeneticStubs());
        System.out.println("SS:"+env.getSpecificStubs());
        System.out.println("Deps:"+env.getMinDeps());
        //System.out.println(agent.getQ_table());
        //t();
    }

    private void printQtable(Map<Integer, List<Integer>> map) {
        for(Map.Entry<Integer, List<Integer>> entry:map.entrySet()) {
            System.out.println(entry.getKey()+"--"+entry.getValue());
        }
    }
    private void printTestOrder(List<Integer> order) {

        for(Integer i:order)
            System.out.print(i+"-");
        System.out.println();
    }

    public ResultData getResult() {
        double cost=env.getMinCost();
        DecimalFormat df=new DecimalFormat("#0.0000");
        ResultData result=new ResultData(env.getTestOrder(),df.format(cost),0,env.getGeneticStubs(),env.getSpecificStubs(),env.getMinDeps());
        return result;
    }
}
