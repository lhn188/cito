package test;

public class A {
	B b;
	C c = new C();
	int aa=0;
	public A(B b) {
		this.b=b;
	}
	public void a1() {
		b.b1();
		b.b ++;
	}
	public void a2() {
		c.c1();
	}
}