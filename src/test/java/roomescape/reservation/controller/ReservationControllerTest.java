package roomescape.reservation.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import roomescape.auth.controller.dto.SignUpRequest;
import roomescape.auth.service.TokenProvider;
import roomescape.member.service.MemberService;
import roomescape.payment.infra.PaymentClient;
import roomescape.payment.dto.PaymentResponse;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.service.ReservationRegister;
import roomescape.reservation.service.ReservationTimeService;
import roomescape.reservation.service.ThemeService;
import roomescape.util.ControllerTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static roomescape.fixture.MemberFixture.getMemberChoco;
import static roomescape.fixture.MemberFixture.getMemberClover;

@DisplayName("예약 API 통합 테스트")
class ReservationControllerTest extends ControllerTest {
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

    @SpyBean
    PaymentClient paymentClient;

    String token;

    @BeforeEach
    void beforeEach() {
        token = tokenProvider.createAccessToken(getMemberChoco().getEmail());
    }

    @DisplayName("사용자 예약 생성 시 201을 반환한다.")
    @Test
    void create() {
        //given
        PaymentResponse paymentResponse = new PaymentResponse(
                "test",
                "test",
                1000L,
                "test",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "DONE"
                );
        BDDMockito.doReturn(paymentResponse)
                .when(paymentClient)
                .confirm(any());

        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(
                new ReservationTimeRequest("10:00"));
        ThemeResponse themeResponse = themeService.create(new ThemeRequest("name", "description", "thumbnail"));

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", "2099-08-05");
        reservation.put("timeId", reservationTimeResponse.id());
        reservation.put("themeId", themeResponse.id());
        reservation.put("paymentKey", "test");
        reservation.put("orderId", "test");
        reservation.put("amount", 1000L);
        reservation.put("paymentType", "test");

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("예약을 삭제한다.")
    @TestFactory
    Stream<DynamicTest> delete() {
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(
                new ReservationTimeRequest("11:00"));
        ThemeResponse themeResponse = themeService.create(new ThemeRequest("name", "description", "thumbnail"));

        ReservationResponse reservationResponse = reservationRegister.createReservation(
                new ReservationRequest(
                        LocalDate.now().plusDays(10).toString(),
                        reservationTimeResponse.id(),
                        themeResponse.id()),
                getMemberChoco().getId(), ReservationStatus.BOOKED
        );

        return Stream.of(
                dynamicTest("타인의 예약 삭제 시, 403을 반환한다.", () -> {
                    //given
                    memberService.create(
                            new SignUpRequest(getMemberClover().getName(), getMemberClover().getEmail(), "qwer"));

                    String cloverToken = tokenProvider.createAccessToken(getMemberClover().getEmail());

                    RestAssured.given().log().all()
                            .cookie("token", cloverToken)
                            .when().delete("/reservations/" + reservationResponse.reservationId())
                            .then().log().all()
                            .statusCode(403);
                }),
                dynamicTest("예약 삭제 시 204를 반환한다.", () -> {
                    //given
                    RestAssured.given().log().all()
                            .cookie("token", token)
                            .when().delete("/reservations/" + reservationResponse.reservationId())
                            .then().log().all()
                            .statusCode(204);
                })
        );
    }

    @DisplayName("예약 조회 시 200을 반환한다.")
    @Test
    void find() {
        //given & when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("예약 생성 시, 잘못된 날짜 형식에 대해 400을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "20-12-31", "2020-1-30", "2020-11-0", "-1"})
    void createBadRequest(String date) {
        //given
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(
                new ReservationTimeRequest("11:00"));
        ThemeResponse themeResponse = themeService.create(new ThemeRequest("name", "description", "thumbnail"));

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", date);
        reservation.put("timeId", reservationTimeResponse.id());
        reservation.put("themeId", themeResponse.id());

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("지나간 날짜와 시간에 대한 예약 생성 시, 400을 반환한다.")
    @Test
    void createReservationAfterNow() {
        //given
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(
                new ReservationTimeRequest("11:00"));
        ThemeResponse themeResponse = themeService.create(new ThemeRequest("name", "description", "thumbnail"));

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", LocalDate.now().minusDays(2).toString());
        reservation.put("timeId", reservationTimeResponse.id());
        reservation.put("themeId", themeResponse.id());

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("내 예약 조회 페이지 조회 시 200을 반환한다.")
    @Test
    void getMyReservationPage() {
        //given

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @DisplayName("예약 추가 시, 내 예약 개수가 늘어난다")
    @Test
    void reservationAndGetMyReservation() {
        //given
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        //when
        PaymentResponse paymentResponse = new PaymentResponse(
                "test",
                "test",
                1000L,
                "test",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "DONE"
        );
        BDDMockito.doReturn(paymentResponse)
                .when(paymentClient)
                .confirm(any());

        ReservationTimeResponse reservationTimeResponse = reservationTimeService.create(
                new ReservationTimeRequest("10:00"));
        ThemeResponse themeResponse = themeService.create(new ThemeRequest("name", "description", "thumbnail"));

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", "2099-08-05");
        reservation.put("timeId", reservationTimeResponse.id());
        reservation.put("themeId", themeResponse.id());
        reservation.put("paymentKey", "test");
        reservation.put("orderId", "test");
        reservation.put("amount", 1000L);
        reservation.put("paymentType", "test");

        //when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);

        //then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

}
