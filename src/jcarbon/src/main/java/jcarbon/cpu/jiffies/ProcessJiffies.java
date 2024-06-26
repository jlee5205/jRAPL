package jcarbon.cpu.jiffies;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jcarbon.cpu.ProcessComponent;
import jcarbon.data.Component;
import jcarbon.data.Interval;

/** An {@link Interval} of task jiffies for a process over a time range. */
public final class ProcessJiffies
    implements Interval<List<TaskJiffies>>, Comparable<ProcessJiffies> {
  public static ProcessJiffies between(ProcessSample first, ProcessSample second) {
    if (first.compareTo(second) > -1) {
      throw new IllegalArgumentException(
          String.format(
              "first sample is not before second sample (%s !< %s)",
              first.timestamp(), second.timestamp()));
    }
    return new ProcessJiffies(
        first.timestamp(),
        second.timestamp(),
        first.processId(),
        difference(first.data(), second.data()));
  }

  private static List<TaskJiffies> difference(List<TaskJiffies> first, List<TaskJiffies> second) {
    Map<Long, TaskJiffies> secondMap = second.stream().collect(toMap(r -> r.taskId, r -> r));
    ArrayList<TaskJiffies> jiffies = new ArrayList<>();
    for (TaskJiffies task : first) {
      if (secondMap.containsKey(task.taskId)) {
        TaskJiffies other = secondMap.get(task.taskId);
        if ((other.userJiffies - task.userJiffies) > 0
            || (other.systemJiffies - task.systemJiffies) > 0) {
          jiffies.add(
              new TaskJiffies(
                  task.taskId,
                  task.processId,
                  task.cpu,
                  Math.max(0, other.userJiffies - task.userJiffies),
                  Math.max(0, other.systemJiffies - task.systemJiffies)));
        }
      }
    }
    return jiffies;
  }

  private final Instant start;
  private final Instant end;
  private final ProcessComponent component;
  private final ArrayList<TaskJiffies> readings = new ArrayList<>();

  ProcessJiffies(Instant start, Instant end, long processId, Iterable<TaskJiffies> readings) {
    this.start = start;
    this.end = end;
    this.component = new ProcessComponent(processId);
    readings.forEach(this.readings::add);
  }

  @Override
  public Instant start() {
    return start;
  }

  @Override
  public Instant end() {
    return end;
  }

  @Override
  public Component component() {
    return component;
  }

  public long processId() {
    return component.processId;
  }

  @Override
  public List<TaskJiffies> data() {
    return new ArrayList<>(readings);
  }

  @Override
  public String toString() {
    // TODO: temporarily using json
    return String.format(
        "{\"start\":{\"seconds\":%d,\"nanos\":%d},\"end\":{\"seconds\":%d,\"nanos\":%d},\"process_id\":%d,\"data\":[%s]}",
        start.getEpochSecond(),
        start.getNano(),
        end.getEpochSecond(),
        end.getNano(),
        component.processId,
        readings.stream().map(TaskJiffies::toString).collect(joining(",")));
  }

  @Override
  public int compareTo(ProcessJiffies other) {
    int start = start().compareTo(other.start());
    if (start < 0) {
      return start;
    } else {
      return end().compareTo(other.end());
    }
  }
}
