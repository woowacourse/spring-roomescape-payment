package roomescape.acceptance;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.application.auth.TokenManager;
import roomescape.application.auth.dto.TokenPayload;
import roomescape.application.member.dto.request.MemberLoginRequest;
import roomescape.application.member.dto.request.MemberRegisterRequest;
import roomescape.application.member.dto.response.MemberResponse;
import roomescape.application.payment.PaymentClient;
import roomescape.application.payment.dto.PaymentClientRequest;
import roomescape.application.reservation.dto.request.ReservationPaymentRequest;
import roomescape.application.reservation.dto.request.ReservationRequest;
import roomescape.application.reservation.dto.request.ReservationTimeRequest;
import roomescape.application.reservation.dto.request.ThemeRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationTimeResponse;
import roomescape.application.reservation.dto.response.ReservationWaitingResponse;
import roomescape.application.reservation.dto.response.ThemeResponse;
import roomescape.domain.member.Role;
import roomescape.domain.payment.Payment;

@TestComponent
@ExtendWith(MockitoExtension.class)
public class AcceptanceFixture {

    @Autowired
    private TokenManager tokenManager;

    @MockBean
    private PaymentClient paymentClient;

    private String adminToken;

    @PostConstruct
    void createAdminToken() {
        TokenPayload payload = new TokenPayload(1L, "admin", Role.ADMIN);
        adminToken = tokenManager.createToken(payload);
    }

    public MemberResponse registerMember(MemberRegisterRequest request) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .extract()
                .as(MemberResponse.class);
    }

    public String loginAndGetToken(MemberLoginRequest request) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().log().all()
                .extract()
                .cookie("token");
    }

    public String getAdminToken() {
        return adminToken;
    }

    public ThemeResponse createTheme(ThemeRequest request) {
        return RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .extract()
                .as(ThemeResponse.class);
    }

    public ReservationTimeResponse createReservationTime(int hour, int minute) {
        return RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .body(new ReservationTimeRequest(LocalTime.of(hour, minute)))
                .when().post("/times")
                .then().log().all()
                .extract()
                .as(ReservationTimeResponse.class);
    }

    public ReservationResponse createReservation(String token, ReservationRequest request) {
        String paymentKey = UUID.randomUUID().toString();
        String orderId = UUID.randomUUID().toString();
        given(paymentClient.requestPurchase(any(PaymentClientRequest.class)))
                .willReturn(new Payment(paymentKey, orderId, 10000L));
        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                request.memberId(), request.themeId(), request.date(), request.timeId(),
                paymentKey, orderId
        );
        return RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservationPaymentRequest)
                .when().post("/reservations")
                .then().log().all()
                .extract()
                .as(ReservationResponse.class);
    }

    public void mockPurchase(Payment payment) {
        given(paymentClient.requestPurchase(any(PaymentClientRequest.class)))
                .willReturn(payment);
    }

    public ReservationWaitingResponse enqueueWaitList(String token, ReservationRequest request) {
        return RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations/queue")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(ReservationWaitingResponse.class);
    }
}
