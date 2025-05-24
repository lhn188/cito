package com.zzc.CITO.Base;

public class rType {
	public static final int NONE = 0;
	public static final int IAS = 1;	//依赖、简单的聚合、相关标记为AS
	public static final int IAG = 2;	//组合，严格生命周期限制的聚合，标记为AG
	public static final int II = 3;		//继承
	public static final int Dy = 4;		//动态依赖
	
	public static int getNone() {
		return NONE;
	}
	public static int getIas() {
		return IAS;
	}
	public static int getIag() {
		return IAG;
	}
	public static int getIi() {
		return II;
	}
	public static int getDy() {
		return Dy;
	}
	
}

