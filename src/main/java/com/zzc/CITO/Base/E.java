package com.zzc.CITO.Base;

public class E {

	private int fromIndex;
	private int toIndex;
	private int type;
	private String couple;
	private String sType;
	public E(int from,int to,int t, String string) {
		fromIndex=from;
		toIndex=to;
		type=t;
		this.couple=string;
		setsType(type);
	}
	
	public String getsType() {
		return sType;
	}

	public void setsType(int type) {
		if(type==1)
			sType="As";
		else if(type==2)
			sType="Ag";
		else if(type==3)
			sType="I";
		else
			sType="Dy";
	}

	public int getFromIndex() {
		return fromIndex;
	}
	public void setFromIndex(int fromIndex) {
		this.fromIndex = fromIndex;
	}
	public int getToIndex() {
		return toIndex;
	}
	public void setToIndex(int toIndex) {
		this.toIndex = toIndex;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCouple() {
		return couple;
	}
	public void setCouple(String couple) {
		this.couple = couple;
	}

	@Override
	public String toString() {
		return "E [fromIndex=" + fromIndex + ", toIndex=" + toIndex + ", type=" + type + ", couple=" + couple
				+ ", sType=" + sType + "]";
	}
	
	
}
