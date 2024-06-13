package roomescape.controller.api;

import static org.hamcrest.Matchers.contains;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.controller.dto.request.CreateReservationRequest;
import roomescape.controller.dto.request.CreateThemeRequest;
import roomescape.controller.dto.request.CreateUserReservationStandbyRequest;
import roomescape.controller.dto.request.LoginRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Payment;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.service.AdminReservationService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;
import roomescape.service.UserReservationService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class AdminReservationControllerTest {
    private static final Long ADMIN_ID = 1L;
    private static final Long USER_ID = 2L;
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
    private AdminReservationService adminReservationService;

    @Autowired
    private UserReservationService userReservationService;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        reservationTimeService.save("10:00");
        themeService.save(new CreateThemeRequest("테마1", "설명1", "https://test.com/test.jpg"));
        paymentRepository.save(new Payment("orderId", 1000, "paymenyKey"));

        memberRepository.save(new Member("관리자", "admin@a.com", "123a!", Role.ADMIN));
        memberRepository.save(new Member("사용자", "user@a.com", "123a!", Role.USER));

        LoginRequest admin = new LoginRequest("admin@a.com", "123a!");
        LoginRequest user = new LoginRequest("user@a.com", "123a!");

        adminToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(admin)
                .when().post("/login")
                .then().extract().cookie("token");

        userToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post("/login")
                .then().extract().cookie("token");
    }

    @DisplayName("성공: 예약 삭제 가능, 다음 순위 예약대기는 자동 예약")
    @Test
    void delete() {
        userReservationService.reserve(new CreateReservationRequest(ADMIN_ID, DATE_FIRST, TIME_ID, THEME_ID), PAYMENT_ID);
        userReservationService.standby(USER_ID, new CreateUserReservationStandbyRequest(DATE_FIRST, TIME_ID, THEME_ID));
        userReservationService.reserve(new CreateReservationRequest(ADMIN_ID, DATE_SECOND, TIME_ID, THEME_ID), PAYMENT_ID);
        userReservationService.standby(USER_ID, new CreateUserReservationStandbyRequest(DATE_SECOND, TIME_ID, THEME_ID));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().delete("/admin/reservations/1")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("id", contains(2, 3));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/admin/reservations/standby")
                .then().log().all()
                .statusCode(200)
                .body("id", contains(4));
    }

    @DisplayName("실패: 일반 유저가 예약 삭제 -> 401")
    @Test
    void delete_ByUnauthorizedUser() {
        RestAssured.given().log().all()
                .cookie("token", userToken)
                .when().delete("/admin/reservations/3")
                .then().log().all()
                .statusCode(401);
    }

    @DisplayName("성공: 전체 예약 조회 -> 200")
    @Test
    void findAll() {
        adminReservationService.reserve(new CreateReservationRequest(ADMIN_ID, DATE_FIRST, TIME_ID, THEME_ID));
        adminReservationService.reserve(new CreateReservationRequest(USER_ID, DATE_SECOND, TIME_ID, THEME_ID));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("id", contains(1, 2));
    }

    @DisplayName("실패: 일반 유저가 전체 예약 조회 -> 401")
    @Test
    void findAll_ByUnauthorizedUser() {
        RestAssured.given().log().all()
                .cookie("token", userToken)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(401);
    }

    @DisplayName("성공: 전체 대기목록 조회 -> 200")
    @Test
    void findAllStandby() {
        userReservationService.reserve(new CreateReservationRequest(ADMIN_ID, DATE_FIRST, TIME_ID, THEME_ID), PAYMENT_ID);
        userReservationService.standby(USER_ID, new CreateUserReservationStandbyRequest(DATE_FIRST, TIME_ID, THEME_ID));
        userReservationService.reserve(new CreateReservationRequest(ADMIN_ID, DATE_SECOND, TIME_ID, THEME_ID), PAYMENT_ID);
        userReservationService.standby(USER_ID, new CreateUserReservationStandbyRequest(DATE_SECOND, TIME_ID, THEME_ID));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/admin/reservations/standby")
                .then().log().all()
                .statusCode(200)
                .body("id", contains(2, 4));
    }
}
