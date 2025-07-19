package UMCS.util;


/**
 * Compressed implementation of the Abstract Datatype Trie.
 * Strings are stored in lexicographical order so all enumerations are sorted.
 * This implementation keeps a singly linked list of all positions.
 *
 * @author Jeroen Donkers  
 **/
public class CompressedTrie implements Trie {


/**
 *  Create an empty Compressed Trie.
 * For example:
 * <pre>
 *   Trie t = new CompressedTrie();
 *   t.insert("Computer");
 *   t.insert("Cooperation");
 *   t.insert("Apple");
 *   t.insert("Comsumer");
 *   TrieEnumerator te = t.findPrefix("co");
 * </pre>

 **/
public CompressedTrie() { 
   clear();
}


/**
 *  Clear a Compressed Trie.
 * For example:
 * <pre>
 *   Trie t = new CompressedTrie();
 *   t.insert("Computer");
 *   t.insert("Cooperation");
 *   t.insert("Apple");
 *   t.insert("Comsumer");
 *   t.clear();
 * </pre>

 **/
public void clear() { 
   root= new CompTrieNode(); 
   root.setLen(0); 
   firstPos = null;
   count = 0;
   nodecount=1;
   stringcount=0;
}


// Insert a string (v) into the Trie, and link a content object (c) to this
// string.  If the string is already present, the new content is rejected.
// If c is null, it is replaced by the NILL content.
// The function returns a reference to the newly added (or changed) node.

public TriePos insert(String v, Object c) {

  count++;
  storecount+=v.length();

  if (c==null) c=NILL;
  if (v.length()==0) { // empty string
     if (!root.hasContent()) {
         root.setContent(this,c);
         chainPosition(root);
     }
     else count--;

     return (TriePos)root.getPos();
  }

  int vpos=0;
  CompTrieNode n = root, chld;

  do {
     int pos = n.findChild(v.charAt(vpos));

     if (pos<0 || pos>=n.nChildren()) { 
        chld=addChild(n,v,vpos);
        if (chld!=null) {
            chld.setContent(this,c);
            chld.setLen(v.length());
            chainPosition(chld);
            nodecount++; stringcount+=v.length()-vpos-1;
       }
        return (TriePos)chld.getPos(); 
     }

     char wc = n.getChild(pos).getChar();
     String w = n.getChild(pos).getSVal();
     if (v.charAt(vpos)<wc) {
           chld=addChild(n,v,vpos,pos);
           if (chld!=null) {
               chld.setContent(this,c);
               chld.setLen(v.length());
               chainPosition(chld);
               nodecount++; stringcount+=v.length()-vpos-1;
           }
           return (TriePos)chld.getPos();
     }
 
     int i = 0;
     while (i<w.length() && i+vpos+1<v.length() && v.charAt(vpos+i+1)==w.charAt(i) ) i++;
     if (i+vpos+1 >= v.length() && i>=w.length()) {
         chld = n.getChild(pos);
         if (!chld.hasContent()) {
            chld.setContent(this,c);
            chld.setLen(v.length());
            chainPosition(chld);
         }  else {
             count--; // String is already present!
             storecount-=v.length();
         }
         return (TriePos)chld.getPos();
     }

     vpos += (i+1);
     if (i==w.length()) {
        n = n.getChild(pos);  // loop further
     } else {
       CompTrieNode oldChild = n.getChild(pos);
       CompTrieNode vchld, wchld;
       boolean vfirst=true;

       if (oldChild.isPosition()) unchainPosition(oldChild);
       Object occ = oldChild.getContent();
       oldChild.clearContent();

       n.replaceChild(wc,w.substring(0,i),pos); 
       
       n = n.getChild(pos);
       if (vpos>=v.length()) {
          vchld = n;
          vchld.setContent(this,c);
          chainPosition(vchld);

          wchld = addChild(n,w,i);
          wchld.adoptChildren(oldChild);
          wchld.setContent(this,occ);
          chainPosition(wchld);
          nodecount++;  stringcount--; // we lose one character!

       } else {
         if (v.charAt(vpos)<w.charAt(i)) {

           vchld = addChild(n,v,vpos);
           vchld.setContent(this,c);
           chainPosition(vchld);

           wchld = addChild(n,w,i);
           wchld.adoptChildren(oldChild);
           wchld.setContent(this,occ);
           chainPosition(wchld);
           nodecount+=2;  stringcount+=v.length()-vpos-2;

         } else {

           wchld = addChild(n,w,i);
           wchld.adoptChildren(oldChild);
           wchld.setContent(this,occ);
           chainPosition(wchld);


           vchld = addChild(n,v,vpos);
           vchld.setContent(this,c);
           chainPosition(vchld);
           nodecount+=2;  stringcount+=v.length()-vpos-2;
         }
       }

       wchld.setLen(oldChild.getLen());
       vchld.setLen(v.length());
       return (TriePos)vchld.getPos();
     }
  } while (n!=null);
  return null;
}




// add a child to n, with value s from position pos
private CompTrieNode addChild(CompTrieNode n, String s, int i) {
    if (i<s.length()-1)
       return n.addChild(s.charAt(i),s.substring(i+1));
    else
       return n.addChild(s.charAt(i), null);
}

private CompTrieNode addChild(CompTrieNode n, String s, int i, int pos) {
    if (i<s.length()-1)
       return n.addChild(s.charAt(i),s.substring(i+1),pos);
    else
       return n.addChild(s.charAt(i), null,pos);
}


// insert a string without a content object
// default NILL content is used
public TriePos insert(String v) {
   return insert(v,NILL);
}



// delete a string at position p
public void delete(TriePos p) {
   if (p == null) return;
   if (p.getTrie() != this) return;
   CompTrieNode n = ((CompTriePos)p).getNode();

   count--;   
   unchainPosition(n);
   n.clearContent();

   // more children

   if (n.nChildren()>1) return;

   // 1 child
   
   if (n.nChildren()==1) {      
      CompTrieNode m = n.getChild(0);
      if (m.isPosition()) unchainPosition(m);
      Object mc = m.getContent(); 
      m.clearContent();

      n.appendValue(m.getChar(),m.getSVal());
      n.adoptChildren(m);
      if (mc!=null) {
         n.setContent(this,mc);
         n.setLen(m.getLen());
         chainPosition(n);
      }
      return; 
   }       

   // no children

   if (n.isRoot()) {
       n.clearContent();
       return;
   }

   CompTrieNode a = n.getParent();
   int i = a.findChild(n.getChar());
   if (i<0) {count++; return; }
   a.removeChild(i);

   if (a.nChildren()>1) return;
   if (a.isPosition()) return;
   if (a.isRoot()) return;
  
   CompTrieNode c = a.getChild(0);
   a.appendValue(c.getChar(),c.getSVal());
   if (c.isPosition()) {
       unchainPosition(c);
       a.setContent(this,c.getContent());
       a.setLen(c.getLen());
   }
   a.adoptChildren(c);
   if (c.isPosition()) chainPosition(a);
   c.clearContent();

}


private void chainPosition(CompTrieNode n) {
   if (n==null) return;
   if (!n.isPosition()) return;
   if (n.isRoot()) {
      root.setNextPos(firstPos);
      firstPos = root.getPos();
      return;
   }
   CompTriePos m = getFirstPositionBefore(n);
   if (m==null) {
      n.setNextPos(firstPos);
      firstPos=n.getPos();
   } else {
     n.setNextPos(m.getNextPos());
     m.setNextPos(n.getPos());
   }
}


private void unchainPosition(CompTrieNode n) {
   if (n==null) return;
   if (!n.isPosition()) return;
   if (n.isRoot()) {
      firstPos = root.getNextPos();
      root.setNextPos(null);
      return;
   }
   CompTriePos m = getFirstPositionBefore(n);
   if (m==null) {
     firstPos=n.getNextPos();
     n.setNextPos(null);
   } else {
     m.setNextPos(n.getNextPos());
     n.setNextPos(null);
   }
}


private CompTriePos getFirstPositionBelow(CompTrieNode n) {
  if (n.isPosition()) return n.getPos();
  while (n.nChildren()>0) {
     n = n.getChild(0);
     if (n.isPosition()) return n.getPos();
  }
  return null;
}

private CompTriePos getLastPositionBelow(CompTrieNode n) {
  while (n.nChildren()>0) 
     n = n.getChild(n.nChildren()-1);
  if (n.isPosition()) return n.getPos(); 
  else return null;
}


private CompTriePos getFirstPositionBefore(CompTrieNode n) {
  if (n.isRoot()) return null;
  do {
     char c = n.getChar();
     n = n.getParent();
     int i = n.findChild(c);
     if (i>0)  return getLastPositionBelow(n.getChild(i-1));
     if (n.isPosition()) return n.getPos();
  } while (!n.isRoot());
  return null;
}

// find the position of a stored string
// returns null if not found.

public TriePos find(String v) {
  if (v.length()==0) return (TriePos)root.getPos();
  int vpos=0;
  CompTrieNode n = root;
  do {
     int pos = n.findChild(v.charAt(vpos));
     if (pos<0) return null; 
     if (pos>=n.nChildren()) return null; 
     char wc = n.getChild(pos).getChar();
     String w = n.getChild(pos).getSVal();
     int i = w.length();
     if (vpos+i+1 > v.length()) return null;
     if (wc!=v.charAt(vpos)) return null;
     if (i>0 && !w.equals(v.substring(vpos+1,vpos+1+i))) return null;
     n = n.getChild(pos);  
     vpos += (i+1);
     if (vpos==v.length()) {
       if (n.isPosition()) return (TriePos)n.getPos(); else return null;
     }
  } while (n!=null);
  return null;
}


// find the position of the first stored string larger than or equal to v
// returns null if not found.

public TriePos findGE(String v) {
  if (v.length()==0) return (TriePos)getFirstPositionBelow(root);
  int vpos=0;
  CompTrieNode n = root;
  do {
     int pos = n.findChild(v.charAt(vpos));
     if (pos<0) return (TriePos)getFirstPositionBelow(n); 
     if (pos>=n.nChildren()) 
          return  (TriePos)(getLastPositionBelow(n).getNextPos());

     CompTrieNode chld = n.getChild(pos);
     char wc = chld.getChar();
     if (wc!=v.charAt(vpos))
         return (TriePos)getFirstPositionBelow(chld);

     String w = chld.getSVal();
     int i = w.length();
     if (vpos+i+1 > v.length()) i = v.length()-vpos-1; 

     if (i>0) {
         int comp= w.compareTo(v.substring(vpos+1,vpos+1+i));
         if (comp<0) return (TriePos)(getLastPositionBelow(chld).getNextPos()); 
         if (comp>0) return (TriePos)(getFirstPositionBelow(chld)); 
     }

     n = n.getChild(pos);  

     vpos += (i+1);
     if (vpos==v.length()) {
       if (n.isPosition()) return (TriePos)n.getPos(); 
       else return (TriePos)getFirstPositionBelow(n);
     }
  } while (n!=null);
  return null;
}


// find the position of the last stored string smaller than or equal to v
// returns null if not found.

public TriePos findLE(String v) {
  if (v.length()==0) {
      if (root.isPosition()) return (TriePos)root.getPos();
  }
  int vpos=0;
  CompTrieNode n = root;
  do {
     int pos = n.findChild(v.charAt(vpos));
     if (pos<0) return (TriePos)getFirstPositionBefore(n); 
     if (pos>=n.nChildren()) 
          return  (TriePos)(getLastPositionBelow(n));

     CompTrieNode chld = n.getChild(pos);
     char wc = chld.getChar();
     if (wc!=v.charAt(vpos))
         return (TriePos)getFirstPositionBefore(chld);

     String w = chld.getSVal();
     int i = w.length();
     if (vpos+i+1 > v.length()) i = v.length()-vpos-1; 

     if (i>0) {
         int comp= w.compareTo(v.substring(vpos+1,vpos+1+i));
         if (comp<0) return (TriePos)(getLastPositionBelow(chld)); 
         if (comp>0) return (TriePos)(getFirstPositionBefore(chld)); 
     }

     n = n.getChild(pos);  

     vpos += (i+1);
     if (vpos==v.length()) {
       if (n.isPosition()) return (TriePos)n.getPos(); 
       else return (TriePos)getFirstPositionBefore(n);
     }
  } while (n!=null);
  return null;
}

public TrieEnumerator findExactPrefix(String v) {
   return findPrefix(v,true);
}

public TrieEnumerator findPrefix(String v) {
   return findPrefix(v,false);
}

private TrieEnumerator findPrefix(String v, boolean exact) {
  if (v.length()==0) return enumerate();

  int vpos=0;
  CompTrieNode n = root;
  do {
     boolean found = false;

     int pos = n.findChild(v.charAt(vpos));
     if (pos<0 || pos>=n.nChildren()) {
         if (exact) return (TrieEnumerator)(new TrieEnum(this));
         found=true;
     }
 
     if (!found) {

       char wc = n.getChild(pos).getChar();
       String w = n.getChild(pos).getSVal();

       int i = w.length();
       if (vpos+i+1 > v.length()) i = v.length()-vpos-1;

       if (wc!=v.charAt(vpos)) {
           if (exact) return (TrieEnumerator)(new TrieEnum(this));
           found=true;
       } else {
         int j=0;
         while (j<i && w.charAt(j) == v.charAt(vpos+j+1)) j++;
         if (j<i) {
            if (exact) return (TrieEnumerator)(new TrieEnum(this));
            found=true;
         }
         n = n.getChild(pos);  
         vpos += i+1;

         if (!found && vpos==v.length()) found=true;
       }

     }

     if (found) {
           TriePos f = (TriePos)getFirstPositionBelow(n);
           TriePos l = (TriePos)getLastPositionBelow(n);
           return (TrieEnumerator)(new TrieEnum(this,f,l));
     }

  } while (n!=null);
  return (TrieEnumerator)(new TrieEnum(this));
}


public TriePos getFirstPos() { 
   return (TriePos)firstPos; 
}

public TriePos getNextPos(TriePos p) {
   if (p == null) return null;
   if (p.getTrie() != this) return null;
   return (TriePos)((CompTriePos)p).getNextPos();
}


public TrieEnumerator enumerate() {
  TriePos lastPos = (TriePos)getLastPositionBelow(root);
  return (TrieEnumerator)(new TrieEnum(this,firstPos,lastPos));
};

public String getString(TriePos p) {
   if (p == null) return "";
   if (p.getTrie() != this) return "";
   return ((CompTriePos)p).getNode().getString();
}


public Object getContent(TriePos p) {
   if (p == null) return null;
   if (p.getTrie() != this) return null;
   return ((CompTriePos)p).getContent();
}


public void setContent(TriePos p, Object c) {
   if (p == null) return;
   if (p.getTrie() != this) return;
   ((CompTriePos)p).setContent(c);
}

public void print() { 
   System.out.println("Trie contains "+ count + " strings.");
   root.print(""); 
}


public int getCount() { return count; }

public long getNodeCount() { return nodecount; }
public long getStringCount() { return stringcount; }
public long getStoreCount() { return storecount; }

private CompTrieNode root;
private CompTriePos firstPos;
private int count; 
private long nodecount;
private long stringcount;
private long storecount;
}
