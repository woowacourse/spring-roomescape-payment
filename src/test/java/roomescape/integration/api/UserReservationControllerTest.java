package roomescape.integration.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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
import roomescape.controller.dto.CreateUserReservationRequest;
import roomescape.controller.dto.CreateUserReservationStandbyRequest;
import roomescape.controller.dto.LoginRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.global.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.service.AdminReservationService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;
import roomescape.service.TossPaymentService;
import roomescape.service.UserReservationService;
import roomescape.service.dto.TossPaymentResponseDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class UserReservationControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeService themeService;

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Autowired
    private AdminReservationService adminReservationService;

    @Autowired
    private UserReservationService userReservationService;

    @MockBean
    private TossPaymentService tossPaymentService;

    private static final Long USER_ID = 1L;
    private static final Long ANOTHER_USER_ID = 2L;

    private static final Long TIME_ID = 1L;
    private static final Long THEME_ID = 1L;

    private static final LocalDate DATE_FIRST = LocalDate.parse("2060-01-01");
    private static final LocalDate DATE_SECOND = LocalDate.parse("2060-01-02");

    private String userToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        reservationTimeService.save("10:00");
        themeService.save("t1", "설명1", "https://test.com/test.jpg");
        memberRepository.save(new Member("러너덕", "user@a.com", "123a!", Role.USER));
        memberRepository.save(new Member("트레", "tre@a.com", "123a!", Role.USER));

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
        doReturn(new TossPaymentResponseDto("", 0, ""))
            .when(tossPaymentService)
            .pay(any(String.class), any(Long.class), any(String.class));

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

    @DisplayName("실패: 결제 실패 시 예약이 추가되면 안 됨")
    @Test
    void save_PaymentApprovalFailed() {
        doThrow(RoomescapeException.class)
            .when(tossPaymentService)
            .pay(any(String.class), any(Long.class), any(String.class));

        CreateUserReservationRequest request = new CreateUserReservationRequest(
            DATE_FIRST, THEME_ID, TIME_ID, "1", "1", 1000, "1");

        assertAll(
            () -> RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", userToken)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400),
            () -> assertThat(adminReservationService.findAllReserved()).hasSize(0),
            () -> assertThat(adminReservationService.findAllStandby()).hasSize(0)
        );
    }

    @DisplayName("성공: 예약대기 추가 -> 201")
    @Test
    void standby() {
        adminReservationService.reserve(ANOTHER_USER_ID, DATE_FIRST, TIME_ID, THEME_ID);

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
        adminReservationService.reserve(ANOTHER_USER_ID, DATE_FIRST, TIME_ID, THEME_ID);
        userReservationService.standby(USER_ID, DATE_FIRST, TIME_ID, THEME_ID);

        RestAssured.given().log().all()
            .cookie("token", userToken)
            .when().delete("/reservations/standby/2")
            .then().log().all()
            .statusCode(204);
    }

    @DisplayName("실패: 다른 사람의 예약대기 삭제 -> 400")
    @Test
    void deleteStandby_ReservedByOther() {
        adminReservationService.reserve(USER_ID, DATE_FIRST, TIME_ID, THEME_ID);
        userReservationService.standby(ANOTHER_USER_ID, DATE_FIRST, TIME_ID, THEME_ID);

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
        doReturn(new TossPaymentResponseDto("", 0, ""))
            .when(tossPaymentService)
            .pay(any(String.class), any(Long.class), any(String.class));

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
        doReturn(new TossPaymentResponseDto("", 0, ""))
            .when(tossPaymentService)
            .pay(any(String.class), any(Long.class), any(String.class));

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
        doReturn(new TossPaymentResponseDto("", 0, ""))
            .when(tossPaymentService)
            .pay(any(String.class), any(Long.class), any(String.class));

        adminReservationService.reserve(ANOTHER_USER_ID, DATE_FIRST, TIME_ID, THEME_ID);

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
        adminReservationService.reserve(USER_ID, DATE_FIRST, TIME_ID, THEME_ID);
        adminReservationService.reserve(ANOTHER_USER_ID, DATE_SECOND, TIME_ID, THEME_ID);
        userReservationService.standby(USER_ID, DATE_SECOND, TIME_ID, THEME_ID);

        RestAssured.given().log().all()
            .cookie("token", userToken)
            .contentType(ContentType.JSON)
            .when().get("/reservations/mine")
            .then().log().all()
            .statusCode(200)
            .body("id", contains(1, 3))
            .body("status", contains("RESERVED", "STANDBY"))
            .body("rank", contains(0, 1));
    }
}
