package com.zzc.CITO.BCN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zzc.CITO.Base.TClass;
import com.zzc.CITO.Base.TEdge;


public class TestOrderBuilder {
	private int class_n;
	private Map<Integer,TClass>mapOfClasses;
	private Set<TClass> setOfClasses;
	private double[] importance;
	private List<Integer> order;
	private List<Integer> isolate;
	
	public TestOrderBuilder(ORD ord) {
		this.class_n=ord.getClass_n();
		this.mapOfClasses=ord.getMapOfVertexById();
		this.setOfClasses=ord.getSetOfClasses();
		importance=ord.getImportance();
		order=new ArrayList<>();
		isolate=new ArrayList<>();
	}
	public int compare(Object o1, Object o2) {
		if(importance[(int)o1]>importance[(int)o2]) {
			return -1;
		}
		else if(importance[(int)o2]>importance[(int)o1]) {
			return 1;
		}
		return 0;
	}
	public List<Integer> getOrder() {
		return order;
	}

	public Map<Integer, TClass> getMapOfClasses() {
		return mapOfClasses;
	}

	public double[] getImportance() {
		return importance;
	}

	public List<Integer> getIsolate() {
		return isolate;
	}

	public void generateTestOrder() {
		//printInfo();
		int[] depth=new int[class_n];
		Arrays.fill(depth, 0);
		List<Integer> tmp=new LinkedList<>();
		int index,size=class_n;
		for(TClass c:setOfClasses) {
			index=c.getcId();
			depth[index]=c.getOutRelation().size();
			if(c.getOutRelation().size()==0&&c.getInRelation().size()==0) {
				depth[index]=-1;
				size--;
				isolate.add(c.getcId());
				continue;
			}
			if(depth[index]==0) {
				tmp.add(index);
			}
		}
		while(size>0) {
			sortByImportance(tmp);
			//System.out.println(tmp);
			int cur=tmp.get(0);
			order.add(cur);
			tmp.remove(0);
			for(TEdge edge:mapOfClasses.get(cur).getInRelation()) {
				index=edge.getFromIndex();
				depth[index]--;
				if(depth[index]==0) {
					tmp.add(index);
				}
			}
			size--;
		}
		//System.out.println("========================================================");
		/*for (int i = 0; i < order.size()-1; i++) {
		//	System.out.println(order.get(i));
			if (compare(order.get(i),order.get(i+1))==0){
				System.out.println("("+order.get(i)+","+order.get(i+1)+")");
			}
		}*/
		//System.out.println(importance[0]);
		System.out.println("====================================================");
		//System.out.println(importance[order.get(0)]);
		//System.out.println(order.get(0));
		int m=1;
		if (compare(order.get(0),order.get(1))==0){
				System.out.println("("+order.get(0)+","+order.get(1)+")");

			}

		for ( int i=0; i < order.size()-3; i+=m) {
			if(importance[order.get(i)]>=2)
			{


				 m=(int)importance[order.get(i)];
				if(i+m>=order.size()-1){
					break;
				}
				for(int j=1;j<=m;j++)
				{
					if(compare(order.get(i+j),order.get(i+j+1))==0){
						System.out.println("("+order.get(i+j)+","+order.get(i+j+1)+")");
					}
				}


			}
		}
		System.out.println("========================================================");
	}

	private void sortByImportance(List<Integer> tmp) {
		if(tmp.size()==0) {
			return ;
		}
		Collections.sort(tmp,new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				if(importance[(int)o1]>importance[(int)o2]) {
					return -1;
				}
				else if(importance[(int)o2]>importance[(int)o1]) {
					return 1;
				}
				return 0;
			}});
	}
	
	/*private void printInfo() {
		for(TClass c:setOfClasses) {
			System.out.println(c.getcId());
			for(TEdge edge:c.getOutRelation()) {
				System.out.print(edge.getToIndex()+"-");
			}
			System.out.println("="+importance[c.getcId()]);
		}
	}*/
}
