package UMCS.util;

/**
 * Abstract Datatype for efficiently storing strings of arbitrary length.
 * For example:
 * <pre>
 *   Trie t = new CompressedTrie();
 *   t.insert("Computer");
 *   t.insert("Cooperation");
 *   t.insert("Apple");
 *   t.insert("Comsumer");
 *   TrieEnumerator te = t.findPrefix("co");
 * </pre>
 *
 * @author Jeroen Donkers  
 * @see UMCS.util.TriePos
 * @see UMCS.util.CompressedTrie
 * @see UMCS.util.TrieEnumerator
 **/

public interface Trie {

  /**
   * Insert a string into the Trie, and link a content object to this
   * string.  If the string is already present, the new content is rejected.
   * If c is null, it is replaced by the NILL content.
   * The function returns a reference to the newly added position.
   *
   * @param v The string that is to be stored.
   * @param c The object that is to be stored at the position of v.
   * @return  The position of the new string, if inserted, else null.
   * @see UMCS.util.TriePos
   **/
  public TriePos insert(String v, Object c);


  /**
   * Insert a string into the Trie (with default content NILL). 
   * If the string is already present, the new content is rejected.
   * The function returns a reference to the newly added position.
   *
   * @param v The string that is to be stored.
   * @return  The position of the new string, if inserted, else null.
   * @see UMCS.util.Trie#NILL
   * @see UMCS.util.TriePos
   **/
  public TriePos insert(String v);

  /**
   * Delete the string at position p from the Trie. 
   * If p is not a position in this trie, the request is ignored.
   *
   * @param p The position from which the string is to be deleted.
   * @see UMCS.util.TriePos
   **/
  public void delete(TriePos p);

  /**
   * Find the position of a String v within the Trie. 
   *
   * @param v The string that is sought.
   * @return The position of the string if found, else null;
   * @see UMCS.util.TriePos
   **/
  public TriePos find(String v);

  /**
   * Find the position of the first String larger than or equal to v within the Trie. 
   *
   * @param v The string that is sought.
   * @return The position of the string if found, else null;
   * @see UMCS.util.TriePos
   **/
  public TriePos findGE(String v);

  /**
   * Find the position of the last String smaller than or equal to v within the Trie. 
   *
   * @param v The string that is sought.
   * @return The position of the string if found, else null;
   * @see UMCS.util.TriePos
   **/
  public TriePos findLE(String v);


  /**
   * Find the positions of all Strings within the Trie that
   * share the largest prefix of v (which could be the empty prefix).
   *
   * @param v The prefix that is sought.
   * @return An enumeration of all positions found;
   * @see UMCS.util.TrieEnumerator
   **/
  public TrieEnumerator findPrefix(String v);

  /**
   * Find the positions of all Strings within the Trie that
   * share the prefix v. 
   *
   * @param v The prefix that is sought.
   * @return An enumeration of all positions found;
   * @see UMCS.util.TrieEnumerator
   **/
  public TrieEnumerator findExactPrefix(String v);


  /**
   * Return the string at position p in the Trie. 
   * If p is not a position in this trie, the empty string is returned.
   *
   * @param p The position from which the string is to be returned.
   * @return the string at this position, or "" if not found.
   * @see UMCS.util.TriePos
   **/
  public String getString(TriePos p);


  /**
   * Return the content of position p in the Trie. 
   * If p is not a position in this trie, null is returned.
   *
   * @param p The position from which the content is to be returned.
   * @return the content at this position, or null if not found.
   * @see UMCS.util.TriePos
   **/
  public Object getContent(TriePos p);


  /**
   * Replace the content of position p in the Trie with c. 
   * If p is not a position in this trie, the request is returned.
   * if c is null, then NILL is used instead.
   *
   * @param p The position from which the content is to be replaced.
   * @param c The new content.
   * @see UMCS.util.TriePos
   **/
  public void setContent(TriePos p, Object c);


  /**
   * Return the first position in the Trie. 
   *
   * @return the first position, or null if the Trie is empty.
   * @see UMCS.util.TriePos
   **/
  public TriePos getFirstPos(); 


  /**
   * Return the next position after p in the Trie. 
   *
   * @param p The position from which the next is to be returned.
   * @return the next position, or null if the Trie is empty or p was the last position.
   * @see UMCS.util.TriePos
   **/
  public TriePos getNextPos(TriePos p);


  /**
   * Return all positions in the Trie. 
   *
   * @return An enumeration of all positions.
   * @see UMCS.util.TrieEnumerator
   **/
  public TrieEnumerator enumerate();


  /**
   * Return the number of positions in the Trie. 
   *
   * @return the number of positions in the Trie.
   **/
  public int getCount();


  /**
   * Print the content of the Trie to System.out. 
   *
   * @see java.lang.System
   **/
  public void print();

  /**
   * Default content of a string position;
   **/
  public final static Object NILL = "NILL";
}
