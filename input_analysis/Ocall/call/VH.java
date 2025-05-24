package call;

public class VH {
	VM M=new VM();
	//VE E=new VF();
	VN N=new VN();
	VI I=new VI();
	VK K=new VK();
	VL L=new VL();
	
	int h;
	void MethodH1() {
		//E.e++;
	}
	void MethodH2() {
		I.i++;
		N.n=I.i+h;
	}
	void MethodH3() {
		K.k=L.l+M.m;
	}
}
