package UMCS.Games.Lib;

public class NzsSearchResult extends SearchResult {
     NzsSearchResult() { }
     NzsSearchResult(short c, short v, short m, SearchResult n) { 
        super((short)(c+v),m,n); 
        cvalue = c;
        vvalue = v;
     }
     public short cvalue = 0;    // common value
     public short vvalue = 0;    // separate value

}

