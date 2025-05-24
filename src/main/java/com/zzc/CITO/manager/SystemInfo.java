package com.zzc.CITO.manager;

public class SystemInfo {

	private int class_n;
	private int circle_n;
	private int ret_n;
	
	public SystemInfo(int class_n,int circle_n,int ret_n) {
		this.class_n=class_n;
		this.circle_n=circle_n;
		this.ret_n=ret_n;
	}

	public int getClass_n() {
		return class_n;
	}

	public void setClass_n(int class_n) {
		this.class_n = class_n;
	}

	public int getCircle_n() {
		return circle_n;
	}

	public void setCircle_n(int circle_n) {
		this.circle_n = circle_n;
	}

	public int getRet_n() {
		return ret_n;
	}

	public void setRet_n(int ret_n) {
		this.ret_n = ret_n;
	}

	@Override
	public String toString() {
		return "SystemInfo [class_n=" + class_n + ", circle_n=" + circle_n + ", ret_n=" + ret_n + "]";
	}
	
}
