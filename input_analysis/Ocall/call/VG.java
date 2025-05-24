package call;

public class VG {

	VD D=new VD();
	VF F=new VF();
	VE E=F;
	VH H=new VH();
	VI I=new VI();
	VJ J=new VJ();

	int g=D.d;
	public void MethodG1() {
		E.MethodE();
	}
	public void MethodG2() {
		I.i++;
		J.j=I.i+g;
	}
	public void MethodG3() {
		H.MethodH2();
	}
	public void MethodG4() {
		I.MethodI();
	}
	public void MethodG5() {
		E.MethodE();
	}
}
