package roomescape.api.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import roomescape.api.fixture.DocumentationFixture;
import roomescape.time.dto.CreateReservationTimeRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.dto.TimeAvailabilityResponse;
import roomescape.time.repository.ReservationTimeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(RestDocumentationExtension.class)
@Sql("/test-time-data.sql")
public class ReservationTimeApiTest {

    @LocalServerPort
    private int port;

    private RequestSpecification documentationSpecification;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.documentationSpecification = DocumentationFixture
                .createDefaultDocumentationSpecification(restDocumentation);
    }

    @DisplayName("POST /times : 시간 추가 API 테스트")
    @Test
    void createTime() {
        // given
        CreateReservationTimeRequest request = new CreateReservationTimeRequest(LocalTime.of(9, 0));
        ReservationTimeResponse expectedResponse = new ReservationTimeResponse(4L, LocalTime.of(9, 0));
        // when
        ReservationTimeResponse actualResponse = RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .filter(TimeDocumentationFixture.CREATE_TIME_DOCUMENT)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .extract().as(ReservationTimeResponse.class);
        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("GET /times : 시간 목록 조회 API 테스트")
    @Test
    void findAllTimes() {
        // given
        ReservationTimeResponse expectedResponse = new ReservationTimeResponse(1L, LocalTime.of(10, 0));
        // when
        ReservationTimeResponse[] actualResponse = RestAssured.given(documentationSpecification).log().all()
                .filter(TimeDocumentationFixture.FIND_ALL_TIMES_DOCUMENT)
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .extract().as(ReservationTimeResponse[].class);
        // then
        assertAll(
                () -> assertThat(actualResponse).hasSize(3),
                () -> {
                    assertNotNull(actualResponse);
                    assertThat(actualResponse[0]).isEqualTo(expectedResponse);
                }
        );
    }

    @DisplayName("GET /times/availability : 예약 가능 시간 조회 API 테스트")
    @Test
    @Sql("/test-reservation-availability-data.sql")
    void findAllTimeAvailability() {
        // given
        List<TimeAvailabilityResponse> expectedResponse = List.of(
                new TimeAvailabilityResponse(1L, LocalTime.of(10, 0), true),
                new TimeAvailabilityResponse(2L, LocalTime.of(11, 0), true),
                new TimeAvailabilityResponse(3L, LocalTime.of(12, 0), false)
        );
        String dateQuery = LocalDate.of(2025, 5, 1).format(DateTimeFormatter.ISO_DATE);
        long themeIdQuery = 1L;
        // when
        TimeAvailabilityResponse[] actualResponse = RestAssured.given(documentationSpecification).log().all()
                .queryParam("date", dateQuery)
                .queryParam("themeId", themeIdQuery)
                .contentType(ContentType.JSON)
                .filter(TimeDocumentationFixture.FIND_ALL_TIME_AVAILABILITY_DOCUMENT)
                .when().get("/times/availability")
                .then().log().all()
                .statusCode(200)
                .extract().as(TimeAvailabilityResponse[].class);
        // then
        assertAll(
                () -> assertThat(actualResponse).hasSize(3),
                () -> {
                    assertNotNull(actualResponse);
                    assertThat(Arrays.asList(actualResponse)).isEqualTo(expectedResponse);
                }
        );
    }

    @DisplayName("DELETE /times/{id} : 시간 삭제 API 테스트")
    @Test
    void deleteTimeById() {
        // given
        long timeIdPathParameter = 1L;
        // when
        RestAssured.given(documentationSpecification).log().all()
                .filter(TimeDocumentationFixture.DELETE_TIME_BY_ID_DOCUMENT)
                .when().delete("/times/{id}", timeIdPathParameter)
                .then().log().all()
                .statusCode(204);
        // then
        assertThat(reservationTimeRepository.count()).isEqualTo(2L);
    }
}
