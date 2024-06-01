package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mockito.BDDMockito;
import org.springframework.http.HttpStatus;
import roomescape.BasicAcceptanceTest;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.request.reservation.ReservationRequest;
import roomescape.dto.response.reservation.TossExceptionResponse;
import roomescape.exception.PaymentException;

class ReservationAcceptanceTest extends BasicAcceptanceTest {
    private String clientToken;
    private String adminToken;

    @BeforeEach
    void SetUp() {
        clientToken = LoginTokenProvider.login("member@wooteco.com", "wootecoCrew6!", 200);
        adminToken = LoginTokenProvider.login("admin@wooteco.com", "wootecoCrew6!", 200);
    }

    @TestFactory
    @DisplayName("관리자 페이지에서 3개의 예약을 추가한다")
    Stream<DynamicTest> adminReservationPostAndGetTest() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        return Stream.of(
                dynamicTest("관리자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postAdminReservation(adminToken, tomorrow.toString(), 1L, 1L, 1L, 201)),
                dynamicTest("관리자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postAdminReservation(adminToken, tomorrow.toString(), 2L, 2L, 2L, 201)),
                dynamicTest("관리자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postAdminReservation(adminToken, tomorrow.toString(), 3L, 3L, 3L, 201)),
                dynamicTest("모든 예약을 조회한다 (총 6개)", () -> ReservationTestStep.getReservations(200, 6))
        );
    }

    @TestFactory
    @DisplayName("관리자 페이지에서 조건에 맞는 예약을 검색한다")
    Stream<DynamicTest> adminSearchReservation() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        return Stream.of(
                dynamicTest("관리자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postAdminReservation(adminToken, tomorrow.toString(), 1L, 1L, 1L, 201)),
                dynamicTest("관리자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postAdminReservation(adminToken, tomorrow.toString(), 1L, 2L, 1L, 201)),
                dynamicTest("관리자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postAdminReservation(adminToken, tomorrow.toString(), 3L, 3L, 3L, 201)),
                dynamicTest("날짜는 어제부터 내일까지, member_id는 1, theme_id는 1인 예약을 검색한다 (총 2개)", () -> adminSearch(adminToken, 1L, 1L, yesterday.toString(), tomorrow.toString(), 200, 2))
        );
    }

    @TestFactory
    @DisplayName("사용자 페이지에서 3개의 예약을 추가한다")
    Stream<DynamicTest> userReservationPostAndGetTest() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        return Stream.of(
                dynamicTest("사용자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postClientReservation(clientToken, tomorrow.toString(), 1L, 1L, 201)),
                dynamicTest("사용자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postClientReservation(clientToken, tomorrow.toString(), 2L, 2L, 201)),
                dynamicTest("사용자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postClientReservation(clientToken, tomorrow.toString(), 3L, 3L, 201)),
                dynamicTest("모든 예약을 조회한다 (총 6개)", () -> ReservationTestStep.getReservations(200, 6)),
                dynamicTest("자신의 예약을 조회한다 (총 3개)", () -> getMyReservations(clientToken, 200, 3))
        );
    }

    @TestFactory
    @DisplayName("과거 시간에 대한 예약을 하면, 예외가 발생한다")
    Stream<DynamicTest> pastTimeReservationTest() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        return Stream.of(
                dynamicTest("과거 시간에 대한 예약을 추가한다", () -> ReservationTestStep.postClientReservation(clientToken, yesterday.toString(), 1L, 1L, 400))
        );
    }

    @TestFactory
    @DisplayName("예약을 추가하고 삭제한다")
    Stream<DynamicTest> reservationPostAndDeleteTest() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        AtomicLong reservationId = new AtomicLong();

        return Stream.of(
                dynamicTest("사용자 페이지에서 예약을 추가한다", () -> reservationId.set(ReservationTestStep.postClientReservation(
                        clientToken, tomorrow.toString(), 1L, 1L, 201))),
                dynamicTest("사용자 페이지에서 예약을 추가한다", () -> ReservationTestStep.postClientReservation(clientToken, tomorrow.toString(), 2L, 2L, 201)),
                dynamicTest("모든 예약을 조회한다 (총 5개)", () -> ReservationTestStep.getReservations(200, 5)),
                dynamicTest("예약을 삭제한다", () -> ReservationTestStep.deleteReservation(reservationId.longValue(), 204)),
                dynamicTest("모든 예약을 조회한다 (총 4개)", () -> ReservationTestStep.getReservations(200, 4))
        );
    }

    @DisplayName("결제가 실패하면 예약이 생성되지 않는다.")
    @Test
    void reservationPostWhenPaymentFail() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        ReservationRequest request = new ReservationRequest(tomorrow, 1L, 1L, null, null, BigDecimal.valueOf(1000));
        PaymentRequest paymentRequest = new PaymentRequest(request.orderId(), request.amount(),
                request.paymentKey());
        BDDMockito.given(paymentClient.requestPayment(paymentRequest))
                .willThrow(new PaymentException(
                        HttpStatus.BAD_REQUEST, new TossExceptionResponse("EXCEPTION", "exception")));

        ReservationTestStep.postClientReservation(
                clientToken, tomorrow.toString(), 1L, 1L, 400);
        ReservationTestStep.getReservations(200, 3);
    }

    private void adminSearch(String token, Long themeId, Long memberId, String dateFrom, String dateTo, int expectedHttpCode, int expectedReservationResponsesSize) {
        Response response = RestAssured.given().log().all()
                .cookies("token", token)
                .when().get("/admin/reservations/search?themeId=" + themeId + "&memberId=" + memberId + "&dateFrom=" + dateFrom + "&dateTo=" + dateTo)
                .then().log().all()
                .statusCode(expectedHttpCode)
                .extract().response();

        List<?> reservationResponses = response.as(List.class);

        assertThat(reservationResponses).hasSize(expectedReservationResponsesSize);
    }

    private void getMyReservations(String token, int expectedHttpCode, int expectedReservationResponsesSize) {
        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(expectedHttpCode)
                .extract().response();

        List<?> reservationResponses = response.as(List.class);

        assertThat(reservationResponses).hasSize(expectedReservationResponsesSize);
    }
}
