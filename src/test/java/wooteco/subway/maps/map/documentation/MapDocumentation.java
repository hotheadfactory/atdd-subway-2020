package wooteco.subway.maps.map.documentation;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.context.WebApplicationContext;
import wooteco.security.core.TokenResponse;
import wooteco.subway.common.documentation.Documentation;
import wooteco.subway.maps.line.dto.LineResponse;
import wooteco.subway.maps.line.dto.LineStationResponse;
import wooteco.subway.maps.map.application.MapService;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.dto.MapResponse;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.map.ui.MapController;
import wooteco.subway.maps.station.dto.StationResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@WebMvcTest(controllers = {MapController.class})
public class MapDocumentation extends Documentation {
    protected TokenResponse tokenResponse;
    @Autowired
    MapController mapController;
    @MockBean
    MapService mapService;

    @BeforeEach
    public void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        super.setUp(context, restDocumentation);
        tokenResponse = new TokenResponse("token");
    }

    @Test
    void findPath() {
        Map<String, Object> params = new HashMap<>();
        params.put("source", 1L);
        params.put("target", 3L);
        params.put("type", PathType.DISTANCE);

        List<StationResponse> stationResponses = Lists.newArrayList(
                new StationResponse(1L, "복정역", LocalDateTime.now(), LocalDateTime.now()),
                new StationResponse(3L, "잠실역", LocalDateTime.now(), LocalDateTime.now())
        );
        when(mapService.findPath(1L, 3L, PathType.DISTANCE, 20))
                .thenReturn(new PathResponse(stationResponses, 6, 2, 1550));

        given().log().all().
                header("Authorization", "Bearer " + tokenResponse.getAccessToken()).
                accept(MediaType.APPLICATION_JSON_VALUE).
                params(params).
                when().
                get("/paths").
                then().
                log().all().
                apply(document("maps/findPath",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer auth credentials")),
                        requestFields(
                                fieldWithPath("source").type(JsonFieldType.NUMBER).description("출발역 아이디"),
                                fieldWithPath("target").type(JsonFieldType.NUMBER).description("도착역 아이디"),
                                fieldWithPath("type").type(JsonFieldType.STRING).description("검색 타입 (최단거리 / 최소시간)")
                        ),
                        responseFields(
                                fieldWithPath("duration").type(JsonFieldType.NUMBER).description("소요 시간"),
                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("총 거리"),
                                fieldWithPath("fare").type(JsonFieldType.NUMBER).description("구간 요금"),
                                fieldWithPath("stations[]").type(JsonFieldType.ARRAY).description("경로에 있는 역 목록"),
                                fieldWithPath("stations[].id").type(JsonFieldType.NUMBER).description("경로에 있는 역 아이디"),
                                fieldWithPath("stations[].name").type(JsonFieldType.STRING).description("경로에 있는 역 이름")))).
                extract();
    }

    @Test
    void getMaps() {
        List<LineStationResponse> lineStationResponses = Lists.newArrayList(
                new LineStationResponse(new StationResponse(1L, "삼성역", LocalDateTime.now(), LocalDateTime.now()), null, 1L, 5, 5)
        );

        List<LineResponse> lineResponses = Lists.newArrayList(
                new LineResponse(1L, "2호선", "green lighten-1", LocalTime.now(), LocalTime.now(), 10, lineStationResponses, LocalDateTime.now(), LocalDateTime.now(), 100),
                new LineResponse(2L, "신분당선", "red lighten-1", LocalTime.now(), LocalTime.now(), 10, lineStationResponses, LocalDateTime.now(), LocalDateTime.now(), 200),
                new LineResponse(3L, "3호선", "orange darken-1", LocalTime.now(), LocalTime.now(), 10, lineStationResponses, LocalDateTime.now(), LocalDateTime.now(), 300)
        );
        when(mapService.findMap()).thenReturn(new MapResponse(lineResponses));

        given().log().all().
                header("authorization", "Bearer " + tokenResponse.getAccessToken()).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/maps").
                then().
                log().all().
                apply(document("maps/findMaps",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer auth credentials")),
                        responseFields(
                                fieldWithPath("lineResponses[]").type(JsonFieldType.ARRAY).description("노선 목록"),
                                fieldWithPath("lineResponses[].id").type(JsonFieldType.NUMBER).description("노선 아이디"),
                                fieldWithPath("lineResponses[].name").type(JsonFieldType.STRING).description("노선 이름"),
                                fieldWithPath("lineResponses[].color").type(JsonFieldType.STRING).description("노선 색상"),
                                fieldWithPath("lineResponses[].startTime").type(JsonFieldType.STRING).description("노선 시작 시각"),
                                fieldWithPath("lineResponses[].endTime").type(JsonFieldType.STRING).description("노선 종료 시각"),
                                fieldWithPath("lineResponses[].intervalTime").type(JsonFieldType.NUMBER).description("노선 운행 간격"),
                                fieldWithPath("lineResponses[].stations[]").type(JsonFieldType.ARRAY).description("노선 구간 목록"),
                                fieldWithPath("lineResponses[].stations[].station").type(JsonFieldType.OBJECT).description("구간 역 정보"),
                                fieldWithPath("lineResponses[].stations[].station.id").type(JsonFieldType.NUMBER).description("구간 역 아이디"),
                                fieldWithPath("lineResponses[].stations[].station.name").type(JsonFieldType.STRING).description("구간 역 이름"),
                                fieldWithPath("lineResponses[].stations[].preStationId").type(JsonFieldType.NUMBER).description("구간 이전 역 아이디"),
                                fieldWithPath("lineResponses[].stations[].lineId").type(JsonFieldType.NUMBER).description("구간 노선 아이디"),
                                fieldWithPath("lineResponses[].stations[].distance").type(JsonFieldType.NUMBER).description("구간 간 거리"),
                                fieldWithPath("lineResponses[].stations[].duration").type(JsonFieldType.NUMBER).description("구간 간 소요 시간"),
                                fieldWithPath("lineResponses[].createdDate").type(JsonFieldType.STRING).description("생성 시각"),
                                fieldWithPath("lineResponses[].modifiedDate").type(JsonFieldType.STRING).description("수정 시각")))).
                extract();
    }
}
