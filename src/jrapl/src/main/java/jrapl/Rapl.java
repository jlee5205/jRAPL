package jrapl;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

/** Simple wrapper around rapl access that requires libjrapl.so. */
public final class Rapl {
  // TODO: the delimiter is currently hacked-in to be ;. this was done because of
  // the formatting issue associated with , vs . on certain locales
  private static final String ENERGY_STRING_DELIMITER = ";";
  private static final HashMap<String, Integer> COMPONENTS;

  private static final double WRAP_AROUND;
  private static final double DRAM_WRAP_AROUND;

  /**
   * Returns the values read from rapl as a double array. Each component for a single socket is
   * listed before the next socket. The component order associated with the other element can be
   * determined from {@code getComponents}. The final element is always a microsecond precision unix
   * timestamp.
   *
   * <p>For example, if the components are {"package": 0, "dram": 1} with socket 0 and 1, the output
   * will be [package_0, dram_0, package_1, dram_1, unixtime_seconds].
   */
  public static double[] read() {
    double[] entries =
        Arrays.stream(readNative().split(ENERGY_STRING_DELIMITER))
            .mapToDouble(e -> Double.parseDouble(e))
            .toArray();
    entries[entries.length - 1] /= 1000000; // convert to seconds to be consistent
    return entries;
  }

  /** Returns an {@link EnergySample} populated by parsing the string returned by {@ readNative}. */
  public static EnergySample sample() {
    if (COMPONENTS.isEmpty()) {
      LoggerUtil.LOGGER.warning("no components founds; rapl likely not available");
      return new EnergySample(Instant.now(), new EnergyReading[0]);
    }

    double[] entries = read();

    // get the timestamp
    long secs = (long) entries[entries.length - 1];
    long nanos = (long) (1000000 * (entries[entries.length - 1] - (double) secs));
    Instant timestamp = Instant.ofEpochSecond(secs, nanos);

    // pull out energy values
    EnergyReading[] readings = new EnergyReading[MicroArchitecture.SOCKETS];
    for (int socket = 0; socket < MicroArchitecture.SOCKETS; socket++) {
      // TODO: i was lazy, probably should use a builder or compress this somehow.
      double pkg = 0;
      double dram = 0;
      double core = 0;
      double gpu = 0;
      for (String component : COMPONENTS.keySet()) {
        double energy = entries[COMPONENTS.size() * socket + COMPONENTS.get(component)];
        switch (component) {
          case "pkg":
            pkg = energy;
            break;
          case "dram":
            dram = energy;
            break;
          case "core":
            core = energy;
            break;
          case "gpu":
            gpu = energy;
            break;
        }
      }
      readings[socket] = new EnergyReading(socket, pkg, dram, core, gpu);
    }

    return new EnergySample(timestamp, readings);
  }

  /** Computes the difference of two {@link EnergyReadings}, applying the wraparound. */
  public static EnergyReading difference(EnergyReading first, EnergyReading second) {
    if (first.socket != second.socket) {
      throw new IllegalArgumentException(
          String.format(
              "readings are not from the same domain (%d != %d)", first.socket, second.socket));
    }
    return new EnergyReading(
        first.socket,
        diffWithWraparound(first.pkg, second.pkg),
        diffWithDramWraparound(first.dram, second.dram),
        diffWithWraparound(first.core, second.core),
        diffWithWraparound(first.gpu, second.gpu));
  }

  /** Computes the difference of two {@link EnergyReadings}, applying the wraparound. */
  public static EnergyInterval difference(EnergySample first, EnergySample second) {
    if (first.timestamp.compareTo(second.timestamp) > -1) {
      throw new IllegalArgumentException(
          String.format(
              "first sample is not before second sample (%s !< %s)",
              first.timestamp, second.timestamp));
    }
    return new EnergyInterval(
        first.timestamp,
        second.timestamp,
        IntStream.range(0, MicroArchitecture.SOCKETS)
            .mapToObj(
                socket -> difference(first.getReadings()[socket], second.getReadings()[socket]))
            .toArray(EnergyReading[]::new));
  }

  private static double diffWithWraparound(double first, double second) {
    double energy = second - first;
    if (energy < 0) {
      energy += WRAP_AROUND;
    }
    return energy;
  }

  private static double diffWithDramWraparound(double first, double second) {
    double energy = second - first;
    if (energy < 0) {
      energy += DRAM_WRAP_AROUND;
    }
    return energy;
  }

  private static HashMap<String, Integer> getComponents() {
    HashMap<String, Integer> components = new HashMap<>();
    // TODO -- there's a 5th possible power domain, right? like full motherboard energy or something
    int index = 0;
    for (String component : components().split(",")) {
      components.put(component, index++);
    }
    return components;
  }

  /**
   * Returns the energy of each component and the current timestamp as a delimited string. The
   * energy values are floating point numbers representing the number of joules since the last boot.
   * The order will be each socket's {@code components} followed by the microsecond timestamp:
   *
   * <p>socket0_component0,socket0_component1,socket1_component0,socket1_component1,...,timestamp
   */
  private static native String readNative();

  /** Returns the available components as a string ("dram,core,pkg"/"dram,core,gpu,pkg"/etc). */
  private static native String components();

  private static native int wrapAround();

  private static native int dramWrapAround();

  static {
    if (NativeLibrary.initialize()) {
      WRAP_AROUND = wrapAround();
      DRAM_WRAP_AROUND = dramWrapAround();
      COMPONENTS = getComponents();
    } else {
      LoggerUtil.LOGGER.warning(
          "native library couldn't be initialized; rapl likely not available");
      WRAP_AROUND = 0;
      DRAM_WRAP_AROUND = 0;
      COMPONENTS = new HashMap<>();
    }
  }

  private Rapl() {}
}
