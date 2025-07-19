package UMCS.Games.Lib.Nzs;
import java.util.Vector;
import UMCS.Games.Lib.*;

public class NzsEquiSet extends Vector {
	public NzsEquilibrium getEquiAt(int i) {
		return (NzsEquilibrium)(elementAt(i));
	}
	
    public boolean contains(NzsEquilibrium e) {
    	if (size()==0) return false;
    	for (int i=0; i<size(); i++)
    	   if (getEquiAt(i).equals(e)) return true;
        return false;
    }
    
	public short minimum(int sign) {
		if (size()==0) return Player.MININF;
		short  min = getEquiAt(0).V(sign);
		for (int i=1; i<size(); i++) {
			short x = getEquiAt(i).V(sign);
			if (x<min) min=x;			
		}
		return min;   
	}
    
    
}