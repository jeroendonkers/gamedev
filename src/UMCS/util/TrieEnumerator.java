package UMCS.util;

/**
 * Interface for representing enumerations of positions in a Trie.
 *
 * A Trie Enumerator can only be created by a Trie.
 *
 * For example:
 * <pre>
 *   Trie t = new CompressedTrie();
 *   ...
 *   TrieEnumerator enum = t.findPrefix("co");
 *   TriePos p = enum.getFirst();
 *   while (p!=null) {
 *      System.out.println(t.getString(p)+": "+t.getContent(p).toString());
 *      p=enum.getNext();
 *   }
 * </pre>
 *
 * @author Jeroen Donkers  
 * @see UMCS.util.Trie
 * @see UMCS.util.TriePos
 **/


public interface TrieEnumerator {



   /**
   * Get the actual position of the enumerator.
   *
   * @return  The position.
   * @see UMCS.util.TriePos
   **/
  public TriePos getPos();

   /**
   * Reset the enumator to the first position.
   **/
   public void reset();

   /**
   * Return whether there is a next position available.
   **/
   public boolean hasNext();

   /**
   * Return whether there is no position in the enumeration.
   **/
   public boolean isEmpty();

   /**
   * Reset the enumeration and return the first position.
   *
   * @return  The position.
   * @see UMCS.util.TriePos
   **/
   public TriePos getFirst();

   /**
   * Get the next position in the enumeration and null if no next is found.
   *
   * @return  The position.
   * @see UMCS.util.TriePos
   **/
  public TriePos getNext();

   /**
   * Get the Trie for which this is an enumeration.
   *
   * @return  The Trie.
   * @see UMCS.util.Trie
   **/
   public Trie getTrie();

}
