package wooteco.subway.maps.map.domain;

import org.jgrapht.graph.DefaultWeightedEdge;
import wooteco.subway.maps.line.domain.LineStation;

public class LineStationEdge extends DefaultWeightedEdge {
    private LineStation lineStation;
    private Long lineId;
    private int lineExtraFare;

    public LineStationEdge(LineStation lineStation, Long lineId, int lineExtraFare) {
        this.lineStation = lineStation;
        this.lineId = lineId;
        this.lineExtraFare = lineExtraFare;
    }

    public LineStation getLineStation() {
        return lineStation;
    }

    public Long getLineId() {
        return lineId;
    }

    public int getLineExtraFare() {
        return lineExtraFare;
    }

    @Override
    protected Object getSource() {
        return this.lineStation.getPreStationId();
    }

    @Override
    protected Object getTarget() {
        return this.lineStation.getStationId();
    }

    public Long extractTargetStationId(Long preStationId) {
        if (lineStation.getStationId().equals(preStationId)) {
            return lineStation.getPreStationId();
        } else if (lineStation.getPreStationId().equals(preStationId)) {
            return lineStation.getStationId();
        } else {
            throw new RuntimeException();
        }
    }
}
