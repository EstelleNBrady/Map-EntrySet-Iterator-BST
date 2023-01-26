import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;
import edu.uwm.cs351.util.DefaultEntry;
import edu.uwm.cs351.util.TreeMap;


public class TestEfficiency extends TestCase {

	private TreeMap<Integer,Integer> tree;
	
	private Random random;
	
	private static final int POWER = 22; // 2 million entries
	private static final int TESTS = 100000;
	
	protected void setUp() throws Exception {
		super.setUp();
		random = new Random();
		try {
			assert tree.size() == TESTS : "cannot run test with assertions enabled";
		} catch (NullPointerException ex) {
			throw new IllegalStateException("Cannot run test with assertions enabled");
		}
		tree = new TreeMap<Integer,Integer>(TestUtil.<Integer>defaultComparator());
		int max = (1 << (POWER)); // 2^(POWER) = 2 million
		for (int power = POWER; power > 1; --power) {
			int incr = 1 << power;
			for (int i=1 << (power-1); i < max; i += incr) {
				tree.put(i, power);
			}
		}
	}
		
	
	@Override
	protected void tearDown() throws Exception {
		tree = null;
		super.tearDown();
	}


	public void testIsEmpty() {
		for (int i=0; i < TESTS; ++i) {
			assertFalse(tree.isEmpty());
		}
	}
	
	public void testSize() {
		for (int i=0; i < TESTS; ++i) {
			assertEquals((1<<(POWER-1))-1,tree.size());
		}
	}

	public void testGet() {
		for (int i=0; i < TESTS; ++i) {
			int r = random.nextInt(TESTS);
			assertTrue(tree.containsKey(r*4+2));
			assertEquals(2,tree.get(r*4+2).intValue());
			assertNull(tree.get(r*2+1));
			assertFalse(tree.containsKey(r*2+1));
		}
	}
	
	public void testIterator() {
		Iterator<Integer> it = tree.values().iterator();
		for (int i=0; i < TESTS; ++i) {
			assertTrue("After " + i + " next(), should still have next",it.hasNext());
			it.next();
		}
	}
	
	public void testIteratorCreation() {
		for (int i=0; i < TESTS; ++i) {
			Iterator<Integer> it = tree.values().iterator();
			assertTrue(it.hasNext());
			assertEquals(2,it.next().intValue());
			assertTrue(it.hasNext());
			assertEquals(3,it.next().intValue());
			assertTrue(it.hasNext());
		}
	}
	
	public void testContainsKey() {
		for (int i=0; i < TESTS; ++i) {
			int r = random.nextInt(TESTS);
			assertTrue(tree.containsKey(r*4+2));
		}
	}
	
	public void testSetContains() {
		for (int i=0; i < TESTS; ++i) {
			int r = random.nextInt(TESTS);
			assertFalse("should not contain bad entry for " + i,
					tree.entrySet().contains(new DefaultEntry<Integer,Integer>(r*4+2,1)));
			assertTrue("should contain entry for " + i,
					tree.entrySet().contains(new DefaultEntry<Integer,Integer>(r*4+2,2)));
			assertFalse("should not contain non-existent entry for " + i,
					tree.entrySet().contains(new DefaultEntry<Integer,Integer>(r*2+1,1)));
		}
	}

	public void testRemove() {
		Set<Integer> touched = new HashSet<Integer>();
		for (int i=0; i < TESTS; ++i) {
			int r = random.nextInt(TESTS);
			if (!touched.add(r)) continue; // don't check again
			assertEquals(2,tree.remove(r*4+2).intValue());
			assertNull(tree.remove(r*2+1));
		}
	}
	
	public void testRemoveSet() {
		Set<Integer> touched = new HashSet<Integer>();
		for (int i=0; i < TESTS; ++i) {
			int r = random.nextInt(TESTS);
			if (!touched.add(r)) continue; // don't check again
			assertFalse("should not be able to remove bad entry for " + i,
					tree.entrySet().remove(new DefaultEntry<Integer,Integer>(r*4+2,1)));
			assertTrue("should be able to remove entry for " + i,
					tree.entrySet().remove(new DefaultEntry<Integer,Integer>(r*4+2,2)));
			assertFalse("should not be able to remove non-existent entry for " + i,
					tree.entrySet().remove(new DefaultEntry<Integer,Integer>(r*2+1,1)));
		}
	}
	
	public void testRemoveIterator() {
		int removed = 0;
		int max = (1 << POWER);
		assertEquals(max/2-1,tree.size());
		Iterator<Entry<Integer,Integer>> it = tree.entrySet().iterator();
		for (int i = 2; i < max; i += 2) {
			assertEquals(i,it.next().getKey().intValue());
			if (random.nextBoolean()) {
				it.remove();
				++removed;
			}
		}
		assertEquals(max/2-1-removed,tree.size());
	}
}
