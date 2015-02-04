package net.fhtagn.utils.utils;

// TODO: This will result in crashes in production => Change to warnings
// and send them to newrelic or some monitoring service
public class Check {  
  public final static void check(boolean what, String message) {
    if (!what) {
      throw new IllegalArgumentException("Check failed : " + message);
    }
  }
  
  // a == b
  public final static <T> void Eq (T a, T b) {
    if (!a.equals(b)) {
      throw new IllegalArgumentException("Check failed : " + a + " == " + b);
    }
  }
  
  // a < b
  public final static <T extends Comparable<T>> void Lt (T a, T b) {
    if (a.compareTo(b) >= 0) {
      throw new IllegalArgumentException("Check failed : " + a + " < " + b);
    }
  }
  
  // a > b
  public final static <T extends Comparable<T>> void Gt (T a, T b) {
    if (a.compareTo(b) <= 0) {
      throw new IllegalArgumentException("Check failed : " + a + " < " + b);
    }
  }
}
