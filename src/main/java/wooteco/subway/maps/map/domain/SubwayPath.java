package wooteco.subway.maps.map.domain;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class SubwayPath {
    public static final int BASE_FARE = 1250;
    private List<LineStationEdge> lineStationEdges;
    private int age;

    public SubwayPath(List<LineStationEdge> lineStationEdges, int age) {
        this.lineStationEdges = lineStationEdges;
        this.age = age;
    }

    public List<LineStationEdge> getLineStationEdges() {
        return lineStationEdges;
    }

    public List<Long> extractStationId() {
        List<Long> stationIds = Lists.newArrayList(lineStationEdges.get(0).getLineStation().getPreStationId());
        stationIds.addAll(lineStationEdges.stream()
                .map(it -> it.getLineStation().getStationId())
                .collect(Collectors.toList()));

        return stationIds;
    }

    public int calculateDuration() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDuration()).sum();
    }

    public int calculateDistance() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDistance()).sum();
    }

    public int calculateFare() {
        return calculateAgeDiscount(calculateFareByDistance() + calculateExtraFare());
    }

    private int calculateFareByDistance() {
        int distance = calculateDistance();
        if (distance < 10) {
            return BASE_FARE;
        }
        if (distance < 50) {
            return (int) (BASE_FARE + ((Math.ceil((distance - 10) / 5) + 1) * 100));
        }
        return (int) (BASE_FARE + ((Math.ceil((distance - 50) / 8) + 1) * 100));
    }

    private int calculateExtraFare() {
        return lineStationEdges.stream().mapToInt(LineStationEdge::getLineExtraFare).max().orElse(0);
    }

    private int calculateAgeDiscount(int fare) {
        if (age >= 6 && age < 13) {
            return (int) ((fare - 350) * 0.5);
        }
        if (age >= 13 && age < 19) {
            return (int) ((fare - 350) * 0.8);
        }
        return fare;
    }
}
