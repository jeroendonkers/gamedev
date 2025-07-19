package UMCS.Games.Lib;

public class SearchResult {

   public SearchResult() {
      value=(short)0; move=Player.NOMOVE; next = null;
   }

   public SearchResult(short v, short m, SearchResult n) {
      value=v; move=m; next = n;
   }

   public short value;
   public short move; 
   public SearchResult next;
}

