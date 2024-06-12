package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.controller.steps.ReservationAdminSteps;
import roomescape.controller.steps.ReservationSteps;
import roomescape.domain.member.MemberRole;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.response.PaymentDto;
import roomescape.web.controller.request.MemberReservationRequest;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static roomescape.Fixture.*;
import static roomescape.controller.doc.DocumentFilter.SAVE_RESERVATION;

class ReservationControllerTest extends ControllerTest {

    @MockBean
    private PaymentManager paymentManager;

    @BeforeEach
    void setInitialData() {
        jdbcTemplate.update("INSERT INTO reservation_time(start_at) VALUES (?)", "12:00");
        jdbcTemplate.update("INSERT INTO theme(name, description, thumbnail, price) VALUES (?, ?, ?, ?)", "방탈출1", "설명1",
                "https://url1", 1000L);
        jdbcTemplate.update("INSERT INTO member(name,email,password,role) VALUES (?,?,?,?)",
                VALID_USER_NAME.getName(), VALID_USER_EMAIL.getEmail(),
                VALID_USER_PASSWORD.getPassword(), MemberRole.USER.name());
        jdbcTemplate.update("INSERT INTO reservation(date,time_id,theme_id,member_id) VALUES (?,?,?,?)",
                "2026-02-01", 1L, 1L, 1L);
    }

    @DisplayName("예약을 저장한다. -> 201")
    @Test
    void reserve() {
        PaymentDto paymentDto = new PaymentDto("paymentKey", "orderId", 1000L);
        when(paymentManager.approve(any())).thenReturn(paymentDto);
        MemberReservationRequest request = new MemberReservationRequest("2040-01-02", 1L, 1L, "paymentKey", "orderId", 1000L);

        RestAssured.given(spec).log().all()
                .filter(SAVE_RESERVATION.getValue())
                .contentType(ContentType.JSON)
                .cookie(COOKIE_NAME, getUserToken())
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("name", is(VALID_USER_NAME.getName()))
                .body("payment.paymentKey", is(paymentDto.paymentKey()));
    }

    @DisplayName("예약을 조회한다. -> 200")
    @Test
    void getReservations() {
        ReservationSteps.getReservations()
                .statusCode(200)
                .body("size()", is(1));
    }

    @DisplayName("실패: paymentKey가 비어 있을 경우 -> 400")
    @Test
    void blankPaymentKeyRequest() {
        LocalDate date = LocalDate.now().minusDays(3);
        MemberReservationRequest request = new MemberReservationRequest(date.toString(), 1L, 1L, "", "orderId", 1000L);
        ReservationSteps.createReservation(request, getUserToken())
                .statusCode(400);
    }

    @DisplayName("실패: orderId가 비어 있을 경우 -> 400")
    @Test
    void blankOrderIdRequest() {
        LocalDate date = LocalDate.now().minusDays(3);
        MemberReservationRequest request = new MemberReservationRequest(date.toString(), 1L, 1L, "paymentKey", "", 1000L);
        ReservationSteps.createReservation(request, getUserToken())
                .statusCode(400);
    }

    @DisplayName("실패: amount가 올바르지 않은 경우 -> 400")
    @Test
    void illegalAmountRequest() {
        LocalDate date = LocalDate.now().minusDays(3);
        MemberReservationRequest request = new MemberReservationRequest(date.toString(), 1L, 1L, "paymentKey", "orderId", null);
        ReservationSteps.createReservation(request, getUserToken())
                .statusCode(400);
    }

    @DisplayName("실패: 예약 날짜가 잘못될 경우 -> 400")
    @Test
    void reserve_IllegalDateRequest() {
        LocalDate date = LocalDate.now().minusDays(3);
        MemberReservationRequest request = new MemberReservationRequest(date.toString(), 1L, 1L, "paymentKey", "orderId", 1000L);
        ReservationSteps.createReservation(request, getUserToken())
                .statusCode(400);
    }

    @DisplayName("실패: 존재하지 않는 테마에 대한 예약  -> 400")
    @Test
    void reserve_NoSuchTheme() {
        MemberReservationRequest request = new MemberReservationRequest("2040-01-02", 1L, 200L, "paymentKey", "orderId", 1000L);
        ReservationSteps.createReservation(request, getUserToken())
                .statusCode(400);
    }

    @DisplayName("실패: 존재하지 않는 예약 시간에 대한 예약  -> 400")
    @Test
    void reserve_NoSuchTime() {
        MemberReservationRequest request = new MemberReservationRequest("2040-01-02", 100L, 1L, "paymentKey", "orderId", 1000L);
        ReservationSteps.createReservation(request, getUserToken())
                .statusCode(400);
    }

    @DisplayName("과거 시간에 예약을 넣을 경우 -> 400")
    @Test
    void reserve_PastTime() {
        MemberReservationRequest request = new MemberReservationRequest("2024-05-10", 100L, 1L, "paymentKey", "orderId", 1000L);

        ReservationSteps.createReservation(request, getUserToken())
                .statusCode(400);
    }

    @DisplayName("내 예약을 조회한다. -> 200")
    @Test
    void getMyReservations() {
        jdbcTemplate.update("INSERT INTO member(name,email,password,role) VALUES (?,?,?,?)",
                "aaa", "aaa@aaa.com",
                "bbb", MemberRole.USER.name());
        jdbcTemplate.update("INSERT INTO reservation(date,time_id,theme_id,member_id) VALUES (?,?,?,?)",
                "2026-02-01", 1L, 1L, 2L);

        ReservationSteps.getMyReservation(getUserToken())
                .statusCode(200)
                .body("size()", is(1));
    }

    @DisplayName("필터링된 예약을 조회한다. -> 200")
    @TestFactory
    Stream<DynamicTest> getFilteredReservations() {
        jdbcTemplate.update("INSERT INTO member(name,email,password,role) VALUES (?,?,?,?)",
                VALID_ADMIN_NAME.getName(), VALID_ADMIN_EMAIL.getEmail(),
                VALID_ADMIN_PASSWORD.getPassword(), MemberRole.ADMIN.name());
        jdbcTemplate.update("INSERT INTO theme(name, description, thumbnail, price) VALUES (?, ?, ?, ?)",
                VALID_THEME.getName(), VALID_THEME.getDescription(), VALID_THEME.getThumbnail(), VALID_THEME.getPrice());
        jdbcTemplate.update("INSERT INTO reservation(date,time_id,theme_id,member_id) VALUES (?,?,?,?)",
                "2026-02-01", 1L, 1L, 2L);
        jdbcTemplate.update("INSERT INTO reservation(date,time_id,theme_id,member_id) VALUES (?,?,?,?)",
                "2026-03-02", 1L, 2L, 1L);

        return Stream.of(
                dynamicTest("테마 아이디로 예약 필터링", () ->
                        ReservationAdminSteps.searchReservation("themeId=1", getAdminToken())
                                .statusCode(200)
                                .body("size()", is(2))),
                dynamicTest("멤버 아이디로 예약 필터링", () ->
                        ReservationAdminSteps.searchReservation("memberId=1", getAdminToken())
                                .statusCode(200)
                                .body("size()", is(2))),
                dynamicTest("시작 날짜로 예약 필터링", () ->
                        ReservationAdminSteps.searchReservation("dateFrom=2026-02-02", getAdminToken())
                                .statusCode(200)
                                .body("size()", is(1))),
                dynamicTest("전체 조건으로 예약 필터링", () ->
                        ReservationAdminSteps.searchReservation("memberId=1&themeId=1&dateFrom=2026-02-01&dateTo=2026-03-02", getAdminToken())
                                .statusCode(200)
                                .body("size()", is(1))),
                dynamicTest("종료 날짜로 예약 필터링", () ->
                        ReservationAdminSteps.searchReservation("dateTo=2026-03-01", getAdminToken())
                                .statusCode(200)
                                .body("size()", is(2)))
        );
    }
}
