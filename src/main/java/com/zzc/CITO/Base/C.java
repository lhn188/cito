package com.zzc.CITO.Base;

public class C {

	private String cName;
	private int cId;
	private String importance;
	public C(int id, String name, String string) {
		cName=name;
		cId=id;
		this.importance=string;
	}
	public String getcName() {
		return cName;
	}
	public void setcName(String cName) {
		this.cName = cName;
	}
	public int getcId() {
		return cId;
	}
	public void setcId(int cId) {
		this.cId = cId;
	}
	
	public String getImportance() {
		return importance;
	}
	public void setImportance(String importance) {
		this.importance = importance;
	}
	@Override
	public String toString() {
		return "C [cName=" + cName + ", cId=" + cId + ", importance=" + importance + "]";
	}
	
}
