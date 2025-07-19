package UMCS.stat;
public class Histogram extends Sample {

public Histogram(int bins, double binsize, double base) {
   this.base=base;
   this.bins=bins;
   this.binsize=binsize;
   bin = new int[bins];
   cml = null;
   maxbin=-1;
}

public Histogram(int bins, double binsize) {
   this(bins,binsize,0);
}

public void add(double X) {
    super.add(X);
    int j = (int)Math.floor( (X-base) / binsize); 
    if (j>=0 && j<bins) {
        bin[j]++;
        if (maxbin==-1) maxbin=j;      
        else { if (bin[j]>maxbin) maxbin=j; }
    }
    cml = null;  // cummulative is not valid anymore
}

public void reset() {
   super.reset();
   bin = new int[bins];
   cml = null;
   maxbin=-1;
}

public int getBin(int i) {
   if (i>=0 && i<bins) return bin[i];
   return 0;
}

public int[] getBins() {
   return bin;
}

public double getBinsize() {
   return binsize;
}

public double getBase() {
   return base;
}

public int getNumBins() {
   return bins;
}

public double[] getCummulative() {
  cml = new double[bins];
  double sum=0;
  int n = getSampleSize();
  for (int j=0; j<bins; j++) {
      sum+=bin[j];   
      cml[j]=sum/n;
  } 
  return cml;
}

public double getPercentile(double p) {
   if (cml==null) getCummulative();
   for (int j=0; j<bins; j++)     
      if (cml[j]>=p) return base + j*binsize;
   return base + bins*binsize;
}

public double getMedian() {
   return getPercentile(0.5);
}

public double getLowerQuartile() {
   return getPercentile(0.25);
}

public double getUpperQuartile() {
   return getPercentile(0.75);
}

public double getInterQuartileRange() {
   return getUpperQuartile() - getLowerQuartile();
}

public double getMode() {
   return base + maxbin*binsize;
}

public String summary() {
  String s="Sample Summary\n";
  s=s+"====================================\n";
  s=s+"Sample size          "+getSampleSize() + "\n";
  s=s+"Mean                 "+getMean() + "\n";
  s=s+"Median               "+getMedian() + "\n";
  s=s+"Mode                 "+getMode() + "\n";
  s=s+"Variance             "+getVariance() + "\n";
  s=s+"Std. dev.            "+getStandardDeviation() + "\n";
  s=s+"Minimum              "+getMin() + "\n";
  s=s+"Maximum              "+getMax() + "\n";
  s=s+"Range                "+getRange() + "\n";
  s=s+"Lower Quartile       "+getLowerQuartile() + "\n";
  s=s+"Upper Quartile       "+getUpperQuartile() + "\n";
  s=s+"interquartile range  "+getInterQuartileRange() + "\n";
  s=s+"Coeff. of variation  "+getCoefficientOfVariation() + "\n";
  s=s+"Sum                  "+getSum() + "\n";
  return s;
}


public String boxplot() {
  String s = getMedian()+" "+getLowerQuartile() +" "+getUpperQuartile() + " " +
      getPercentile(0.05) +" "+getPercentile(0.95);
  return s;
}

private double base=0;
private int bins = 100;
private double binsize = 1;
private int[] bin;
private double[] cml;
private int maxbin;

}
