package UMCS.util;

class CompTrieNode {

  protected CompTrieNode() { 
         value= null; 
         children = null;  siblings=null; content=null; 
  }

  protected CompTrieNode(char cc, String v) { 
         this(); 
         c = cc;
         value = v; 
  }

  protected CompTrieNode(char cc, String v, CompTrieContainer sib) { 
     this(cc,v); siblings= sib; 
  }


  protected char getChar() { 
      return c; 
  }

  protected String getSVal() { 
     if (isRoot()) return "";
     if (value==null) return "";
     return value; 
  }


  protected void appendValue(char cc, String s) { 
     if (value==null) value=cc+s;
     else value=value+cc+s; 
  }

  protected int nChildren() { 
      if (children==null) return 0; 
      else return children.size(); 
  }

  protected void adoptChildren(CompTrieNode n) {
     children = n.children;
     if (children!=null) children.setOwner(this);
  }


  protected CompTrieNode addChild(char cc, String v, int i) { 
     if (children==null) {
         children = new CompTrieContainer(this);
         return children.addNode(cc,v);
     }
     return children.addNode(cc,v,i);
  }


  protected CompTrieNode addChild(char cc, String v) { 
     if (children==null) children = new CompTrieContainer(this);
     return children.addNode(cc,v);
  }


  protected void removeChild(int i) { 
     if (children==null) return;
     children.removeAt(i);
  }

  protected void replaceChild(char cc, String v, int i) { 
     if (children==null) return;
     children.replaceAt(i,cc,v);
  }


  protected CompTrieNode getParent() {
     if (siblings==null) return null;
     return siblings.getOwner();
  }

  protected CompTrieNode getChild(int i) {
     if (children==null) return null;
     return children.nodeAt(i);
  }

  protected int findChild(char v) {
     if (children==null) return -1;
     return children.findNodePos(v);
  }

  protected boolean isRoot() {
    return (siblings==null);
  }


  protected boolean isPosition() { 
     return hasContent(); 
  }

  protected boolean hasContent() {
    return (content!=null);
  }

  protected void setContent(CompressedTrie t, Object c) {
     if (c==null) return;
     content=new CompTriePos(t,this,c);
  }

   protected void clearContent() {
     if (content!=null) content.clear();
     content=null;
  }

  protected Object getContent() {
     if (content==null) return null;
     return content.getContent();
  }

  protected void print(String pre) { 
    System.out.print( pre+c);
    if (value!=null)    System.out.print(" <"+value+">");
    if (content!=null)   System.out.print( " ["+getContent().toString()+"] ("+getLen()+")");
    if (getNextPos()!=null)  System.out.print( " >> "+getNextPos().getNode().getString()); 
    System.out.println( );

    if (children!=null) 
      for (int i=0; i<nChildren(); i++) 
         getChild(i).print(pre+" ");
  }

  protected String getString() {
      CompTrieNode n = this; 
      StringBuffer s = new StringBuffer(getLen());
      while (!n.isRoot()) {
         if (n.value!=null) s.insert(0,n.value);
         s.insert(0,n.c);
         n = n.getParent();
      }
      return s.toString();
  }

  protected CompTriePos getPos() { 
     return content;
  }

  protected CompTriePos getNextPos() { 
    if (content==null) return null;
    return content.getNextPos(); 
  }

  protected void setNextPos(CompTriePos n) { 
    if (content==null) return;
    content.setNextPos(n); 
  }

  protected int getLen() {
    if (content==null) return 0;
    return content.getLen();
  }

  protected void setLen(int l) {
    if (content==null) return;
    content.setLen(l);
  }

  private String value;
  private char c;
  private CompTrieContainer children, siblings; 
  private CompTriePos content; 
}
