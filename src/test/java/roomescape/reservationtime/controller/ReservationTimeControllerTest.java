package roomescape.reservationtime.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createAdminMember;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultTheme;
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
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.domain.Theme;

class ReservationTimeControllerTest extends IntegrationTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void 모든_예약_시간_조회() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        dbHelper.insertTime(createTimeAt(LocalTime.of(11, 0)));

        // when & then
        List<ReservationTimeResponse> responses = givenWithDocs("reservationTime-get")
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
    void 예약가능시간여부_포함_예약시간_모두_조회() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        ReservationTime time1 = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        ReservationTime time2 = dbHelper.insertTime(createTimeAt(LocalTime.of(11, 0)));
        Theme theme = dbHelper.insertTheme(createDefaultTheme());

        // when & then
        List<AvailableReservationTimeResponse> responses =RestAssured.given(documentationSpec)
                .filter(document("reservationTime-available-get",
                        queryParameters(
                                parameterWithName("themeId").description("테마 ID"),
                                parameterWithName("date").description("예약 날짜")
                        )
                ))
                .cookie("token", token)
                .when().get("/times/available?date=" + DEFAULT_DATE + "&themeId=" + theme.getId())
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", AvailableReservationTimeResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(responses).hasSize(2);
            assertThat(responses)
                    .extracting("startAt")
                    .containsExactly(
                            LocalTime.of(10, 0),
                            LocalTime.of(11, 0)
                    );
            assertThat(responses)
                    .extracting("alreadyBooked")
                    .containsExactly(false, false);
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
        ReservationTimeResponse timeResponse = givenWithDocs("reservationTime-create")
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
        givenWithDocs("reservationTime-delete")
                .cookie("token", token)
                .when().delete("/times/" + reservationTime.getId())
                .then().log().all()
                .statusCode(204);
    }
}
