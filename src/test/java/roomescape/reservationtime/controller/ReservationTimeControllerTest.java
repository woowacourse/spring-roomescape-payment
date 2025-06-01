package roomescape.reservationtime.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createAdminMember;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;

class ReservationTimeControllerTest extends IntegrationTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void 예약_시간_조회() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        dbHelper.insertTime(createTimeAt(LocalTime.of(11, 0)));

        // when & then
        List<ReservationTimeResponse> responses = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", ReservationTimeResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(responses).hasSize(2);
            assertThat(responses)
                    .extracting("startAt")
                    .containsExactly(
                            LocalTime.of(10, 0),
                            LocalTime.of(11, 0)
                    );
        });
    }


    @Test
    void 예약_시간_저장() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        ReservationTimeRequest timeRequest = new ReservationTimeRequest(LocalTime.of(10, 0));

        // when & then
        ReservationTimeResponse timeResponse = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(timeRequest)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .extract().as(ReservationTimeResponse.class);

        assertThat(timeResponse.startAt()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    void 예약_시간_삭제() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        ReservationTime reservationTime = createTimeAt(LocalTime.of(10, 0));
        dbHelper.insertTime(reservationTime);

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/times/" + reservationTime.getId())
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void 예약_시간이_포함된_예약이_있다면_삭제시도_시_예외_발생() {
         // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        ReservationTime reservationTime = createTimeAt(LocalTime.of(10, 0));
        dbHelper.insertTime(reservationTime);

        dbHelper.insertReservation(createReservationOf(adminMember, DEFAULT_DATE, reservationTime, createDefaultTheme()));

        // when & the
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/times/" + reservationTime.getId())
                .then().log().all()
                .statusCode(400)
                .body("detail", equalTo("해당 시간으로 예약된 건이 존재합니다."));
    }
}
