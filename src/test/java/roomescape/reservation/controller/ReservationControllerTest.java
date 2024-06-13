package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static roomescape.fixture.MemberFixture.MEMBER_ADMIN;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.fixture.CookieProvider;
import roomescape.fixture.DateFixture;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.model.SpringBootTestBase;
import roomescape.paymenthistory.PaymentType;
import roomescape.paymenthistory.exception.PaymentException;
import roomescape.paymenthistory.service.TossPaymentHistoryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;

@AutoConfigureMockMvc
class ReservationControllerTest extends SpringBootTestBase {

    @Autowired
    private ReservationRepository reservationRepository;

    @MockBean
    private TossPaymentHistoryService tossPaymentHistoryService;

    @DisplayName("예약을 조회, 추가, 삭제할 수 있다.")
    @Test
    void findCreateDeleteReservations() {
        doNothing().when(tossPaymentHistoryService).approvePayment(any());

        Cookies cookies = restAssuredTemplate.makeUserCookie(MEMBER_BRI);
        LocalDate date = LocalDate.now().plusDays(1);
        Long themeId = restAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), cookies).id();
        Long timeId = restAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), cookies).id();

        ReservationCreateRequest params = new ReservationCreateRequest(date, timeId, themeId, "paymentKey", "orderId",
                1000, PaymentType.NORMAL);

        // 예약 추가
        ReservationResponse response = RestAssured.given().log().all()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getObject("", ReservationResponse.class);

        // 예약 조회
        List<ReservationResponse> reservationResponses = RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("", ReservationResponse.class);

        assertThat(reservationResponses).containsExactlyInAnyOrder(response);

        // 예약 삭제
        RestAssured.given().log().all()
                .when().delete("/reservations/" + response.id())
                .then().log().all()
                .statusCode(204);

        // 예약 조회
        reservationResponses = RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("", ReservationResponse.class);

        assertThat(reservationResponses).isEmpty();
    }

    @DisplayName("예약 추가 시 인자 중 null이 있을 경우, 예약을 추가할 수 없다.")
    @Test
    void createReservation_whenNameIsNull() {
        Cookies cookies = restAssuredTemplate.makeUserCookie(MEMBER_ADMIN);
        Long themeId = restAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), cookies).id();
        Long timeId = restAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), cookies).id();

        AdminReservationCreateRequest params = new AdminReservationCreateRequest
                (null, null, timeId, themeId);
        Cookies userCookies = CookieProvider.makeUserCookie();

        RestAssured.given().log().all()
                .cookies(userCookies)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(400)
                .body("errorMessage", is("인자 중 null 값이 존재합니다."));
    }

    @DisplayName("예약 삭제 시 예약 대기가 존재하지 않는다면 삭제된다.")
    @Test
    void deleteReservation_whenWaitingNotExists() {
        Cookies cookies = restAssuredTemplate.makeUserCookie(MEMBER_ADMIN);
        LocalDate date = LocalDate.now().plusDays(1);
        Long themeId = restAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), cookies).id();
        Long timeId = restAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), cookies).id();

        AdminReservationCreateRequest reservationParams =
                new AdminReservationCreateRequest(MEMBER_ADMIN.getId(), date, timeId, themeId);
        ReservationResponse response = restAssuredTemplate.create(reservationParams, cookies);

        // 예약 삭제
        RestAssured.given().log().all()
                .when().delete("/reservations/" + response.id())
                .then().log().all()
                .statusCode(204);

        // 예약 조회
        List<ReservationResponse> reservationResponses = RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList("", ReservationResponse.class);

        assertThat(reservationResponses).doesNotContain(response);
    }

    @DisplayName("사용자가 예약을 시도하던 도중 결제에 실패하는 경우 예약을 취소한 후 에러를 발생한다.")
    @Test
    void findMyReservationsWaitings_whenPaymentFails() {

        Cookies loginMemberCookies = restAssuredTemplate.makeUserCookie(MemberFixture.MEMBER_BROWN);
        Cookies adminCookies = restAssuredTemplate.makeUserCookie(MemberFixture.MEMBER_ADMIN);

        Long themeId = restAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), adminCookies).id();
        Long timeId = restAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), adminCookies).id();

        ReservationCreateRequest reservationParams = new ReservationCreateRequest(DateFixture.TOMORROW_DATE, timeId,
                themeId,
                "paymentKey", "orderId",
                214000, PaymentType.NORMAL);

        doThrow(PaymentException.class)
                .when(tossPaymentHistoryService).approvePayment(any());

        RestAssured.given().log().all()
                .cookies(loginMemberCookies)
                .contentType(ContentType.JSON)
                .body(reservationParams)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(500);

        Optional<Reservation> reservation = reservationRepository.findById(1L);

        assertThat(reservation)
                .isEmpty();
    }
}
