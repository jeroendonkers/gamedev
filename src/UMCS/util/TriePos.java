package UMCS.util;

/**
 * Abstract Datatype for representing positions in a Trie.
 * For example:
 * <pre>
 *   Trie t = new CompressedTrie();
 *   t.insert("Computer");
 *   t.insert("Cooperation");
 *   t.insert("Apple");
 *   t.insert("Comsumer");
 *   TriePos p = t.find("Consumer");
 *   p = t.getNext(p);
 *   System.out.println(t.getString(p)); 
 * </pre>
 *
 * @author Jeroen Donkers  
 * @see UMCS.util.Trie
 **/


public interface TriePos {

  /**
   * Return the Trie to which this position belongs.
   *
   * @return the Trie;
   * @see UMCS.util.Trie
   **/
  public Trie getTrie();

}
