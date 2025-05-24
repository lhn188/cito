package com.zzc.CITO.Qlearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zzc.CITO.BCN.TestOrderBuilder;
import com.zzc.CITO.manager.InitInformation;
import com.zzc.CITO.manager.ResultData;
import org.springframework.core.annotation.Order;
import polyglot.ast.New;


public class Qlearning {
	private NewEnvironment env;
	private CITOAgent agent;
	private int class_n;
	private List<Integer> order;
	public Qlearning(InitInformation initInformation) {
		class_n=initInformation.getClass_n();
		env=new NewEnvironment(initInformation);
	}
	public void runQlearning() {
		long startTime=System.currentTimeMillis();
		long endTime;
		List<Integer> actions=new ArrayList<>();//
		for(int i=0;i<class_n;i++)//动作初始化
			actions.add(0);
			endTime=System.currentTimeMillis();
			//环境初始化
			Long observation=(long) 1;
			int a_=env.reset();
			int action=-1;
			List<Object> step;
			while(true) {
				order=env.getOrder();
				action=env.choose();
				//与环境交互，执行一步
				step=env.step(action);
				boolean done=(boolean)step.get(0);
				//a_=(int)step.get(1);

				if(done) {
					break;
				}
			}	

		
		System.out.println("--------------Q-----------------");
		System.out.println(env.getTestOrder());
		System.out.println(env.getMinCost());
		System.out.println("GS:"+env.getGeneticStubs());
		System.out.println("SS:"+env.getSpecificStubs());
		System.out.println("Deps:"+env.getMinDeps());
		List<Integer> builder = env.getTestOrder();
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
