package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final String INFILE = "Activities.txt";
    private static final String OUTFILE = "Stats.txt";
    private static final Path INPATH = Paths.get(INFILE);

    private List<MonitoredData> data;
    private int numberOfDays;
    private Map<String, Integer> numberOfEachActivity;
    private Map<String, Map<String, Integer>> numberOfEachActivityPerDay;
    private List<String> eachActivityDuration;
    private Map<String, Integer> activitiesDuration;
    private List<String> filteredActivities;

    private List<MonitoredData> generateList() {
        try {
            return Files.lines(INPATH)
                    .map(s -> s.split("\t\t"))
                    .map(s -> {
                        s[0] = s[0].replace(" ", "T");
                        s[1] = s[1].replace(" ", "T");
                        MonitoredData d = new MonitoredData(LocalDateTime.parse(s[0].replace(" ", "T")),
                                LocalDateTime.parse(s[1].replace(" ", "T")), s[2].trim());
                        return d;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getNumberOfDays() {
        return data.stream()
                .map(MonitoredData::getDate)
                .collect(Collectors.toSet())
                .size();
    }

    private Map<String, Integer> getNumberOfEachActivity() {
        return data.stream()
                .collect(
                        Collectors.groupingBy(
                                MonitoredData::getActivityLabel,
                                Collectors.collectingAndThen(
                                        Collectors.mapping(MonitoredData::getActivityLabel,
                                                            Collectors.counting()),
                                                            Long::intValue
                                )
                        )
                );
    }

    private Map<String, Map<String, Integer>> getNumberOfEachActivityPerDay() {
        return data.stream()
                .collect(
                        Collectors.groupingBy(
                                MonitoredData::getDate,
                                Collectors.groupingBy(
                                        MonitoredData::getActivityLabel,
                                        Collectors.collectingAndThen(
                                                Collectors.mapping(MonitoredData::getActivityLabel,
                                                                    Collectors.counting()), Long::intValue
                                        )
                                )
                        )
                );
    }

    private List<String> getEachActivityDuration() {
        return data.stream()
                .map(e -> "\n" + e.toString() + "\nDuration: " + e.getDuration() + " min")
                .collect(Collectors.toList());
    }

    private Map<String, Integer> getActivitiesDuration() {
        return data.stream()
                .collect(
                        Collectors.groupingBy(
                                MonitoredData::getActivityLabel,
                                Collectors.collectingAndThen(
                                        Collectors.mapping(
                                                MonitoredData::getDuration, Collectors.summingInt(Long::intValue)
                                        ), Integer::intValue
                                )
                        )
                );
    }

    private List<String> filterActivities() {
        return data.stream()
                .collect(
                        Collectors.groupingBy(
                                MonitoredData::getActivityLabel,
                                Collectors.toSet()
                        )
                )
                .entrySet()
                .stream()
                .map(e -> {
                    int maxFive = 0;
                    for (MonitoredData m : e.getValue()) {
                        if (m.getDuration() < 5) maxFive++;
                    }

                    if (maxFive >= (double)e.getValue().size() * 90 / 100) {
                        return e.getKey();
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void generateStatisticsFile() {
        File outFile = new File(OUTFILE);

        try {
            FileWriter writer = new FileWriter(outFile);

            StringBuilder sb = new StringBuilder();
            sb.append("~~~~~~~~~~ NUMBER OF MONITORED DAYS ~~~~~~~~~~");
            sb.append("\n" + numberOfDays);
            writer.write(sb.toString() + "\n\n");

            sb.setLength(0);
            sb.append("~~~~~~~~~~ NUMBER OF EACH ACTIVITY ~~~~~~~~~~");
            for (String activity : numberOfEachActivity.keySet()) {
                sb.append("\n" + activity + ": " + numberOfEachActivity.get(activity));
            }
            sb.append("\n\n");
            writer.write(sb.toString());

            sb.setLength(0);
            sb.append("~~~~~~~~~~ NUMBER OF EACH ACTIVITY/DAY ~~~~~~~~~~");
            for (String date : numberOfEachActivityPerDay.keySet()) {
                sb.append("\n\n\t\t\t" + date);
                for (String activity : numberOfEachActivityPerDay.get(date).keySet()) {
                    sb.append("\n" + activity + ": " + numberOfEachActivityPerDay.get(date).get(activity));
                }
            }
            sb.append("\n\n");
            writer.write(sb.toString());

            sb.setLength(0);
            sb.append("~~~~~~~~~~ DURATION OF EACH ACTIVITY LABEL ~~~~~~~~~~");
            for (String activity: eachActivityDuration) {
                sb.append("\n" + activity);
            }
            sb.append("\n\n");
            writer.write(sb.toString());

            sb.setLength(0);
            sb.append("~~~~~~~~~~ DURATION OF EACH ACTIVITY OVER THE MONITORING PERIOD~~~~~~~~~~");
            for(String activity: activitiesDuration.keySet()) {
                sb.append("\n" + activity + ": " + activitiesDuration.get(activity));
            }
            sb.append("\n\n");
            writer.write(sb.toString());

            sb.setLength(0);
            sb.append("~~~~~~~~~~ FILTERED ACTIVITIES ~~~~~~~~~~");
            for(String activity: filteredActivities) {
                sb.append("\n" + activity);
            }
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main m = new Main();

        m.data = m.generateList();
        m.numberOfDays = m.getNumberOfDays();
        m.numberOfEachActivity = m.getNumberOfEachActivity();
        m.numberOfEachActivityPerDay = m.getNumberOfEachActivityPerDay();
        m.eachActivityDuration = m.getEachActivityDuration();
        m.activitiesDuration = m.getActivitiesDuration();
        m.filteredActivities = m.filterActivities();
        m.generateStatisticsFile();
    }
}
