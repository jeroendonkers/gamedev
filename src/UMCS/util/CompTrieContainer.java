package UMCS.util;
import java.util.Vector;

class CompTrieContainer extends Vector {

private CompTrieContainer() {owner=null; }
protected CompTrieContainer(CompTrieNode p) { owner = p;}

protected CompTrieNode getOwner() { return owner; }
protected void setOwner(CompTrieNode p) { owner = p; }

protected CompTrieNode addNode(char cc, String value) {
    add(new CompTrieNode(cc, value, this));
    return nodeAt(size()-1);
}


protected CompTrieNode addNode(char cc, String value, int i) {
    if (size()==0) {
       add(new CompTrieNode(cc, value, this));
       return nodeAt(0);
    } else {
       insertAt(i, cc, value);
       return nodeAt(i);
    }
}


protected int findNodePos(char vc) {
   if (size()==0) {
       return -1; }
   if (vc <= nodeAt(0).getChar()) {
       return 0; }
   char nc = nodeAt(size()-1).getChar(); // first char in node
   if (vc == nc) {
      return size()-1;
   }
   if (vc > nc) {
       return size();
   }
   int o,b,m;
   o = 0; b = size()-1;
   while (o<b-1) {
      m = (o+b) / 2;
      nc = nodeAt(m).getChar();
      if (vc == nc) {  
          return m;
      }
      if (vc < nc) b=m; else o=m;
   }
   return b;
}


protected CompTrieNode nodeAt(int i) {
   if (i<0 || i>=size()) return null;
   return (CompTrieNode)elementAt(i);
}

protected void insertAt(int i, char cc, String value) {
   insertElementAt(new CompTrieNode(cc, value, this), i);
}

protected void removeAt(int i) {
   removeElementAt(i);
}

protected void replaceAt(int i, char cc, String value) {
   setElementAt(new CompTrieNode(cc, value, this), i);
}

private CompTrieNode owner;

}
