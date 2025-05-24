package test;

public class C {
	B b = new B();
    A a = new A(b);
	E e = new D();
    public void c1() {
		b.b1();
	    a.a1();
    }
	public void c2() {
		e.e1();
	}
	public void c3() {
		a.aa++;
		e.e++;
		b.b++;
	}
}
