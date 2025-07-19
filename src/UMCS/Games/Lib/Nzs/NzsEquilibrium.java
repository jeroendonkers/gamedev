package UMCS.Games.Lib.Nzs;
import UMCS.Games.Lib.*;


public class NzsEquilibrium  {
	
	public NzsEquilibrium(short c, short v) {
		cvalue=c;
		vvalue=v;
	}
	
	public NzsEquilibrium(NzsSearchResult r) {
		cvalue=r.cvalue;
		vvalue=r.vvalue;
	}
	
	
	public boolean equals(NzsEquilibrium e) {
		return (cvalue==e.cvalue && vvalue==e.vvalue);
	}
	
	public String toString() {
		return "(" + cvalue+","+vvalue+") ";
	}
	
	
	public short V1() {
		return (short)(cvalue+vvalue);
	}
	
	public short V2() {
		return (short)(cvalue-vvalue);
	}
	
	public short V(int sign) {
		return (short)(cvalue+sign*vvalue);
	}
	
	
	public short cvalue = 0;
	public short vvalue = 0;
}	
	