package UMCS.stat;

public class Combinat {

public long over(long n, long m) {
   if (n<m) return 1;
   if (m==0 || m==n) return 1;
   long k = n-m; if (k<m) m=k;
   return over(n-1,m)+over(n-1,m-1);
}
}
