package UMCS.util;


class TrieEnum implements TrieEnumerator {

  protected TrieEnum(Trie t) {
    trie=t;
    first = null;
    last = null;
    act = null;
  }

  protected TrieEnum(Trie t, TriePos f, TriePos l) {
    trie=t;
    first = f;
    last = l;
    act = f;
  }


  public TriePos getPos() { 
    return act; 
  }

  public void reset() { 
    act=first; 
  }
  public boolean hasNext() { 
     return (act != last && act != null);
  }

  public boolean isEmpty() { 
    return (first == null);
  }

   public TriePos getFirst() { 
      reset();
      return getPos();
   }

  public TriePos getNext() { 
    if (act==null || act==last) return null;
    act=trie.getNextPos(act); 
    return act;
  }

   public Trie getTrie() { 
       return trie; 
   }

private Trie trie;
private TriePos first, last, act;

}
