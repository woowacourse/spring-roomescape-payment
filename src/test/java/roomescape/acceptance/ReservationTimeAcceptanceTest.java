package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import static roomescape.fixture.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.fixture.TestFixture.START_AT_SIX;
import static roomescape.fixture.ThemeFixture.themeFixture;
import static roomescape.fixture.TimeFixture.TIMES;
import static roomescape.fixture.TimeFixture.timeFixture;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.dto.reservation.AvailableReservationTimeResponse;
import roomescape.dto.reservation.ReservationTimeResponse;
import roomescape.dto.reservation.ReservationTimeSaveRequest;

class ReservationTimeAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("예약 시간을 성공적으로 생성하면 201을 응답한다.")
    void respondCreatedWhenCreateReservationTime() {
        final var request = new ReservationTimeSaveRequest(START_AT_SIX);

        var response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .extract().as(ReservationTimeResponse.class);

        assertThat(response.startAt()).isEqualTo(START_AT_SIX);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "13-00"})
    @DisplayName("잘못된 형식으로 예약 시간 생성 시 400을 응답한다.")
    void respondBadRequestWhenCreateInvalidReservationTime(final String invalidTime) {
        final var request = new ReservationTimeSaveRequest(invalidTime);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 시간 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindReservationTimes() {
        TIMES.forEach(this::saveTime);
        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .extract().as(new TypeRef<List<ReservationTimeResponse>>() {});
    }

    @Test
    @DisplayName("예약 시간을 성공적으로 삭제하면 204를 응답한다.")
    void respondNoContentWhenDeleteReservationTime() {
        ReservationTime time = saveTime(timeFixture(1L));

        RestAssured.given().log().all()
                .when().delete("/times/" + time.getId())
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간을 삭제하면 404를 응답한다.")
    void respondBadRequestWhenDeleteNotExistingReservationTime() {
        RestAssured.given().log().all()
                .when().delete("/times/1")
                .then().log().all()
                .statusCode(404);
    }

    @Test
    @DisplayName("예약 가능한 시간 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindAvailableReservationTimes() {
        TIMES.forEach(this::saveTime);
        final String date = DATE_MAY_EIGHTH.toString();
        final Theme theme = saveTheme(themeFixture(1L));

        final var responses = RestAssured.given().log().all()
                .queryParam("date", date)
                .queryParam("themeId", theme.getId())
                .when().get("/times/available")
                .then().log().all()
                .statusCode(200)
                .extract().as(new TypeRef<List<AvailableReservationTimeResponse>>() {});

        assertThat(responses).hasSize(TIMES.size());
    }
}
