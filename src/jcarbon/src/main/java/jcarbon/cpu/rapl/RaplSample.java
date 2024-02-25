package jcarbon.cpu.rapl;

import static java.util.stream.Collectors.joining;

import java.time.Instant;
import java.util.Arrays;
import jcarbon.data.Sample;

/** A sample of rapl energy consumption since boot. */
public final class RaplSample implements Sample<RaplReading[]>, Comparable<RaplSample> {
  private final Instant timestamp;
  private final RaplReading[] readings;

  RaplSample(Instant timestamp, RaplReading[] readings) {
    this.timestamp = timestamp;
    this.readings = Arrays.copyOf(readings, readings.length);
  }

  @Override
  public Instant timestamp() {
    return timestamp;
  }

  @Override
  public RaplReading[] data() {
    return Arrays.copyOf(readings, readings.length);
  }

  @Override
  public String toString() {
    // TODO: temporarily using json
    return String.format(
        "{\"timestamp\":{\"seconds\":%d,\"nanos\":%d},\"data\":[%s]}",
        timestamp.getEpochSecond(),
        timestamp.getNano(),
        Arrays.stream(readings).map(RaplReading::toString).collect(joining(",")));
  }

  @Override
  public int compareTo(RaplSample other) {
    return timestamp().compareTo(other.timestamp());
  }
}
