package jcarbon.data;

import java.time.Instant;

/** A piece of data over a timestamped range. */
public interface Interval<T> {
  /** Start of the interval. */
  Instant start();

  /** End of the interval. */
  Instant end();

  /** The executing component. */
  Component component();

  /** What is in the interval. */
  T data();
}
