import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Supplier;

import edu.uwm.cs.junit.LockedTestCase;
import edu.uwm.cs351.util.DefaultEntry;
import edu.uwm.cs351.util.TreeMap;


public class TestTreeMap extends LockedTestCase {
    protected static void assertException(Class<? extends Throwable> c, Runnable r) {
    	try {
    		r.run();
    		assertFalse("Exception should have been thrown",true);
        } catch (RuntimeException ex) {
        	assertTrue("should throw exception of " + c + ", not of " + ex.getClass(), c.isInstance(ex));
        }	
    }	

	protected Map<String,Integer> d1;
	protected Map<Integer,String> d2;
	protected Map<Secret,Integer> d3;
	
	
	@Override
	protected void setUp() {
		try {
			assert d1.get(null) == d2.size();
			throw new IllegalStateException("assertions must be enabled to run this test");
		} catch (NullPointerException ex) {
			// OK!
		}
		d1 = new TreeMap<String,Integer>(TestUtil.reverse(TestUtil.<String>defaultComparator()));
		d2 = new TreeMap<Integer,String>();
		d3 = new TreeMap<Secret,Integer>();
	}

	// A "Supplier" is an interface uses for lambda expressions
	// that we call the "get" method to run it.
	// For a "Supplier<T>", the get method will return a value of type T.
	/**
	 * Convert the result into a string
	 * @param s supplier of a value
	 * @return the string of the result, or the name of the exception thrown.
	 */
	protected String asString(Supplier<?> s) {
		try {
			return "" + s.get();
		} catch (RuntimeException ex) {
			return ex.getClass().getSimpleName();
		}
	}
	
	/// Locked Tests
	
	@SuppressWarnings("unlikely-arg-type")
	public void test() {
		d1.put("apples", 4);
		d1.put("oranges", 10);
		// choose the string of the result or the exception name
		assertEquals(Ts(385718565), asString(() -> d1.get("oranges")));
		assertEquals(Ts(653655633), asString(() -> d1.get("pears")));
		assertEquals(Ts(490246719), asString(() -> d1.put("apples", 6)));
		assertEquals(Ts(1645942496), asString(() -> d1.remove("oranges")));
		assertEquals(Ts(602528875), asString(() -> d1.remove(6)));
		assertEquals(Ts(291805556), asString(() -> d1.put("oranges", 100)));
		assertEquals(Ts(1314488979), asString(() -> d1.put(null, 42)));
		d1.clear();
		test1(d1);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	private void test1(Map<String,Integer> m) {
		// m starts empty
		m.put("apples", 3);
		m.put("oranges", 8);
		Set<Entry<String,Integer>> s = m.entrySet();
		assertEquals(2, s.size());
		m.put("bananas", 15);
		// type string of result or exception name (if it throws an exception)
		assertEquals(Ts(1031326257), asString(() -> s.size()));
		assertEquals(Ts(1131065326), asString(() -> s.remove("bananas"))); // removing from the entry set!
		assertEquals(Ti(291182933), s.size());
		s.remove(new DefaultEntry<>("bananas",0));
		assertEquals(Ti(84587854), s.size());
		s.remove(new DefaultEntry<>("bananas",15));
		assertEquals(Ti(97297734), m.size());
		assertEquals(Ts(2036890944), asString(() -> m.get("bananas")));
		d1.clear();
		test2(d1);
	}
	
	private void test2(Map<String,Integer> m) {
		// m starts empty using a DESCENDING comparator
		m.put("bread", 3);
		Set<Entry<String,Integer>> s = m.entrySet();
		Iterator<Entry<String,Integer>> it1 = s.iterator();
		// see DefaultEntry or the homework description:
		assertEquals(Ts(149500088), asString(() -> it1.next()));
		m.put("eggs", 12);
		assertEquals(Ts(455840129), asString(() -> it1.next()));
		Iterator<Entry<String,Integer>> it2 = s.iterator();
		assertEquals(Ts(1376678912), asString(() -> it2.next())); // what ordering are we using?
		m.put("bread", 1);
		assertEquals(Ts(285015467), asString(() -> it2.next())); // did the number of entries change?
	}
	
	
	
	/// test0x: tests of empty trees
	
	public void test00() {
		TreeMap<String,Integer> m = new TreeMap<String,Integer>(null);
		assertEquals(0, m.size());
	}
	
	public void test01() {
		assertEquals(0,d1.size());
	}
	
	public void test02() {
		assertNull(d1.get(""));
	}
	
	public void test03() {
		assertFalse(d1.containsKey("foo"));
	}
	
	public void test04() {
		assertEquals(0,d1.entrySet().size());
	}
	
	public void test05() {
		assertFalse(d1.entrySet().contains(new DefaultEntry<String,Integer>("hello",3)));
	}
	
	
	/// test1x: tests of put
	
	public void test10() {
		assertNull(d1.put("c",63));
		assertEquals(1,d1.size());
	}
	
	public void test11() {
		assertNull(d1.put("c", 63));
		assertEquals(63,d1.put("c",99).intValue());
		assertEquals(1,d1.size());
	}
	
	public void test12() {
		assertNull(d1.put("c", 63));
		assertNull(d1.put("a", 61));
		assertEquals(2,d1.size());
	}
	
	public void test13() {
		assertNull(d1.put("c", 63));
		assertNull(d1.put("a", 61));
		assertEquals(61,d1.put("a",97).intValue());
		assertEquals(2,d1.size());
	}
	
	public void test14() {
		assertNull(d1.put("c", 63));
		assertNull(d1.put("a", 61));
		assertNull(d1.put("f", 66));
		assertEquals(3,d1.size());
	}
	
	public void test15() {
		assertNull(d1.put("c", 63));
		assertNull(d1.put("a", 61));
		assertNull(d1.put("f", 66));
		assertNull(d1.put("b", 62));
		assertEquals(4,d1.size());
	}
	
	public void test16() {
		assertException(NullPointerException.class,() -> d1.put(null,60));
	}
	
	public void test17(){
		assertNull(d1.put("c", 63));
		assertNull(d1.put("a", 61));
		assertNull(d1.put("f", 66));
		assertNull(d1.put("b", 62));
		assertNull(d1.put("e", 65));
		assertNull(d1.put("d", 64));
		assertNull(d1.put("g", 67));
		
		assertEquals(7, d1.size());
		assertEquals(Integer.valueOf(61), d1.put("a", 97));
		assertEquals(7, d1.size());
		assertEquals(Integer.valueOf(62), d1.put("b", 98));
		assertEquals(7, d1.size());
		assertEquals(Integer.valueOf(63), d1.put("c", 99));
		assertEquals(7, d1.size());
		assertEquals(Integer.valueOf(64), d1.put("d", 100));
		assertEquals(7, d1.size());
		assertEquals(Integer.valueOf(65), d1.put("e", 101));
		assertEquals(7, d1.size());
		assertEquals(Integer.valueOf(66), d1.put("f", 102));
		assertEquals(7, d1.size());
		assertEquals(Integer.valueOf(67), d1.put("g", 103));
		assertEquals(7, d1.size());
	}
	
	public void test18() {
		d3.put(new Secret("hear"), null);
		d3.put(new Secret("here"), 10);
		
		assertNull(d3.put(new Secret("Hear"), 42));
		assertEquals(10,d3.put(new Secret("Here"), null).intValue());

		assertEquals(2,d3.size());
	}

	
	/// test2x: tests of get, containsKey

	public void test20() {
		d1.put("lemons", 4);
		assertEquals(4,d1.get("lemons").intValue());
	}
	
	public void test21() {
		d1.put("melons", 1);
		assertNull(d1.get("lemons"));
		assertNull(d1.get("oranges"));
	}
	
	public void test22() {
		d1.put("diamonds", 19);
		d1.put("rubies", 8);
		assertEquals(8,d1.get("rubies").intValue());
		assertEquals(19,d1.get("diamonds").intValue());
	}
	
	public void test23() {
		d1.put("emeralds", 2);
		d1.put("sapphires", 3);
		d1.put("carbuncles", 10);
		assertEquals(3,d1.get("sapphires").intValue());
		assertEquals(2,d1.get("emeralds").intValue());
		assertEquals(10,d1.get("carbuncles").intValue());
	}
	
	public void test24() {
		d1.put("emeralds", 2);
		d1.put("sapphires", 3);
		d1.put("carbuncles", 10);
		assertNull(d1.get("topaz"));
		assertNull(d1.get("garnets"));
		assertNull(d1.get("diamonds"));
		assertNull(d1.get("beryl"));
	}
	
	public void test25() {
		d1.put("eggs", 10);
		d1.put("oranges", 3);
		d1.put("carrots", 12);
		d1.put("apples", 6);
		assertFalse(d1.containsKey("rubies"));
		assertTrue(d1.containsKey("oranges"));
		assertFalse(d1.containsKey("sapphires"));
		assertTrue(d1.containsKey("eggs"));
		assertFalse(d1.containsKey("diamonds"));
		assertTrue(d1.containsKey("carrots"));
		assertFalse(d1.containsKey("carats"));
		assertTrue(d1.containsKey("apples"));
		assertFalse(d1.containsKey("amethyst"));
	}
	
	public void test26() {
		d2.put(100, "cents");
		d2.put(10, "dimes");
		d2.put(20, "nickels");
		d2.put(1000, "mills");
		d2.put(4, "quarters");
		
		assertEquals("nickels",d2.get(20));
		assertFalse(d2.containsKey(12));
		
		d2.clear();
		
		assertEquals(0,d2.size());
		assertFalse(d2.containsKey(100));
	}
	

	/// test3x: tests of iterators

	public void test30() {
		Iterator<Map.Entry<String, Integer>> it = d1.entrySet().iterator();
		assertFalse(it.hasNext());
	}
	
	public void test31() {
		d1.put("flowers", 0);
		Iterator<Map.Entry<String, Integer>> it = d1.entrySet().iterator();
		assertTrue(it.hasNext());
	}
	
	public void test32() {
		d1.put("gloves", 1);
		Iterator<Map.Entry<String, Integer>> it = d1.entrySet().iterator();
		assertEquals(new DefaultEntry<>("gloves",1),it.next());
	}
	
	private void makeTree() {
		// (((1)2(((3(4))5)6((7(8))9)))10((11)12(13)))
		d2.put(100,"100");
		d2.put(20,"20");
		d2.put(10,"10");
		d2.put(60,"60");
		d2.put(50,"50");
		d2.put(30,"30");
		d2.put(40,"40");
		d2.put(90, "90");
		d2.put(70, "70");
		d2.put(80,"80");
		d2.put(120,"120");
		d2.put(110,"110");
		d2.put(130,"130");
	}

	public void test39() {
		makeTree();
		assertEquals(13,d2.size());
		assertNull(d2.get(0));

		for (int i=10; i <= 130; i+=10)
			assertEquals(""+i,d2.get(i));

		for (int i=5; i < 130; i+=10)
			assertNull(d2.get(i));
	
		assertNull(d2.get(14));
		
		testCollection(d2.keySet(),"BIG",10,20,30,40,50,60,70,80,90,100,110,120,130);

		for (int i=10; i <= 130; i += 20)
			d2.put(i, "-"+i);
		
		assertEquals(13,d2.size());

		for (int i=10; i <= 130; i += 10) {
			String expected = (((i/10)&1) == 0 ? "" : "-") + i;
			assertEquals(expected,d2.get(i));
		}
		testCollection(d2.keySet(),"BIG-",10,20,30,40,50,60,70,80,90,100,110,120,130);
		
		d2.clear();
		
		assertEquals(0,d2.size());
		for (int i=0; i <= 140; ++i)
			assertNull(d2.get(i));
		
		testCollection(d2.keySet(),"BIG-cleared");
	}
	

	/// test4x: smaller tests of remove
	
	public void test40() {
		assertNull(d1.remove("hi"));
	}
	
	public void test41() {
		d2.put(66, "Hello");
		assertEquals("Hello",d2.remove(66));
		assertEquals(0,d2.size());
	}
	
	public void test42() {
		d3.put(new Secret("Hi"),77);
		d3.put(new Secret("bye"),101);
		assertEquals(101,d3.remove(new Secret("Bye")).intValue());
		assertEquals(1,d3.size());
	}
	
	public void test43() {
		d1.put("oranges",8);
		d1.put("apples",10);
		d1.put("pickles",-5);
		assertEquals(8,d1.remove("oranges").intValue());
		assertEquals(2,d1.size());
		assertEquals(10,d1.get("apples").intValue());
		assertEquals(-5,d1.get("pickles").intValue());
	}
	
	public void test44() {
		d2.put(10,"ten");
		d2.put(20,"twenty");
		d2.put(30,"thirty");
		assertEquals("twenty",d2.remove(20));
		assertEquals(2,d2.size());
		assertEquals("ten",d2.get(10));
		assertEquals("thirty",d2.get(30));
		assertNull(d2.remove(20));
	}
	
	public void test45() {
		d3.put(new Secret("Hi"),32);
		assertFalse(d3.entrySet().remove(new DefaultEntry<>(new Secret("Bye"),32)));
		assertEquals(1,d3.size());
	}
	
	public void test46() {
		d3.put(new Secret("Hi"),32);
		assertFalse(d3.entrySet().remove(new DefaultEntry<>(new Secret("Hi"),33)));
		assertEquals(1,d3.size());
	}
	
	public void test47() {
		d1.put("muffins", 0);
		assertTrue(d1.entrySet().remove(new MyEntry<>("muffins",0)));
		assertEquals(0,d1.size());
	}

	public void test48() {
		d2.put(50,"fifty");
		d2.put(30,"thirty");
		d2.put(40,"forty");
		assertTrue(d2.entrySet().remove(new MyEntry<>(40,"forty")));
		assertEquals(2,d2.size());
	}
	
	public void test49() {
		d2.put(50,"fifty");
		d2.put(30,"thirty");
		d2.put(40,"forty");
		assertFalse(d2.entrySet().remove(new MyEntry<>(40,"forty-one")));
		assertEquals(3,d2.size());
	}
	
	
	/// text5x: Tests of remove using iterators
	
	public void test50() {
		d1.put("eggs",12);
		Iterator<Map.Entry<String,Integer>> it = d1.entrySet().iterator();
		it.next();
		it.remove();
		assertEquals(0,d1.size());
	}
	
	public void test51() {
		d1.put("frogs",1);
		Iterator<Map.Entry<String,Integer>> it = d1.entrySet().iterator();
		it.next();
		it.remove();
		assertFalse(it.hasNext());
	}
	
	public void test52() {
		d2.put(45,"start");
		d2.put(100,"later");
		Iterator<Map.Entry<Integer,String>> it = d2.entrySet().iterator();
		it.next();
		it.remove();
		assertEquals(1,d2.size());
		assertEquals("later",d2.get(100));
	}
	
	public void test53() {
		d2.put(10,"first");
		d2.put(45,"last");
		Iterator<Map.Entry<Integer,String>> it = d2.entrySet().iterator();
		it.next();
		it.remove();
		assertEquals(new DefaultEntry<>(45,"last"),it.next());
	}
	
	public void test54() {
		d3.put(new Secret("Begin"),66);
		d3.put(new Secret("After"), 99);
		Iterator<Integer> it = d3.values().iterator();
		it.next();
		it.remove();
		assertEquals(1,d3.size());
		assertEquals(66,d3.get(new Secret("begin")).intValue());
		assertEquals(Integer.valueOf(66),it.next());
	}
	
	public void test55() {
		d1.put("eggs",12);
		d1.put("apples",8);
		d1.put("grapes",2);
		Iterator<Integer> it = d1.values().iterator();
		it.next();
		it.next();
		it.remove();
		assertEquals(2,d1.size());
		assertEquals(8,d1.get("apples").intValue());
		assertEquals(2,d1.get("grapes").intValue());
	}
	
	public void test56() {
		d1.put("eggs",12);
		d1.put("apples",8);
		d1.put("grapes",2);
		Iterator<String> it = d1.keySet().iterator();
		it.next();
		it.next();
		it.remove();
		assertEquals("apples",it.next());
	}
	
	public void test57() {
		d1.put("eggs",12);
		d1.put("apples",8);
		d1.put("grapes",2);
		Iterator<String> it = d1.keySet().iterator();
		it.next();
		it.remove();
		assertEquals("eggs",it.next());
		it.next();
		it.remove();
		assertFalse(it.hasNext());
		assertEquals(1,d1.size());
	}
	
	public void test58() {
		d1.put("eggs",12);
		d1.put("apples",8);
		d1.put("grapes",2);
		Iterator<String> it = d1.keySet().iterator();
		it.next();
		it.remove();
		it.next();
		it.remove();
		it.next();
		it.remove();
		assertFalse(it.hasNext());
		assertEquals(0,d1.size());
	}
	
	
	/// test6x: Tests of remove errors and stale iterators
	
	public void test60() {
		Iterator<String> it = d1.keySet().iterator();
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void test61() {
		d1.put("hello",67);
		Iterator<String> it = d1.keySet().iterator();
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void test62() {
		d1.put("bye",-19);
		d1.put("hello",100);
		Iterator<String> it = d1.keySet().iterator();
		it.next();
		it.remove();
		assertException(IllegalStateException.class, () -> it.remove());
	}
	
	public void test63() {
		Iterator<String> it = d1.keySet().iterator();
		d1.put("test", 19);
		assertException(ConcurrentModificationException.class,() -> it.hasNext());
	}
	
	public void test64() {
		Iterator<String> it = d1.keySet().iterator();
		d1.put("test", 19);
		assertException(ConcurrentModificationException.class,() -> it.next());
	}
	
	public void test65() {
		d1.put("bye",-19);
		d1.put("hello",100);
		Iterator<String> it = d1.keySet().iterator();
		it.next();
		d1.put("test",0);
		assertException(ConcurrentModificationException.class,() -> it.hasNext());
		assertException(ConcurrentModificationException.class,() -> it.remove());
	}
	
	public void test66() {
		d1.put("bye",-19);
		d1.put("hello",100);
		Iterator<String> it = d1.keySet().iterator();
		it.next();
		d1.put("hello",-100);
		d1.put("bye",19);
		assertTrue(it.hasNext());
		assertEquals("bye",it.next());
	}
	
	public void test67() {
		d1.put("bye",-19);
		d1.put("hello",100);
		Iterator<String> it = d1.keySet().iterator();
		it.next();
		d1.remove("hello");
		assertException(ConcurrentModificationException.class,() -> it.next());
	}
	
	public void test68() {
		d1.put("test",100);
		d1.put("bye",77);
		Iterator<String> it = d1.keySet().iterator();
		it.next();
		d1.clear();
		assertException(ConcurrentModificationException.class,() -> it.hasNext());
	}
	
	public void test69() {
		d1.put("test",100);
		d1.put("bye",77);
		Iterator<String> it = d1.keySet().iterator();
		it.next();
		d1.clear();
		assertException(ConcurrentModificationException.class,() -> it.remove());
	}

	
	/// test7x: Tests for lots of removes

	private void removeOne(int x) {
		makeTree();
		assertEquals(Integer.valueOf(x).toString(), d2.remove(x));
		
		assertEquals("(BIG-"+(x)+").size()",12,d2.size());
		assertNull("d2.get(" + x + ") should be removed",d2.get(x));
		for (int i=1; i <= 13; ++i)
			if (i*10 != x) assertEquals("(BIG-"+x+").get("+(i*10)+")",""+(i*10),d2.get(i*10));
	}
	
	private void removeOneEntry(int x) {
		makeTree();
		d2.entrySet().remove(new DefaultEntry<Integer,String>(x,""+x));
		
		assertEquals(12,d2.size());
		assertNull("d2.get(" + x + ") should be removed",d2.get(x));
		for (int i=1; i <= 13; ++i)
			if (i*10 != x) assertEquals(""+(i*10),d2.get(i*10));
	}

	private void removeWithIterator(int x) {
		makeTree();
		for (Iterator<Entry<Integer,String>> it = d2.entrySet().iterator(); it.hasNext();)
			if (it.next().getKey().intValue() == x)
				it.remove();

		
		assertEquals(12,d2.size());
		assertNull("d2.get(" + x + ") should be removed",d2.get(x));
		for (int i=1; i <= 13; ++i)
			if (i*10 != x) assertEquals(""+(i*10),d2.get(i*10));
	}

	public void test70() {
		for (int i=10; i <= 130; i += 10) {
			d2 = new TreeMap<>();
			removeOne(i);
		}		
	}
	
	public void test71() {
		for (int i=10; i <= 130; i += 10) {
			d2 = new TreeMap<>();
			removeOneEntry(i);
		}				
	}
	
	public void test72() {
		for (int i=10; i <= 130; i += 10) {
			d2 = new TreeMap<>();
			removeWithIterator(i);
		}
	}
	
	public void test73() {
		makeTree();
		for (Iterator<Integer> it = d2.keySet().iterator(); it.hasNext();) {
			it.next();
			it.remove();
		}
		
		assertEquals(0,d2.size());
		for (int i=0; i <= 140; ++i)
			assertNull(d2.get(i));
	}
	
	public void test74() {
		makeTree();
		for (Iterator<Integer> it = d2.keySet().iterator(); it.hasNext();)
			if (((it.next() / 10) & 1) != 0)
				it.remove();

		assertEquals(6,d2.size());
		for (int i=10; i <= 130; i += 10)
			if (((i/10) & 1) == 0)
				assertEquals(""+i,d2.get(i));	
			else
				assertNull("d2.get(" + i + ") should have been removed (odd)",d2.get(i));
	}
	
	public void test75() {
		makeTree();
		for (Iterator<Integer> it = d2.keySet().iterator(); it.hasNext();)
			if (((it.next() / 10) & 1) == 0)
				it.remove();

		assertEquals(7,d2.size());
		for (int i=10; i <= 130; i += 10)
			if (((i/10) & 1) != 0)
				assertEquals(""+i,d2.get(i));	
			else
				assertNull("d2.get(" + i + ") should have been removed (even)",d2.get(i));
	}

	public void test76() {
		makeTree();
		for (Iterator<Integer> it = d2.keySet().iterator(); it.hasNext();)
			if (it.next() < 70)
				it.remove();

		assertEquals(7,d2.size());
		for (int i=10; i <= 130; i += 10)
			if (i > 60)
				assertEquals(""+i,d2.get(i));	
			else
				assertNull("d2.get(" + i + ") should have been removed (small)",d2.get(i));
	}

	public void test77() {
		makeTree();
		for (Iterator<Integer> it = d2.keySet().iterator(); it.hasNext();)
			if (it.next() >= 70)
				it.remove();

		assertEquals(6,d2.size());
		for (int i=10; i <= 130; i += 10)
			if (i <= 60)
				assertEquals(""+i,d2.get(i));	
			else
				assertNull("d2.get(" + i + ") should have been removed (small)",d2.get(i));
	}
	
	public void test78() {
		Iterator<Map.Entry<String,Integer>> it = d1.entrySet().iterator();
		d1.clear(); // makes no changes
		assertFalse(it.hasNext());
		d1.put("forty-two", 42);
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
	}
	
	public void test79() {
		d1.put("forty-two", 42);
		Iterator<Map.Entry<String,Integer>> it = d1.entrySet().iterator();
		d1.clear(); // makes changes
		assertException(ConcurrentModificationException.class, () -> it.hasNext());
	}


	/// test8x: testing nulls
	
	public void test81() {
		makeTree();
		assertNull(d2.get(null));
	}
	
	public void test82() {
		makeTree();
		assertNull(d2.remove(null));
	}
	
	public void test83() {
		makeTree();
		assertFalse(d2.containsKey(null));
	}

	public void test84() {
		makeTree();
		assertFalse(d2.entrySet().contains(null));
	}

	public void test85() {
		makeTree();
		assertFalse(d2.entrySet().remove(null));
	}

	public void test86() {
		makeTree();
		assertFalse(d2.entrySet().contains(new DefaultEntry<Integer,String>(45,null)));
	}

	public void test87() {
		makeTree();
		assertFalse(d2.entrySet().contains(new DefaultEntry<Integer,String>(null,"hello")));
	}

	public void test88() {
		makeTree();
		assertFalse(d2.entrySet().contains(new DefaultEntry<Integer,String>(null,null)));
	}
	
	public void test89() {
		d1.put("hello",null);
		assertTrue(d1.entrySet().contains(new MyEntry<>("hello",null)));
		assertFalse(d1.entrySet().contains(new DefaultEntry<>("hello",0)));
		assertTrue(d1.entrySet().remove(new DefaultEntry<>("hello",null)));
	}
	
	
	/// test9x: testing proper casting
	// Warnings being suppressed because we want to
	// call your code in "bad" situations and Eclipse is warning
	// us that our calls don't make real sense.
	
	@SuppressWarnings("unlikely-arg-type")
	public void test90() {
		makeTree();
		assertNull(d2.get("hello"));
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void test91() {
		makeTree();
		assertNull(d2.remove("hello"));
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void test92() {
		makeTree();
		assertFalse(d2.containsKey("hello"));
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void test93() {
		makeTree();
		assertFalse(d2.entrySet().contains("hello"));
	}
	
	public void test94() {
		makeTree();
		assertFalse(d2.entrySet().contains(new DefaultEntry<String,Integer>("100",10)));
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void test95() {
		makeTree();
		assertFalse(d2.entrySet().remove("hello"));
	}
	
	public void test96() {
		makeTree();
		assertFalse(d2.entrySet().remove(new DefaultEntry<String,Integer>("100",10)));
	}
	
	public void test97() {
		TreeMap<Wrapper<Integer>,String> t = new TreeMap<>();
		t.put(new Wrapper<Integer>(88),"nice!");
		t.put(new Wrapper<Integer>(100),"round");
		assertFalse(t.entrySet().contains(new DefaultEntry<Wrapper<String>,String>(new Wrapper<String>("88"),"nice!")));
	}
	
	
	private <T> void testCollection(Collection<T> l, String name, Object... parts)
	{
		assertEquals(name + ".size()",parts.length,l.size());
		Iterator<T> it = l.iterator();
		int i=0;
		while (it.hasNext() && i < parts.length) {
			assertEquals(name + "[" + i + "]",parts[i],it.next());
			++i;
		}
		
		assertFalse(name + " claims too long (iterator claims more elements)",it.hasNext());
		try {
			it.next();
			assertFalse("iterator should have thrown exception, there is no next", true);
		} catch(Exception e){
			assertTrue(e instanceof NoSuchElementException);
		}
		
		assertFalse(name + " claims too short (iterator stops too soon, after " + i + " elements)",(i < parts.length));
	}
	
	private static class Wrapper<E extends Comparable<E>> implements Comparable<Wrapper<E>> {
		private final E wrapped;
		
		public Wrapper(E w) {
			wrapped = w;
		}
		
		@Override
		public int hashCode() {
			return (wrapped == null) ? 0 : wrapped.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Wrapper<?>)) return false;
			Wrapper<?> w = (Wrapper<?>)obj;
			return wrapped == null ? w.wrapped == null : wrapped.equals(w.wrapped);
		}

		@Override
		public String toString() {
			return "Wrapper(" + wrapped + ")";
		}

		@Override
		public int compareTo(Wrapper<E> o) {
			if (wrapped == null) return o.wrapped == null ? 0 : -1;
			if (o.wrapped == null) return 1;
			return wrapped.compareTo(o.wrapped);
		} 
		
	}
	private static class Secret implements Comparable<Secret> {
		
		private final String contents;
		public Secret(String s) { contents = s; }
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Secret)) return false;
			Secret s = (Secret)o;
			return contents.equalsIgnoreCase(s.contents);
		}
		
		@Override
		public int hashCode() {
			return contents.toLowerCase().hashCode(); }
		
		public int compareTo(Secret c) {
			return contents.compareToIgnoreCase(c.contents); }
	}
	
	private static class MyEntry<K,V> implements Entry<K,V> {
		private final Entry<K,V> wrapped;
		MyEntry(K k, V v) {
			wrapped = new DefaultEntry<>(k,v);
		}
		@Override
		public K getKey() {
			return wrapped.getKey();
		}
		@Override
		public V getValue() {
			return wrapped.getValue();
		}
		@Override
		public V setValue(V value) {
			return wrapped.setValue(value);
		}
		@Override
		public boolean equals(Object obj) {
			return wrapped.equals(obj);
		}
		@Override
		public int hashCode() {
			return wrapped.hashCode();
		}
		
	}
}