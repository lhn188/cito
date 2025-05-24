package com.zzc.CITO.Qlearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CITOAgent {
	private int class_n;
	private double learning_rate;//学习率
	private double gamma;//折扣率
	private double epilson;//探索概率
	private double ep2;//探索概率平方
	private List<Integer> Actions;
	private Map<Long,List<Integer>> q_table;
	
	public Map<Long, List<Integer>> getQ_table() {
		return q_table;
	}
	
	public void setEpilson(double epilson) {
		this.epilson = epilson;
	}

	public void setEp2(double ep2) {
		this.ep2 = ep2;
	}

	public CITOAgent(double lr,double reward_decay,double e_greedy,List<Integer> actions) {
		learning_rate=lr;
		gamma=reward_decay;
		Actions=actions;
		epilson=e_greedy;
		ep2=epilson*epilson;
		q_table=new HashMap<Long,List<Integer>>();
		class_n=Actions.size();
	}
	public int choose_action(Long observation,List<Integer> order, int a_) {
		if(!check_state_exist(observation))
			setImpossible(order,observation);
		Random r=new Random();
		int chooseA;
		double random=r.nextDouble();

		if(random>epilson) {//0.15 选择当前状态下最大的Q对应的动作
			List<Integer> actions=q_table.get(observation);//将observation的动作序列赋予actions
			chooseA=randomChooseMaxArg(actions);
		}
		else{//0.85 随机选动作
			chooseA=r.nextInt(class_n);
			while(q_table.get(observation).get(chooseA)==Integer.MIN_VALUE) {
				chooseA=r.nextInt(class_n);
			}
		}
		if(random<ep2) {
			chooseA=a_;
		}
		return chooseA;
	}
	//将检测不存在的状态observation对应的动作内的数值替换为MIN_VALUE
	private void setImpossible(List<Integer> order,Long observation) {
		for(Integer i:order) {
			q_table.get(observation).remove(i);
			q_table.get(observation).add(i,Integer.MIN_VALUE);
		}
	}
	private int randomChooseMaxArg(List<Integer> actions) {
		Random r=new Random();
		int start=r.nextInt(class_n);
		int index;
		int maxQ=Integer.MIN_VALUE;
		int choose=start;
		for(int i=0;i<class_n;i++) {
			index=(i+start)%class_n;
			int tmp=actions.get(index);
			if(tmp>maxQ) {
				maxQ=tmp;
				choose=index;
			}
		}
		return choose;
	}
	public void learn(Long observation,int a,double r,Long s_,boolean done) {
		check_state_exist(s_);//检查本轮状态是否存在
		int q_predict=(int)q_table.get(observation).get(a);//取出上一状态动作a的Q值
		double q_target;
		if(!done) {		//非终态
			q_target=r+gamma*findMaxActionQ(s_);
		}else {
			q_target=r;
		}
		learning_rate=learning_rate>0.1?learning_rate-0.05:0.01;
		setQtable(observation,a,learning_rate*(q_target-q_predict));
	}
	//设置Q表
	private void setQtable(Long observation, int a, double d) {
		int sum=q_table.get(observation).get(a)+(int)d;
		//p32 Q(s,a)<-Q(s,a)+alpha*(r+gamma*maxQ(s',a')-Q(s,a))
		q_table.get(observation).remove(a);
		q_table.get(observation).add(a,sum);
	}
	//找到最大Q值得动作ֵ
	private double findMaxActionQ(Long s_) {
		int maxQ=Integer.MIN_VALUE;
		for(Integer a:q_table.get(s_)) {
			maxQ=maxQ>a?maxQ:a;
		}
		return maxQ;
	}
	private boolean check_state_exist(Long state) {
		//不存在该状态
		if(!q_table.containsKey(state)) {
			List<Integer> actions=new ArrayList<Integer>();
			for(int i=0;i<class_n;i++) {
				actions.add(i, 0);
			}
			q_table.put(state, actions);
			return false;
		}
		return true;
	}
	public static void main(String []args) {
		Random r=new Random();
		for(int i=0;i<100;i++) {
			System.out.println(r.nextDouble());
		}
	}
}
