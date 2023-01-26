package edu.uwm.cs351.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import junit.framework.TestCase;

//Estelle Brady
//CS 351-401
//collaborated with Julian Moreno, Miguel Garcia, Marvin Ista and worked with tutor Matt

public class TreeMap<K,V>  extends AbstractMap<K,V> {

	// Here is the data structure to use.

	private static class Node<K,V> extends DefaultEntry<K,V> {
		Node<K,V> left, right;
		Node<K,V> parent;
		Node(K k, V v) {
			super(k,v);
			parent = left = right = null;
		}
	}

	private Comparator<K> comparator;
	private Node<K,V> dummy;
	private int numItems = 0;
	private int version = 0;


	/// Invariant checks:

	private static Consumer<String> reporter = (s) -> { System.err.println("Invariant error: " + s); };

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	/**
	 * Return whether nodes in the subtree rooted at the given node have correct parent
	 * and have keys that are never null and are correctly sorted and are all in the range 
	 * between the lower and upper (both exclusive).
	 * If either bound is null, then that means that there is no limit at this side.
	 * The first problem is found will be reported.
	 * @param node root of subtree to examine
	 * @param p parent of subtree to examine
	 * @param lower value that all nodes must be greater than.  If null, then
	 * there is no lower bound.
	 * @param upper value that all nodes must be less than. If null,
	 * then there is no upper bound.
	 * @return whether the subtree is fine.  If false is 
	 * returned, there is a problem, which has already been reported.
	 */
	private boolean checkInRange(Node<K,V> node, Node<K, V> p, K lower, K upper) {

		if(node == null)
			return true;

		if(node.getKey() == null)
			return report("can't be null");

		if(node.parent != p)
			return report("nope");

		if(upper != null)
			if(comparator.compare(node.getKey(), upper) >= 0)
				return report("low cannot come before high");

		if(lower != null)
			if(comparator.compare(node.getKey(), lower) < 0)
				return report("high cannot come before low");

		//we want to do a while loop or recursion for the key


		return checkInRange(node.left, node, lower, node.key) && checkInRange(node.right, node, node.key, upper);
	}
	
	//implemented homework 9 solution - doRemove
	private Node<K,V> doRemove(Node<K,V> r, Node<K,V>b) 
	{
		if(r == null)    return null;

		//if is is on the left side of the tree
		if(comparator.compare(r.key, b.key) > 0) { 
			r.left = doRemove(r.left, b);
			if(r.left!=null)
				r.left.parent = r;
		}
		//if it is on the right side
		else if(comparator.compare(r.key, b.key) < 0) {
			r.right = doRemove(r.right, b);
			if(r.right!=null)
				r.right.parent = r;
			//if it is then equal
		}else {
			if(r.right == null && r.left != null) {
				r.left.parent = r;
				r = r.left;	return r;
			}else if(r.left == null) {
				r = r.right;return r;
			}
			else {
				Node<K,V> temp = firstInTree(r.right);

				temp.right = doRemove(r.right,firstInTree(r.right));
				if(temp.right != null) 
					temp.right.parent = temp;

				temp.left = r.left;

				if(temp.left != null) {
					temp.left.parent = temp;
					return temp;
				}
			}
		}
		return r;
	}

	@Override //implementation
	public V remove(Object o) 
	{
		assert wellFormed():("Invariant error at start of V remove");

		//temporary variable to return
		V temp = get(o);

		if(findKey(o) == null){
			return null;
		}else {
			dummy.left = doRemove(dummy.left, findKey(o));

			if(dummy.left!=null)
				dummy.left.parent=dummy;

			numItems--;
			version++;

			assert wellFormed() : "invariant broken at end of V remove()";
			return temp;
		}
	}

	/**
	 * Return the first node in a non-empty subtree.
	 * It doesn't examine the data in teh nodes; 
	 * it just uses the structure.
	 * @param r subtree, must not be null
	 * @return first node in the subtree
	 */
	private Node<K,V> firstInTree(Node<K,V> r) {
		if(r!= null) {
			while(r.left!=null) {
				r = r.left;
			}
			//null means we are at the end of the loop
			if(r.left == null)
				return r;
		}
		return null; // TODO: non-recursive is fine
	}

	/**
	 * Return the number of nodes in a binary tree.
	 * @param r binary (search) tree, may be null but must not have cycles
	 * @return number of nodes in this tree
	 */
	private int countNodes(Node<K,V> r) {
		if (r == null) return 0;
		return 1 + countNodes(r.left) + countNodes(r.right);
	}

	/**
	 * Check the invariant, printing a message if not satisfied.
	 * @return whether invariant is correct
	 */
	private boolean wellFormed() {
		// TODO:
		// 1. check that comparator is not null
		if(comparator == null)
			return report("comparator cannot be null");
		// 2. check that dummy is not null
		if(dummy == null)
			return report("Dummy cannot be null");
		// 3. check that dummy's key, right subtree and parent are null
		if(dummy.key != null || dummy.right != null || dummy.parent != null)
			return report("dummy's key, right subtree or parent are not null");
		// 4. check that all (non-dummy) nodes are in range
		if(checkInRange(dummy.left, dummy, null, null) == false)
			return false;
		// 5. check that all nodes have correct parents
		// 6. check that number of items matches number of (non-dummy) nodes
		// "checkInRange" will help with 4,5
		if(numItems != countNodes(dummy.left))
			return report("The number of nodes must match numItems");

		return true;
	}

	/// constructors

	private TreeMap(boolean ignored) { } // do not change this.

	/**
	 * Initialize as empty
	 **/   
	public TreeMap() {
		this(null);
		assert wellFormed() : "invariant broken after constructor()";
	}

	/**
	 * creates a comparator that can be used
	 * sets a new dummy node
	 * @param c 
	 */
	@SuppressWarnings("unchecked")
	public TreeMap(Comparator<K> c) {
		if(c == null) {
			Comparator<K> comp2 = (s1,s2) -> ((Comparable<K>) s1).compareTo(s2);
			comparator = comp2;
		}else {
			comparator = c;
		}
		dummy = new Node<K,V>(null,null);
		numItems = 0;

		assert wellFormed() : "invariant broken after constructor(Comparator)";
	}

	@SuppressWarnings("unchecked")
	private K asKey(Object x) {
		if (dummy.left == null || x == null) return null;
		try {
			comparator.compare(dummy.left.key,(K)x);
			comparator.compare((K)x,dummy.left.key);
			return (K)x;
		} catch (ClassCastException ex) {
			return null;
		}
	}

	private Node<K, V> findKeyHelper(Node<K,V> r, Object o){

		K oAsKey = asKey(o);

		//return null if any cases are null
		if(r == null || oAsKey == null)
			return null;

		//go left if argument comes before
		if(comparator.compare(r.key, oAsKey) > 0) {
			return findKeyHelper(r.left, oAsKey);
		}

		//go right if argument comes after
		if(comparator.compare(r.key, oAsKey) < 0) {
			return findKeyHelper(r.right, oAsKey);
		}
		return r;
	}

	/**
	 * Find the node for a given key.  Return null if the key isn't present
	 * in the tree.  This helper method assumes that the tree is well formed,
	 * but doesn't check that.
	 * @param o object treated as a key.
	 * @return node whose data is equal to o, 
	 * or null if no nodes in the tree have this property.
	 */
	private Node<K, V> findKey(Object o){
		assert wellFormed() : "invariant broken at beginning of findKey";
		Node<K,V> r = findKeyHelper(dummy.left, o);
		assert wellFormed() : "invariant broken at end of findKey";
		return r;
	}

	@Override // implementation
	public int size() {
		assert wellFormed() : "invariant broken at the beginning of TreeMap size";
		return numItems;
	}

	// TODO: many methods to override here:
	// size, containsKey(Object), get(Object), clear(), put(K, V), remove(Object)
	// make sure to use @Override and assert wellformedness
	// plus any private helper methods.
	// Our solution has getNode(Object)
	// Increase version if data structure if modified.
	@Override //efficiency
	public boolean containsKey(Object o) {
		assert wellFormed() : "invariant broken at start of containsKey";	
		if(findKey(o) == null) {
			return false;
		}
		assert wellFormed() : "invariant broken at end of containsKey";	
		return true;	
	}

	@Override //efficiency
	public V get(Object key) {
		assert wellFormed() : "invariant broken at beginning of get";	
		Node<K,V> a= findKey(asKey(key));
		if(a == null)
			return null;
		assert wellFormed() : "invariant broken at end of get";	
		return a.getValue();
	}

	/**
	 * Add a new element to this book, in order.  If an equal appointment is already
	 * in the book, it is inserted after the last of these. 
	 * The current element (if any) is not affected.
	 * @param element
	 *   the new element that is being added, must not be null
	 * @param Node r 
	 * 	the node that we are using
	 * @postcondition
	 *   A new copy of the element has been added to this book. The current
	 *   element (whether or not is exists) is not changed.l
	 *  @return r
	 **/

	//used my insertHelper solution from homework 9
	private Node<K,V> putHelper(Node<K,V> r, K a, V b) {
		++version;
		if(r == null) {
			r= new Node<K,V>(a,b);
			r.parent = dummy;
			return r;
		}
		if(comparator.compare(a, r.key) < 0) {
			r.left = putHelper(r.left, a, b);
			r.left.parent= r;
		}else {
			r.right = putHelper(r.right, a,b);
			r.right.parent = r;
		}
		return r;
	}

	//put(k,v) Add an entry (if no entry for key) or modify the existing entry. Throws an exception
	//if key is null. Return the previous value associated with this key, or null.
	@Override // implementation
	public V put(K key, V value) {
		assert wellFormed() : "invariant broken at beginning of put";
		if(key == null)
			throw new NullPointerException("key can't be null");
		if(!containsKey(key)) {
			dummy.left = putHelper(dummy.left, key, value);
			numItems++;
			assert wellFormed() : "invariant broken at end of put";
			return null;
		}else {
			Node<K,V> a = findKey(key);
			V temp = a.value;
			a.value = value;
			assert wellFormed() : "invariant broken at end of put";
			return temp;
		}
	}

	@Override //efficiency
	public void clear() {
		assert wellFormed() : "invariant broken at beginning of clear";
		if(numItems ==0)return;
		numItems = 0;
		dummy.left =null;
		++version;
		assert wellFormed() : "invariant broken at end of clear";
	}

	private volatile Set<Entry<K,V>> entrySet;

	@Override //required
	public Set<Entry<K, V>> entrySet() {
		assert wellFormed() : "invariant broken at beginning of entrySet";
		if (entrySet == null) {
			entrySet = new EntrySet();
		}
		return entrySet;
	}

	/**
	 * The set for this map, backed by the map.
	 * By "backed: we mean that this set doesn't have its own data structure:
	 * it uses the data structure of the map.
	 */
	private class EntrySet extends AbstractSet<Entry<K,V>> {
		// Do NOT add any fields! 

		@Override //required
		public int size() {
			assert wellFormed() : "invariant broken at beginning of EntrySet size";
			return TreeMap.this.size();
			// TODO: Easy: delegate to TreeMap.size()
		}

		@Override // required
		public Iterator<Entry<K, V>> iterator() {
			assert wellFormed() : "invariant broken at beginning of iterator method";
			return new MyIterator();
		}
		// Otherwise, check the entry for this entry's key.
		// If there is no such entry return false;
		// Otherwise return whether the entries match (use the equals method of the Node class). 
		// N.B. You can't check whether the key is of the correct type
		// because K is a generic type parameter.  So you must handle any
		// Object similarly to how "get" does.

		@Override //efficiency
		public boolean contains(Object o) {
			assert wellFormed() : "Invariant broken at start of EntrySet.contains";
			// TODO if o is not an entry (instanceof Entry<?,?>), return false
			if(!(o instanceof Entry<?,?>))
				return false;
			Entry<?,?> a = (Entry<?,?> )o;

			if(a.equals(findKey(a.getKey()))) {
				return true;
			}
			assert wellFormed() : "Invariant broken at end of EntrySet.contains";
			return false;
		}

		@Override //efficiency
		public boolean remove(Object x) {
			assert wellFormed() : "Invariant broken at start of entrySet remove";

			if(!contains(x)) {
				return false;

			}else {
				Entry<?,?> e = (Entry<?,?>) x;
				TreeMap.this.remove(e.getKey());
			}
			++version;
			// TODO: if the tree doesn't contain x, return false
			// otherwise do a TreeMap remove.
			// make sure that the invariant is true before returning.
			assert wellFormed() : "Invariant broken at end of entrySet remove";
			return true;
		}

		@Override //efficiency
		public void clear() {
			// TODO: Easy: delegate to the TreeMap.clear()
			assert wellFormed() : "Invariant broken at beginning of entrySet remove";
			TreeMap.this.clear();
		}
	}

	/**
	 * Iterator over the map.
	 * We use parent pointers.
	 * current points to node (if any) that can be removed.
	 * next points to dummy indicating no more next.
	 */
	private class MyIterator implements Iterator<Entry<K,V>> {

		Node<K, V> current, next;
		int colVersion = version;

		boolean wellFormed() {
			// TODO: See Homework description for more details.  Here's a summary:
			// (1) check the outer wellFormed()
			if(!TreeMap.this.wellFormed())	return false;
			// (2) If version matches, do the remaining checks:
			if(colVersion!=version)	return true;	
			//     (a) current should either be null or a non-dummy node in the tree
			if(current == dummy)	return report("current cannot be dummy");
			//     (b) next should never be null and should be in the tree (maybe dummy).
			if(next == null || current == next)return report("next should not be null");
			if(next != dummy) {
				if(next.parent == null)
					return report("noooope");
			}
			if(current != null) {
				if(current.parent == null)
					return report("uhh");
				if(!containsKey(current.key))
					return report("next should never be null and should be in the tree (maybe dummy)");
			}
			//    (c) if current is not null, make sure it is the last node before where next is.
			if(current!= null)
				if(next != nextInTree(dummy.left, current.key, false, dummy))	return report("if current is not null, make sure it is the last node before where next is.");

			return true;
		}

		/**
		 * Return the first node in a non-empty subtree.
		 * It doesn't examine the data in teh nodes; 
		 * it just uses the structure.
		 * @param r subtree, must not be null
		 * @return first node in the subtree
		 */
		private Node<K,V> firstInTree(Node<K,V> r) {
			if(r!= null) {
				while(r.left!=null) {
					r = r.left;
				}
				//null means we are at the end of the loop
				if(r.left == null)
					return r;
			}
			return null; // TODO: non-recursive is fine
		}
		
		/**
		 * Find the node that has the appt (if acceptEquivalent) or the first thing
		 * after it.  Return that node.  Return the alternate if everything in the subtree
		 * comes before the given appt.
		 * @param r subtree to look into, may be null
		 * @param appt appointment to look for, must not be null
		 * @param acceptEquivalent whether we accept something equivalent.  Otherwise, only
		 * appointments after the appt are accepted.
		 * @param alt what to return if no node in subtree is acceptable.
		 * @return node that has the first element equal (if acceptEquivalent) or after
		 * the appt.
		 */
		private Node<K,V> nextInTree(Node<K,V> r, K appt, boolean acceptEquivalent, Node<K,V> alt) {
			// TODO: recursion not required, but is simpler

			if(r != null) {
				// if appt is equal to r
				if(comparator.compare(appt, r.key) == 0) {
					if(acceptEquivalent) return r;
					else {
						// find the successor
						// if r has a right child then get the left most of it
						// consider about duplicate elements
						return nextInTree(r.right, appt, acceptEquivalent, alt);
					}
				} 
				// if appt comes before r
				else if(comparator.compare(appt, r.key) < 0) {
					// whenever we go to the left we know that the root is our next element
					// unless there is a right child
					// go to the left node
					return nextInTree(r.left, appt, acceptEquivalent, r);
				} 
				// if appt comes after r
				else {
					// go to the right node
					return nextInTree(r.right, appt, acceptEquivalent, alt);
				} 
			}
			return alt;
		}

		MyIterator(boolean ignored) {} // do not change this

		/**
		 * constructor for new iterator
		 */
		MyIterator() {
			next = firstInTree(dummy);
			// TODO: initialize next to the leftmost node
			assert wellFormed() : "invariant broken after iterator constructor";
		}

		public void checkVersion() {
			if (version != colVersion) throw new ConcurrentModificationException("stale iterator");
		}

		@Override //required
		public boolean hasNext() {
			assert wellFormed() : "invariant broken before hasNext()";
			checkVersion();
			// TODO: easy!
			return next!=dummy;
		}

		@Override //required
		public Entry<K, V> next() {
			assert wellFormed() : "invariant broken at start of next()";
			// TODO
			// We don't use (non-existent)nextInTree: 
			// but rather parent pointers in the second case.
			checkVersion();
			if(!hasNext()) throw new NoSuchElementException("there is no next element");

			current = next;

			if(current.right !=null) {
				next =	firstInTree(current.right);
			}else {
				next = nextHelper(current);	
			}

			assert wellFormed() : "invariant broken at end of next()";
			return current;
		}

		//taken from lab 9
		private Node<K,V> nextHelper(Node<K,V> r) 
		{   
			if(r.right != null) { 
				r = r.right; 
				while(r.left != null)  r = r.left; 
				return r;
			} else {
				while(r.parent != null && r.parent.right == r) { 
					r = r.parent;
				}  
				return r.parent;
			} 
		}

		@Override //implementation
		public void remove() {
			assert wellFormed() : "invariant broken at start of iterator.remove()";
			// TODO: check that there is something to remove.
			checkVersion();
			if(current != null) {
				TreeMap.this.remove(current.key);
				current = null;
			} else  
				throw new IllegalStateException("no current");
			// Use the remove method from TreeMap to remove it.
			// (See handout for details.)
			// After removal, record that there is nothing to remove any more.
			// Handle versions.
			colVersion++;
			assert wellFormed() : "invariant broken at end of iterator.remove()";
		}
	}

	/// Junit test case of private internal structure.
	// Do not change this nested class.
	public static class TestSuite extends TestCase {

		protected Consumer<String> getReporter() {
			return reporter;
		}

		protected void setReporter(Consumer<String> c) {
			reporter = c;
		}

		protected static class Node<K,V> extends TreeMap.Node<K, V> {
			public Node(K k, V v) {
				super(k,v);
			}

			public void setLeft(Node<K,V> n) {
				this.left = n;
			}

			public void setRight(Node<K,V> n) {
				this.right = n;
			}

			public void setParent(Node<K,V> n) {
				this.parent = n;
			}
		}

		protected class MyIterator extends TreeMap<Integer,String>.MyIterator {
			public MyIterator() {
				tree.super(false);
			}

			public void setCurrent(Node<Integer,String> c) {
				this.current = c;
			}
			public void setNext(Node<Integer,String> nc) {
				this.next = nc;
			}
			public void setColVersion(int cv) {
				this.colVersion = cv;
			}

			@Override // make visible
			public boolean wellFormed() {
				return super.wellFormed();
			}
		}

		protected TreeMap<Integer,String> tree;

		@Override // implementation
		protected void setUp() {
			tree = new TreeMap<>(false);
		}

		protected boolean wellFormed() {
			return tree.wellFormed();
		}

		protected void setDummy(Node<Integer,String> d) {
			tree.dummy = d;
		}

		protected void setNumItems(int ni) {
			tree.numItems = ni;
		}

		protected void setComparator(Comparator<Integer> c) {
			tree.comparator = c;
		}

		protected void setVersion(int v) {
			tree.version = v;
		}

		protected Node<Integer,String> findKey(Object key) {
			return (Node<Integer,String>)tree.findKey(key);
		}
	}
}