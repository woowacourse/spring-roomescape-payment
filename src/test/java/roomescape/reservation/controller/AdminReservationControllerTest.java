package roomescape.reservation.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.auth.controller.dto.SignUpRequest;
import roomescape.auth.service.TokenProvider;
import roomescape.member.controller.dto.MemberResponse;
import roomescape.member.service.MemberService;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.service.ReservationRegister;
import roomescape.reservation.service.ReservationTimeService;
import roomescape.reservation.service.ThemeService;
import roomescape.util.ControllerTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static roomescape.fixture.MemberFixture.getMemberAdmin;
import static roomescape.fixture.MemberFixture.getMemberChoco;

class AdminReservationControllerTest extends ControllerTest {

    @Autowired
    ReservationRegister reservationRegister;

    @Autowired
    ReservationTimeService reservationTimeService;

    @Autowired
    ThemeService themeService;

    @Autowired
    MemberService memberService;

    @Autowired
    TokenProvider tokenProvider;

    String token;

    @BeforeEach
    void beforeEach() {
        token = tokenProvider.createAccessToken(getMemberAdmin().getEmail());
    }

    @DisplayName("성공 : 예약 목록 조회에 성공한다.")
    @Test
    void getReservations() {
        //given & when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("성공 : 관리자가 예약을 생성 후, 예약을 삭제한다.")
    @TestFactory
    Stream<DynamicTest> createAndDelete() {
        List<ReservationResponse> responses = new ArrayList<>();

        return Stream.of(
                dynamicTest("관리자 예약 생성 시, 201을 반환한다.", () -> {
                    //given
                    ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(
                            new ReservationTimeRequest("11:00"));
                    ThemeResponse themeResponse = themeService.create(
                            new ThemeRequest("name", "description", "thumbnail"));
                    MemberResponse memberResponse = memberService.create(
                            new SignUpRequest(getMemberChoco().getName(), getMemberChoco().getEmail(), "1234"));

                    Map<String, Object> params = new HashMap<>();
                    params.put("memberId", memberResponse.id());
                    params.put("date", "2099-08-05");
                    params.put("timeId", reservationTimeResponse.id());
                    params.put("themeId", themeResponse.id());

                    //when & then
                    ReservationResponse reservationResponse = RestAssured.given().log().all()
                            .cookie("token", token)
                            .contentType(ContentType.JSON)
                            .body(params)
                            .when().post("/admin/reservations")
                            .then().log().all()
                            .statusCode(201).extract().as(ReservationResponse.class);

                    responses.add(reservationResponse);
                }),
                dynamicTest("관리자 예약 삭제 시, 204를 반환한다.", () -> {
                    //given
                    ReservationResponse reservationResponse = responses.get(0);

                    //when &then
                    RestAssured.given().log().all()
                            .cookie("token", token)
                            .when().delete("/admin/reservations/" + reservationResponse.reservationId())
                            .then().log().all()
                            .statusCode(204);
                })
        );
    }

    @DisplayName("성공 : 예약 대기 목록을 조회한다.")
    @Test
    void waitingReservation () {
        //given

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/reservations/waiting")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("실패 : 잘못된 id로 삭제 요청시 에러가 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    void deleteByWrongId(Long wrongId) {
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/admin/reservations/" + wrongId)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
