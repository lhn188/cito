package com.zzc.CITO.Base;

import com.alibaba.fastjson.annotation.JSONField;

import soot.SootClass;
import soot.SootMethod;

public class TEdge {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromClass == null) ? 0 : fromClass.hashCode());
		result = prime * result + type;
		result = prime * result + ((toClass == null) ? 0 : toClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TEdge other = (TEdge) obj;
		if (fromClass == null) {
			if (other.fromClass != null)
				return false;
		} else if (!fromClass.equals(other.fromClass))
			return false;
		if (type != other.type)
			return false;
		if (toClass == null) {
			if (other.toClass != null)
				return false;
		} else if (!toClass.equals(other.toClass))
			return false;
		return true;
	}

	private TClass fromClass;	//入边类
	private TClass toClass;		//出边类
	@JSONField
	private int type;			//边类型
	@JSONField
	public int fromIndex;
	@JSONField
	public int toIndex;
	
	public TEdge(TClass from, TClass to, int rt) {
		fromClass = from;
		toClass = to;
		type = rt;
		
		fromIndex=from.getcId();
		toIndex=to.getcId();
	}
	
	public TEdge getCause() {
		for(TEdge eFromTo : toClass.getOutRelation()) {
			if(eFromTo.getRtype() == rType.II) {//若为继承
				for(TEdge eFromFrom : fromClass.getOutRelation()) {
					if(eFromFrom.getToClass().equals(eFromTo.getToClass())) {
						return eFromFrom;
					}
				}
			}
		}
		return null;
	}

	public void setMethodSet(SootClass sClass) {
		for(SootMethod sm:sClass.getMethods()) {
			
		}
	}
	public TClass getFromClass() {
		return fromClass;
	}

	public void setFromClass(TClass fromClass) {
		this.fromClass = fromClass;
	}

	public TClass getToClass() {
		return toClass;
	}

	public void setToClass(TClass toClass) {
		this.toClass = toClass;
	}

	public int getRtype() {
		return type;
	}

	public void setRtype(int type) {
		this.type = type;
	}

	public int getFromIndex() {
		return fromClass.getcId();
	}
	
	public int getToIndex() {
		return toClass.getcId();
	}

	@Override
	public String toString() {
		return "TEdge [type=" + type + ", fromIndex=" + fromIndex + ", toIndex=" + toIndex + "]";
	}

	
}

