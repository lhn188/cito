package com.zzc.CITO.Base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map.Entry;

public class TClass {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cId;
		result = prime * result + ((cName == null) ? 0 : cName.hashCode());
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
		TClass other = (TClass) obj;
		if (cId != other.cId)
			return false;
		if (cName == null) {
			if (other.cName != null)
				return false;
		} else if (!cName.equals(other.cName))
			return false;
		return true;
	}
	@JSONField
	private Integer cId;		//类编号
	private String cName;	//类名称
	private Map<String,Integer> attrDeps;	//其他类的属性依赖个数
	private Map<String,Integer> methodDeps;	//其他类的方法依赖个数
	private Set<TEdge> inRelation;			//入边集合
	private Set<TEdge> outRelation;		//出边集合
	private boolean isAbstract;

	
	public TClass(int id,String name) {
		cId=id;
		cName=name;
		attrDeps=new HashMap<String,Integer>();
		methodDeps=new HashMap<String,Integer>();
		inRelation=new HashSet<TEdge>();
		outRelation=new HashSet<TEdge>();
		isAbstract=false;
	}
	
	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public int getcId() {
		return cId;
	}


	public void setcId(int cId) {
		this.cId = cId;
	}


	public String getcName() {
		return cName;
	}


	public void setcName(String cName) {
		this.cName = cName;
	}


	public Map<String, Integer> getAttrDeps() {
		return attrDeps;
	}


	public void setAttrDeps(Map<String, Integer> attrDeps) {
		this.attrDeps = attrDeps;
	}


	public Map<String, Integer> getMethodDeps() {
		return methodDeps;
	}


	public void setMethodDeps(Map<String, Integer> methodDeps) {
		this.methodDeps = methodDeps;
	}


	public Set<TEdge> getInRelation() {
		return inRelation;
	}


	public void setInRelation(Set<TEdge> inRelation) {
		this.inRelation = inRelation;
	}


	public Set<TEdge> getOutRelation() {
		return outRelation;
	}


	public void setOutRelation(Set<TEdge> outRelation) {
		this.outRelation = outRelation;
	}
	
	//添加边
	public void addRelation(TEdge e) throws Exception{
		if(this.equals(e.getToClass())) {
			inRelation.add(e);
		}
		else if(this.equals(e.getFromClass())) {
			outRelation.add(e);
		}
		else {
			throw new Exception("relation class error!");
		}
	}

	// 设置方法最大值
	public int searchMaxMethodDep() {
		int maxMethodDep = 0;
		if (this.methodDeps != null) {
			for (Entry<String, Integer> entry : this.methodDeps.entrySet()) {
				if (maxMethodDep < entry.getValue().intValue())
					maxMethodDep = entry.getValue().intValue();
			}
		}
		return maxMethodDep;
	}
	// 设置属性最大值
	public int searchMaxAttrDep() {
		int maxAttrDep = 0;
		if (this.attrDeps != null) {
			for (Entry<String, Integer> entry : this.attrDeps.entrySet()) {
				if (maxAttrDep < entry.getValue().intValue())
					maxAttrDep = entry.getValue().intValue();
			}
		}
		return maxAttrDep;
	}

	

	/*@Override
	public String toString() {
		return "TClass [cId=" + cId + ", cName=" + cName + ", attrDeps=" + attrDeps + ", methodDeps=" + methodDeps
				+ ", inRelation=" + inRelation + ", outRelation=" + outRelation + ", isAbstract=" + isAbstract + "]";
	}*/

	@Override
	public String toString() {
		return "TClass{" +
				"cId=" + cId +
				", attrDeps=" + attrDeps +
				", methodDeps=" + methodDeps +
				'}';
	}

	public void removeRelation(TEdge e) throws Exception {
		if(this.equals(e.getToClass())) {
			inRelation.remove(e);
		}
		else if(this.equals(e.getFromClass())) {
			outRelation.remove(e);
		}
		else {
			throw new Exception("relation class error!");
		}
	}

}

