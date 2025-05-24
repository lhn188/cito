package com.zzc.test;

public class G {

	A a=new A();
	H h=new H();
	D d=new D(a);
	E e=new F();
	
	public void g1() {
		h.h1();
		d.dd++;
		a.a1();
	}
	public void g2() {
		e.e1();
		d.d1();
		a.a++;
	}
	public void g3() {
		e.e++;
		h.h+=7;
	}
}
