package roomescape.acceptance;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.BasicAcceptanceTest;
import roomescape.dto.response.reservation.AvailableTimeResponse;
import roomescape.dto.request.reservation.ReservationTimeRequest;

class ReservationTimeAcceptanceTest extends BasicAcceptanceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String adminToken;

    @BeforeEach
    void SetUp() {
        jdbcTemplate.update(
                "INSERT INTO member (name, email, password, role) VALUES ('회원', 'member@wooteco.com', 'wootecoCrew6!', 'BASIC')");
        jdbcTemplate.update(
                "INSERT INTO member (name, email, password, role) VALUES ('운영자', 'admin@wooteco.com', 'wootecoCrew6!', 'ADMIN')");
        adminToken = LoginTokenProvider.login("admin@wooteco.com", "wootecoCrew6!", 200);
    }

    @TestFactory
    @DisplayName("동일한 예약 시간을 두번 추가하면, 예외가 발생한다")
    Stream<DynamicTest> duplicateReservationTest() {
        return Stream.of(
                dynamicTest("예약 시간을 추가한다 (10:00)", () -> postReservationTime(adminToken, "01:00", 201)),
                dynamicTest("동일한 예약 시간을 추가한다 (10:00)", () -> postReservationTime(adminToken, "01:00", 409))
        );
    }

    @TestFactory
    @DisplayName("3개의 예약 시간을 추가한다")
    Stream<DynamicTest> reservationPostTest() {
        return Stream.of(
                dynamicTest("예약 시간을 추가한다 (09:00)", () -> postReservationTime(adminToken, "01:00", 201)),
                dynamicTest("예약 시간을 추가한다 (10:00)", () -> postReservationTime(adminToken, "02:00", 201)),
                dynamicTest("예약 시간을 추가한다 (11:00)", () -> postReservationTime(adminToken, "03:00", 201)),
                dynamicTest("모든 예약 시간을 조회한다 (총 3개)", () -> getReservationTimes(200, 3))
        );
    }

    @TestFactory
    @DisplayName("예약 시간을 추가하고 삭제한다")
    Stream<DynamicTest> reservationPostAndDeleteTest() {
        AtomicLong reservationTimeId = new AtomicLong();
        return Stream.of(
                dynamicTest("예약 시간을 추가한다 (01:00)", () -> reservationTimeId.set(postReservationTime(adminToken, "01:00", 201))),
                dynamicTest("예약 시간을 삭제한다 (01:00)", () -> deleteReservationTime(adminToken, reservationTimeId.longValue(), 204)),
                dynamicTest("예약 시간을 추가한다 (01:00)", () -> postReservationTime(adminToken, "01:00", 201)),
                dynamicTest("모든 예약 시간을 조회한다 (총 1개)", () -> getReservationTimes(200, 1))
        );
    }

    @DisplayName("예약이 가능한 시간을 반환한다.")
    @Test
    void reservationAvailableTimes() {
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('11:00')");
        jdbcTemplate.update("INSERT INTO theme (name, description, thumbnail, price) VALUES ('name1', 'description1', 'thumbnail1', 1000)");
        jdbcTemplate.update("INSERT INTO theme (name, description, thumbnail, price) VALUES ('name2', 'description2', 'thumbnail2', 1000)");
        jdbcTemplate.update("INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES ('2099-04-29', 1, 1, 1, 'RESERVATION')");

        getAvailableTimes(200, 1);
    }

    private Long postReservationTime(String token, String time, int expectedHttpCode) {
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.parse(time));

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies("token", token)
                .body(reservationTimeRequest)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(expectedHttpCode)
                .extract().response();

        if (expectedHttpCode == 201) {
            return response.jsonPath().getLong("id");
        }

        return null;
    }

    private void getReservationTimes(int expectedHttpCode, int expectedReservationTimesSize) {
        Response response = RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(expectedHttpCode)
                .extract().response();

        List<?> reservationTimeResponses = response.as(List.class);

        assertThat(reservationTimeResponses).hasSize(expectedReservationTimesSize);
    }

    private void deleteReservationTime(String token, Long reservationTimeId, int expectedHttpCode) {
        RestAssured.given().log().all()
                .cookies("token", token)
                .when().delete("/admin/times/" + reservationTimeId)
                .then().log().all()
                .statusCode(expectedHttpCode);
    }

    private void getAvailableTimes(int expectedHttpCode, int expectedAlreadyBookedSize) {
        Response response = RestAssured.given().log().all()
                .when().get("/times/available?date=2099-04-29&themeId=1")
                .then().log().all()
                .statusCode(expectedHttpCode)
                .extract().response();

        List<AvailableTimeResponse> reservationTimeResponses = response.jsonPath().getList(".", AvailableTimeResponse.class);

        List<AvailableTimeResponse> list = reservationTimeResponses.stream()
                .filter(AvailableTimeResponse::isBooked)
                .toList();

        assertThat(list).hasSize(expectedAlreadyBookedSize);
    }
}
