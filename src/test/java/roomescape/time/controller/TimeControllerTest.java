package roomescape.time.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.fixture.MemberFixture.MEMBER_ADMIN;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;
import static roomescape.fixture.TimeFixture.TIME_2;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.model.SpringBootTestBase;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.time.dto.AvailableTimeResponse;
import roomescape.time.dto.TimeCreateRequest;
import roomescape.time.dto.TimeResponse;

class TimeControllerTest extends SpringBootTestBase {

    @DisplayName("시간을 조회, 추가, 삭제 할 수 있다.")
    @Test
    void findCreateDeleteTimes() {
        TimeCreateRequest params = TimeFixture.toTimeCreateRequest(TIME_1);

        // 시간 추가
        TimeResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(201).extract()
                .jsonPath().getObject("", TimeResponse.class);

        // 시간 조회
        List<TimeResponse> timeResponses = RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList("", TimeResponse.class);

        assertThat(timeResponses).containsExactlyInAnyOrder(response);

        // 시간 삭제
        RestAssured.given().log().all()
                .when().delete("/times/" + response.id())
                .then().log().all()
                .statusCode(204);

        // 시간 조회
        timeResponses = RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList("", TimeResponse.class);

        assertThat(timeResponses).isEmpty();
    }

    @DisplayName("예약 가능한 시간 목록을 읽을 수 있다.")
    @Test
    void findAvailableTimes() {
        Cookies cookies = restAssuredTemplate.makeUserCookie(MEMBER_ADMIN);
        LocalDate date = LocalDate.of(2100, 1, 1);
        Long themeId = restAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), cookies).id();
        TimeResponse reservedTimeResponse = restAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1),
                cookies);
        TimeResponse notReservedTimeResponse = restAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_2),
                cookies);

        AdminReservationCreateRequest reservationParams =
                new AdminReservationCreateRequest(MEMBER_ADMIN.getId(), date, reservedTimeResponse.id(), themeId);
        restAssuredTemplate.create(reservationParams, cookies);

        List<AvailableTimeResponse> expected = List.of(
                new AvailableTimeResponse(reservedTimeResponse, true),
                new AvailableTimeResponse(notReservedTimeResponse, false));

        List<AvailableTimeResponse> response = RestAssured.given().log().all()
                .when().get("/times/available?date=2100-01-01&themeId=" + themeId)
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", AvailableTimeResponse.class);

        assertThat(response).isEqualTo(expected);
    }
}
