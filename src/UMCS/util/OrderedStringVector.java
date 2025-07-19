package UMCS.util;

public class OrderedStringVector extends java.util.Vector {

   public OrderedStringVector() { }

   public String stringAt(int i) { return (String)(elementAt(i)); }

   public int findString(String s) {
       if (size()==0) return -1;
       int o=0, b=size()-1, m;
       int co = s.compareTo(stringAt(o));
       if (co<0) return -1;   if (co==0) return o;
       int cb = s.compareTo(stringAt(b));
       if (cb>0) return -b-2; if (cb==0) return b;
       
       do {
           m = (o+b)/2;
           int cm = s.compareTo(stringAt(m));  
           if (cm==0) return m;
           if (cm<0) b=m; else o=m; 
       } while (b>o+1);
       return -b-1;
   }

   public void insert(String s) {
       int pos = findString(s);
       if (pos>=0) return;
       pos=-pos-1;
       if (pos>=size()) add(s); else insertElementAt(s,pos);
   }

}
