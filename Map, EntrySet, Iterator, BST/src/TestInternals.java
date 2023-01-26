import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.util.TreeMap;



public class TestInternals extends TreeMap.TestSuite {
	private int reports = 0;
	
	protected void assertReporting(boolean expected, Supplier<Boolean> test) {
		reports = 0;
		Consumer<String> savedReporter = getReporter();
		try {
			setReporter((String message) -> {
				++reports;
				if (message == null || message.trim().isEmpty()) {
					assertFalse("Uninformative report is not acceptable", true);
				}
				if (expected) {
					assertFalse("Reported error incorrectly: " + message, true);
				}
			});
			assertEquals(expected, test.get().booleanValue());
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
			setReporter(null);
		} finally {
			setReporter(savedReporter);
		}

	}

	private Comparator<Integer> ascending = new Comparator<Integer>() {
		public int compare(Integer arg0, Integer arg1) {
			return arg0 - arg1;
		}
	};
	private Comparator<Integer> descending = new Comparator<Integer>() {
		public int compare(Integer arg0, Integer arg1) {
			return arg1 - arg0;
		}
	};
	private Comparator<Integer> nondiscrimination = new Comparator<Integer>() {
		public int compare(Integer arg0, Integer arg1) {
			return 0;
		}
	};
	
	private Node<Integer,String> n1, n2, n3, n4, n5, n3a;
	private Node<Integer,String> d;
	
	@Override // decorate
	protected void setUp() {
		super.setUp();
		d = new Node<Integer,String>(null,"DUMMY");
		n1 = new Node<Integer,String>(1,"one");
		n2 = new Node<Integer,String>(2,"two");
		n3 = new Node<Integer,String>(3,"three");
		n4 = new Node<Integer,String>(4,"four");
		n5 = new Node<Integer,String>(5,"five");
		n3a = new Node<Integer,String>(3,"three");
	}

	public void testA() {
		setDummy(d);
		assertReporting(false, () -> wellFormed());
		setComparator(ascending);
		assertReporting(true, () -> wellFormed());
		setDummy(null);
		assertReporting(false, () -> wellFormed());
	}

	public void testB(){
		setComparator(ascending);
		assertReporting(false, () -> wellFormed());
		setDummy(d);			
		assertReporting(true, () -> wellFormed());
		d.setRight(n1);
		n1.setParent(d);
		assertReporting(false, () -> wellFormed());
		d.setRight(null);
		d.setParent(n2);
		n2.setRight(d);
		assertReporting(false, () -> wellFormed());
		n2.setLeft(d);
		n2.setRight(null);
		assertReporting(false, () -> wellFormed());
		setDummy(n2);
		setNumItems(1);
		assertReporting(false, () -> wellFormed());
	}
	
	public void testC() {
		setComparator(ascending);
		setDummy(d);
		setNumItems(1);
		assertReporting(false, () -> wellFormed());
	}
	
	public void testD() {
		setComparator(ascending);
		setDummy(d);
		d.setLeft(n3);
		n3.setParent(d);
		assertReporting(false, () -> wellFormed());
		setNumItems(1);
		setComparator(nondiscrimination);
		assertReporting(true, () -> wellFormed());
		setComparator(null);
		assertReporting(false, () -> wellFormed());
		setComparator(ascending);
		n3.setParent(null);
		assertReporting(false, () -> wellFormed());
		n3.setParent(n2);
		assertReporting(false, () -> wellFormed());
		n2.setRight(n3);
		assertReporting(false, () -> wellFormed());
	}
	
	public void testE() {
		setComparator(ascending);
		setDummy(d);
		d.setLeft(n3);
		n3.setParent(d);
		n3.setLeft(n1);
		n1.setParent(n3);
		setNumItems(1);
		assertReporting(false, () -> wellFormed());
		setNumItems(2);
		assertReporting(true, () -> wellFormed());
		setComparator(nondiscrimination);
		assertReporting(false, () -> wellFormed());
		setComparator(descending);
		assertReporting(false, () -> wellFormed());
		setComparator(ascending);
		assertReporting(true, () -> wellFormed());
		n1.setParent(null);
		assertReporting(false, () -> wellFormed());
		n1.setParent(n3a);
		assertReporting(false, () -> wellFormed());
		n3a.setLeft(n1);
		assertReporting(false, () -> wellFormed());
	}
	
	public void testF() {
		setDummy(d);
		d.setLeft(n3);
		n3.setParent(d);
		n4.setParent(n3);
		n3.setLeft(n4);
		setNumItems(2);
		setComparator(ascending);
		assertReporting(false, () -> wellFormed());
		n3.setLeft(n3);
		assertReporting(false, () -> wellFormed());
		n3.setParent(n3);
		assertReporting(false, () -> wellFormed());
		n3.setLeft(null);
		n3.setRight(n3);
		assertReporting(false, () -> wellFormed());
		n3.setParent(d);
		assertReporting(false, () -> wellFormed());
	}
	
	public void testG() {
		setDummy(d);
		d.setLeft(n5);
		n5.setParent(d);
		n5.setLeft(n2); 
		n2.setParent(n5);
		n2.setRight(n3); 
		n3.setParent(n2);
		n3.setLeft(n1); 
		n1.setParent(n3);
		setNumItems(4);
		setComparator(ascending);
		assertReporting(false, () -> wellFormed());
		
		n3.setLeft(null); 
		n3.setRight(n4); 
		assertReporting(false, () -> wellFormed());
		n4.setParent(n3);
		assertReporting(true, () -> wellFormed());
		
		setNumItems(5);
		assertReporting(false, () -> wellFormed());
		setNumItems(4);
		n4.setParent(null);
		assertReporting(false, () -> wellFormed());
		n4.setParent(n3a);
		assertReporting(false, () -> wellFormed());
		n3a.setRight(n4);
		assertReporting(false, () -> wellFormed());
	}
	
	public void testH() {
		setDummy(d);
		d.setLeft(n1);
		n1.setParent(d);
		n1.setRight(n4);
		n4.setParent(n1);
		n4.setLeft(n3);
		n3.setParent(n4);
		n3.setRight(n5);
		n5.setParent(n3);
		setNumItems(4);
		setComparator(ascending);
		assertReporting(false, () -> wellFormed());
		
		n3.setRight(null); 
		n3.setLeft(n2);
		assertReporting(false, () -> wellFormed());
		n2.setParent(n3);
		assertReporting(true, () -> wellFormed());
		n2.setParent(n3a);
		assertReporting(false, () -> wellFormed());
		n3a.setLeft(n2);
		assertReporting(false, () -> wellFormed());
	}
	
	public void testI() {
		setDummy(d);
		d.setLeft(n3);
		n3.setParent(d);
		n3.setLeft(n2); 
		n2.setParent(n3);
		n2.setLeft(n1); 
		n1.setParent(n2);
		n3.setRight(n5);
		n5.setParent(n3);
		n5.setLeft(n4);
		n4.setParent(n5);
		setComparator(ascending);
		for (int i=0; i < 10; ++i) {
			setNumItems(i);
			if (i == 5) assertReporting(true, () -> wellFormed());
			else assertReporting(false, () -> wellFormed());
		}
		setNumItems(5);
		setComparator(nondiscrimination);
		assertReporting(false, () -> wellFormed());
	}
	
	public void testJ() {
		setDummy(d);
		setComparator(ascending);
		assertNull(findKey(0));
		assertNull(findKey("String"));
		assertNull(findKey(null));
	}
	
	public void testK() {
		setDummy(d);
		setComparator(ascending);
		d.setLeft(n3); n3.setParent(d);
		n3.setRight(n5); n5.setParent(n3);
		setNumItems(2);
		assertReporting(true, () -> wellFormed());
		
		assertNull(findKey(0));
		assertNull(findKey("String"));
		assertNull(findKey(null));
		
		assertSame(n3, findKey(3));
		assertNull(findKey(4));
		assertSame(n5, findKey(5));
		assertNull(findKey(6));
		
		// making sure code does not use iterators:
		Node<Integer,String> n2 = new Node<>(2,"two");
		Node<Integer, String> n2a = new Node<>(2,"two again");
		n2.setLeft(n2a); n2.setRight(n2);
		n2a.setLeft(n2); n2a.setRight(n2a);
		n3.setLeft(n2); n2.setParent(n3); n2a.setParent(n2);
		
		assertSame(n3, findKey(3));
		assertNull(findKey(4));
		assertSame(n5, findKey(5));
		assertNull(findKey(6));
	}
	
	public void testL() {
		setDummy(d);
		setComparator(descending);
		
		d.setLeft(n4); n4.setParent(d);
		n4.setRight(n2); n2.setParent(n4);
		n2.setRight(n1); n1.setParent(n2);
		n2.setLeft(n3); n3.setParent(n2);
		
		Node<Integer,String> n6 = new Node<>(12, "six");
		Node<Integer,String> n7 = new Node<>(14, "seven");
		
		n4.setLeft(n7); n7.setParent(n4);
		n7.setRight(n5); n5.setParent(n7);
		n5.setLeft(n6); n6.setParent(n5);
		
		setNumItems(7);
		
		assertReporting(true, () -> wellFormed());
		
		assertNull(findKey(0));
		assertSame(n1, findKey(1));
		assertSame(n2, findKey(2));
		assertSame(n3, findKey(3));
		assertSame(n4, findKey(4));
		assertSame(n5, findKey(5));
		assertNull(findKey(6));
		assertNull(findKey(8));
		assertNull(findKey(10));
		assertSame(n6, findKey(12));
		assertNull(findKey(13));
		assertSame(n7, findKey(14));
		assertNull(findKey(15));
	}
	
	public void testM() {
		setDummy(d);
		setComparator(ascending);
		assertReporting(true, () -> wellFormed());
		
		MyIterator it = new MyIterator();
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(null);
		it.setNext(d);
		assertReporting(true, () -> it.wellFormed());
		
		setComparator(null);
		assertReporting(false, () -> wellFormed());
		setComparator(descending);
		
		it.setCurrent(n1);
		assertReporting(false, () -> it.wellFormed());
		it.setColVersion(2);
		assertReporting(true, () -> it.wellFormed());
	}
	
	public void testN() {
		setDummy(d);
		d.setLeft(n3);
		n3.setParent(d);
		n3.setLeft(n2); 
		n2.setParent(n3);
		n2.setLeft(n1); 
		n1.setParent(n2);
		n3.setRight(n5);
		n5.setParent(n3);
		n5.setLeft(n4);
		n4.setParent(n5);
		setComparator(ascending);
		setNumItems(5);
		n3a.setLeft(n2);
		n3a.setRight(n5);
		MyIterator it = new MyIterator();
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(n5);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(null);
		it.setNext(n5);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(n5);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(n4);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(n3);
		assertReporting(false, () -> it.wellFormed());
		it.setNext(n4);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(n4);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(null);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(n3a);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(n2);
		assertReporting(false, () -> it.wellFormed());
		it.setNext(n3);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(n1);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(null);
		assertReporting(true, () -> it.wellFormed());
	}

	public void testO() {
		setDummy(d);
		d.setLeft(n4);
		n4.setParent(d);
		n4.setLeft(n2);
		n2.setParent(n4);
		n4.setRight(n5);
		n5.setParent(n4);
		n2.setLeft(n1); 
		n1.setParent(n2);
		n2.setRight(n3);
		n3.setParent(n2);
		setComparator(ascending);
		setNumItems(5);
		MyIterator it = new MyIterator();
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(d);
		assertReporting(false, () -> it.wellFormed());
		it.setNext(d);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(n1);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(null);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(n2);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(n4);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(n5);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(n3);
		assertReporting(false, () -> it.wellFormed());
		it.setNext(n4);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(n3a);
		assertReporting(false, () -> it.wellFormed());
		it.setCurrent(null);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(n4);
		assertReporting(false, () -> it.wellFormed());
		it.setNext(n5);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(null);
		assertReporting(true, () -> it.wellFormed());
		it.setCurrent(d);
		assertReporting(false, () -> it.wellFormed());
		it.setNext(n3);
		assertReporting(false, () -> it.wellFormed());
	}

}
