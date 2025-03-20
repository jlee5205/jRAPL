package jcarbon.linux.thermal;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

final class SysThermalCooldown {
  private static final Logger logger = getLogger();

  private static class CooldownArgs {
    private final int periodMillis;
    private final int targetTemperature;

    private CooldownArgs(int periodMillis, int targetTemperature) {
      this.periodMillis = periodMillis;
      this.targetTemperature = targetTemperature;
    }
  }

  private static final Integer DEFAULT_PERIOD_MILLIS = 1000;

  private static ArrayList<Integer> searchZones(String type){
    ArrayList<Integer> zoneIds = new ArrayList<>();
    for(int zone: SysThermal.ZONE_COUNT){
      if(SysThermal.getZoneType(zone).equals(type)){
        zoneIds.add(Integer.valueOf(zone));
      }
    }
    return zoneIds;
  } 
  
  public static void zoneCooldown(int zone, int period, int temperature){
    long start = System.currentTimeMillis();

    ArrayList<Integer> samples = new ArrayList<>();

    int k = 10;
    int p = Double.valueOf(0.80 * k).intValue();

    while (true) {
      int temp = SysThermal.getTemperature(zone);
      samples.add(temp);
      logger.info(String.format("Temperature for thermal zone %s is at %s", zone, temp));
      int kCount = samples.size();
      List<Integer> values =
          samples.stream().skip(Math.max(0, samples.size() - 10)).collect(toList());
      long found = values.stream().filter(i -> temperature >= i).count();
      logger.info(
          String.format(
              "%d/%d of the last %d/%d samples (%s) met the threshold of %d",
              found, p, k, kCount, values, temperature));
      System.out.println(kCount == k);
      System.out.println(found >= p);
      if (kCount >= k && found >= p) {
        break;
      }
      Thread.sleep(period);
    }

    long end = System.currentTimeMillis();
    double elapsed = (end - start) / 1000.0;
    logger.info(String.format("Cooling down to %s Celsius took %s seconds", temperature, elapsed));
  }

  private static CooldownArgs getCooldownArgs(String[] args) throws Exception {
    Option periodOption =
        Option.builder("p")
            .hasArg(true)
            .longOpt("period")
            .desc("period in milliseconds to sample at")
            .type(Integer.class)
            .build();
    Option temperatureOption =
        Option.builder("t")
            .hasArg(true)
            .longOpt("temperature")
            .desc("the target temperature in celsius")
            .type(Integer.class)
            .build();
    Options options = new Options().addOption(periodOption).addOption(temperatureOption);
    CommandLine cmd = new DefaultParser().parse(options, args);
    return new CooldownArgs(
        cmd.getParsedOptionValue(periodOption, DEFAULT_PERIOD_MILLIS).intValue(),
        cmd.getParsedOptionValue(temperatureOption).intValue());
  }


  public static void main(String[] args) {
    CooldownArgs args = getCooldownArgs(args);
    // add the cooldown logic
    int period = Integer.valueOf(args.periodMillis);
    int temperature = Integer.valueOf(args.targetTemperature);
    ArrayList<Integer> zoneIds = searchZones("x86_pkg_temp");
    for(Integer id: zoneIds){
      SysThermalCooldown.zoneCooldown(id, period, temperature);
    }
  }
}