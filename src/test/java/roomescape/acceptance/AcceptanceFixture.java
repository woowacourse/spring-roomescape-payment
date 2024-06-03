package roomescape.acceptance;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import java.time.LocalTime;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.application.auth.TokenManager;
import roomescape.application.auth.dto.TokenPayload;
import roomescape.application.member.dto.request.MemberLoginRequest;
import roomescape.application.member.dto.request.MemberRegisterRequest;
import roomescape.application.payment.PaymentClient;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.application.reservation.dto.request.ReservationPaymentRequest;
import roomescape.application.reservation.dto.request.ReservationRequest;
import roomescape.application.reservation.dto.request.ReservationTimeRequest;
import roomescape.application.reservation.dto.request.ThemeRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.application.reservation.dto.response.ReservationTimeResponse;
import roomescape.application.reservation.dto.response.ThemeResponse;
import roomescape.domain.member.Role;

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

    public ExtractableResponse<Response> registerMember(MemberRegisterRequest request) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .extract();
    }

    public String loginAndGetToken(String email, String password) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new MemberLoginRequest(email, password))
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
        given(paymentClient.requestPurchase(any(PaymentRequest.class)))
                .willReturn(new Payment("paymentKey", "orderId", "DONE", 10000L));
        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                request.memberId(), request.themeId(), request.date(), request.timeId(),
                "paymentKey", "orderId"
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
}
