package roomescape.controller.api;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.controller.dto.request.CreateReservationRequest;
import roomescape.controller.dto.request.CreateThemeRequest;
import roomescape.controller.dto.request.CreateUserReservationRequest;
import roomescape.controller.dto.request.CreateUserReservationStandbyRequest;
import roomescape.controller.dto.request.LoginRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.payment.Payment;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.service.PaymentService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;
import roomescape.service.UserReservationService;
import roomescape.service.dto.PaymentRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class UserReservationControllerTest {
    private static final Long USER_ID = 1L;
    private static final Long ANOTHER_USER_ID = 2L;
    private static final Long TIME_ID = 1L;
    private static final Long THEME_ID = 1L;
    private static final Long PAYMENT_ID = 1L;
    private static final LocalDate DATE_FIRST = LocalDate.parse("2060-01-01");
    private static final LocalDate DATE_SECOND = LocalDate.parse("2060-01-02");

    @LocalServerPort
    int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Autowired
    private UserReservationService userReservationService;

    @MockBean
    private PaymentService paymentService;

    private String userToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        reservationTimeService.save("10:00");
        themeService.save(new CreateThemeRequest("t1", "설명1", "https://test.com/test.jpg"));
        memberRepository.save(new Member("러너덕", "user@a.com", "123a!", Role.USER));
        memberRepository.save(new Member("트레", "tre@a.com", "123a!", Role.USER));
        Payment payment = new Payment("1", 1000, "1");
        payment = paymentRepository.save(payment);
        when(paymentService.pay(any(PaymentRequest.class)))
                .thenReturn(payment);


        LoginRequest user = new LoginRequest("user@a.com", "123a!");

        userToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/login")
                .then().extract().cookie("token");
    }

    @DisplayName("성공: 예약 저장 -> 201")
    @Test
    void save() {
        CreateUserReservationRequest request = new CreateUserReservationRequest(
                DATE_FIRST, THEME_ID, TIME_ID, "1", "1", 1000, "1");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", userToken)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("id", is(1))
                .body("memberName", is("러너덕"))
                .body("date", is("2060-01-01"))
                .body("time", is("10:00"))
                .body("themeName", is("t1"));
    }

    @DisplayName("성공: 예약대기 추가 -> 201")
    @Test
    void standby() {
        userReservationService.reserve(new CreateReservationRequest(ANOTHER_USER_ID, DATE_FIRST, TIME_ID, THEME_ID), PAYMENT_ID);

        CreateUserReservationStandbyRequest request = new CreateUserReservationStandbyRequest(
                DATE_FIRST, THEME_ID, TIME_ID);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", userToken)
                .body(request)
                .when().post("/reservations/standby")
                .then().log().all()
                .statusCode(201)
                .body("id", is(2))
                .body("memberName", is("러너덕"))
                .body("date", is("2060-01-01"))
                .body("time", is("10:00"))
                .body("themeName", is("t1"));
    }

    @DisplayName("성공: 예약대기 삭제 -> 204")
    @Test
    void deleteStandby() {
        userReservationService.reserve(new CreateReservationRequest(ANOTHER_USER_ID, DATE_FIRST, TIME_ID, THEME_ID), PAYMENT_ID);
        userReservationService.standby(USER_ID, new CreateUserReservationStandbyRequest(DATE_FIRST, TIME_ID, THEME_ID));

        RestAssured.given().log().all()
                .cookie("token", userToken)
                .when().delete("/reservations/standby/2")
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("실패: 다른 사람의 예약대기 삭제 -> 400")
    @Test
    void deleteStandby_ReservedByOther() {
        userReservationService.reserve(new CreateReservationRequest(USER_ID, DATE_FIRST, TIME_ID, THEME_ID), PAYMENT_ID);
        userReservationService.standby(ANOTHER_USER_ID, new CreateUserReservationStandbyRequest(DATE_FIRST, TIME_ID, THEME_ID));

        RestAssured.given().log().all()
                .cookie("token", userToken)
                .when().delete("/reservations/standby/2")
                .then().log().all()
                .statusCode(400)
                .body("message", is("자신의 예약만 삭제할 수 있습니다."));
    }

    @DisplayName("실패: 존재하지 않는 time id 예약 -> 400")
    @Test
    void save_TimeIdNotFound() {
        CreateUserReservationRequest request = new CreateUserReservationRequest(
                DATE_FIRST, THEME_ID, 2L, "1", "1", 1000, "1");

        RestAssured.given().log().all()
                .cookie("token", userToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("message", is("입력한 시간 ID에 해당하는 데이터가 존재하지 않습니다."));
    }

    @DisplayName("실패: 존재하지 않는 theme id 예약 -> 400")
    @Test
    void save_ThemeIdNotFound() {
        CreateUserReservationRequest request = new CreateUserReservationRequest(
                DATE_FIRST, 2L, TIME_ID, "1", "1", 1000, "1");

        RestAssured.given().log().all()
                .cookie("token", userToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("message", is("입력한 테마 ID에 해당하는 데이터가 존재하지 않습니다."));
    }

    @DisplayName("실패: 중복 예약 -> 400")
    @Test
    void save_Duplication() {
        userReservationService.reserve(new CreateReservationRequest(ANOTHER_USER_ID, DATE_FIRST, TIME_ID, THEME_ID), PAYMENT_ID);

        CreateUserReservationRequest request
                = new CreateUserReservationRequest(DATE_FIRST, THEME_ID, TIME_ID, "123", "123", 1000, "123");

        RestAssured.given().log().all()
                .cookie("token", userToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("message", is("해당 시간에 예약이 이미 존재합니다."));
    }

    @DisplayName("실패: 과거 시간 예약 -> 400")
    @Test
    void save_PastTime() {
        CreateUserReservationRequest request = new CreateUserReservationRequest(
                LocalDate.now().minusDays(1), THEME_ID, TIME_ID, "1", "1", 1000, "1");

        RestAssured.given().log().all()
                .cookie("token", userToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("message", is("[date](은)는 과거 날짜로는 예약할 수 없습니다."));
    }

    @DisplayName("성공: 나의 예약 목록 조회 -> 200")
    @Test
    void findMyReservations() {
        userReservationService.reserve(new CreateReservationRequest(USER_ID, DATE_FIRST, TIME_ID, THEME_ID), PAYMENT_ID);
        userReservationService.reserve(new CreateReservationRequest(ANOTHER_USER_ID, DATE_SECOND, TIME_ID, THEME_ID), PAYMENT_ID);
        userReservationService.standby(USER_ID, new CreateUserReservationStandbyRequest(DATE_SECOND, TIME_ID, THEME_ID));

        RestAssured.given().log().all()
                .cookie("token", userToken)
                .contentType(ContentType.JSON)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("id", contains(1, 3))
                .body("status", contains("PAYMENT_RESERVED", "STANDBY"))
                .body("rank", contains(0, 1));
    }
}
