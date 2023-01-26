import java.util.Comparator;
import java.util.Random;


/**
 * Things to be used by the driver / test suite.
 * Not to be used by application code in package edu.uwm.cs351.util
 */
public class TestUtil {

	public static <T> Comparator<T> nondiscrimination() {
		return new Comparator<T>() {
			public int compare(T o1, T o2) {
				return 0;
			}
		};
	}
	
	
	private static class DefaultComparator<T extends Comparable<T>> implements Comparator<T> {
		public int compare(T o1, T o2) {
			return o1.compareTo(o2);
		}	
	}
	
	public static <T extends Comparable<T>> Comparator<T> defaultComparator() {
		return new DefaultComparator<T>();
	}
	
	
	public static <T> Comparator<T> reverse(final Comparator<T> comp) {
		return new Comparator<T>() {
			public int compare(T o1, T o2) {
				return comp.compare(o2, o1);
			}
		};
	}
	
	public static <T> Comparator<T> obscure(final Comparator<T> comp) {
		return new Comparator<T>() {
			private Random r = new Random();
			public int compare(T o1, T o2) {
				int c = comp.compare(o1, o2);
				return c * (r.nextInt(100) + 1);
			}
		};
	}
	

}