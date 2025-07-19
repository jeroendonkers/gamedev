package UMCS.util;

class CompTriePos implements TriePos {

  protected CompTriePos(CompressedTrie t, CompTrieNode n, Object c) {
     trie=t;
     owner=n;
     content=c;
     stlen=0;
     nextPos=null;
  }

  protected void clear() {
     trie=null;
     owner=null;
     content=null;
     nextPos=null;
  }

  protected void setContent(Object c) {
     if (c==null) return;
     content=c;
  }

   protected Object getContent() {
     return content;
  }

  public Trie getTrie() { 
     return (Trie)trie; 
  }

  protected CompressedTrie getCompressedTrie() { 
     return trie; 
  }

  protected CompTrieNode getNode() { 
     return owner; 
  }


  protected CompTriePos getNextPos() { 
    return nextPos; 
  }

  protected void setNextPos(CompTriePos n) { 
    nextPos = n; 
  }

  protected int getLen() {
    return stlen;
  }

  protected void setLen(int l) {
    stlen = l;
  }



  private CompTriePos nextPos; 
  private Object content;
  private CompTrieNode owner;
  private CompressedTrie trie;
  private int stlen;  

}
