package com.zzc.CITO.manager;

import java.util.ArrayList;
import java.util.List;

public class ResultData {
	private List<Integer> order;
	private String cost;
	private long time;
	private int GS;
	private int SS;
	private int Deps;
	
	public ResultData(List<Integer> order,String cost,long time,int GS,int SS,int Deps){
		this.order=order;
		this.cost=cost;
		this.time=time;
		this.GS=GS;
		this.SS=SS;
		this.Deps=Deps;
	}

	public List<Integer> getOrder() {
		return order;
	}
	public List<Integer> reOrder() {
		return order;
	}
	public void setOrder(List<Integer> order) {
		this.order = order;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getGS() {
		return GS;
	}

	public void setGS(int gS) {
		GS = gS;
	}

	public int getSS() {
		return SS;
	}

	public void setSS(int sS) {
		SS = sS;
	}

	public int getDeps() {
		return Deps;
	}

	public void setDeps(int deps) {
		Deps = deps;
	}

	@Override
	public String toString() {
		return "ResultData [order=" + order + ", cost=" + cost + ", time=" + time + ", GS=" + GS + ", SS=" + SS
				+ ", Deps=" + Deps + "]";
	}

}
