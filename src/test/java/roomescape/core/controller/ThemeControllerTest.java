package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static roomescape.core.utils.e2eTest.getAccessToken;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import roomescape.core.domain.Status;
import roomescape.core.dto.reservation.MemberReservationRequest;
import roomescape.core.dto.reservationtime.ReservationTimeRequest;
import roomescape.core.utils.e2eTest;

/**
 * 로그인 정보 (어드민) { "id": 1 "name": 어드민 "email": test@email.com "password": password "role": ADMIN }
 * <p>
 * 테마 정보 { "id": 1, "name": '테마1' } { "id": 2, "name": '테마2' } { "id": 3, "name": '테마3' } { "id": 4, "name": '테마4' } {
 * "id": 5, "name": '테마5' }
 **/

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
class ThemeControllerTest {
    private static final String PAYMENT_KEY = "mockPaymentKey";
    private static final String ORDER_ID = "mockOrderId";
    private static final Long AMOUNT = 1000L;

    private String accessToken;

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate paymentApproveRestTemplate;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        accessToken = getAccessToken();
    }

    @Test
    @DisplayName("모든 테마 목록을 조회한다.")
    void findAllThemes() {
        ValidatableResponse response = e2eTest.get("/themes");

        response.statusCode(200)
                .body("size()", is(5));
    }

    @Test
    @DisplayName("지난 한 주 동안의 인기 테마 목록을 조회한다.")
    void findPopularThemes() {
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(paymentApproveRestTemplate)
                .ignoreExpectOrder(true)
                .build();
        mockServer.expect(ExpectedCount.times(2), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess());
        createReservationTimes();
        createReservations();

        ValidatableResponse response = e2eTest.get("/themes/popular");

        response.statusCode(200)
                .body("size()", is(1))
                .body("name", is(List.of("테마2")));
        mockServer.verify();
    }

    private void createReservationTimes() {
        ReservationTimeRequest timeRequest = new ReservationTimeRequest(
                LocalTime.now().plusMinutes(1).format(DateTimeFormatter.ofPattern("HH:mm")));
        ValidatableResponse response1 = e2eTest.post(timeRequest, "/admin/times", accessToken);
        response1.statusCode(201);

        ReservationTimeRequest timeRequest2 = new ReservationTimeRequest(
                LocalTime.now().plusMinutes(2).format(DateTimeFormatter.ofPattern("HH:mm")));

        ValidatableResponse response2 = e2eTest.post(timeRequest2, "/admin/times", accessToken);
        response2.statusCode(201);
    }

    private void createReservations() {
        MemberReservationRequest firstThemeMemberReservationRequest = new MemberReservationRequest(
                LocalDate.now().format(DateTimeFormatter.ISO_DATE), 4L, 2L, Status.BOOKED.getValue(), PAYMENT_KEY,
                ORDER_ID, AMOUNT);

        ValidatableResponse response1 = e2eTest.post(firstThemeMemberReservationRequest, "/reservations", accessToken);
        response1.statusCode(201);

        MemberReservationRequest firstThemeMemberReservationRequest2 = new MemberReservationRequest(
                LocalDate.now().format(DateTimeFormatter.ISO_DATE), 5L, 2L, Status.BOOKED.getValue(),
                PAYMENT_KEY + "notUnique", ORDER_ID + "notUnique", AMOUNT);

        ValidatableResponse response2 = e2eTest.post(firstThemeMemberReservationRequest2, "/reservations", accessToken);
        response2.statusCode(201);
    }
}