package roomescape.acceptance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import io.restassured.RestAssured;
import roomescape.auth.JwtTokenProvider;
import roomescape.component.TossPaymentClient;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
abstract class AcceptanceTest {

    @LocalServerPort
    private int port;

    @MockBean
    TossPaymentClient paymentClient;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        when(paymentClient.confirm(any()))
                .thenReturn(new PaymentConfirmResponse("paymentKey", "orderId", 1000L));
    }

    protected Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    protected ReservationTime saveTime(ReservationTime time) {
        return timeRepository.save(time);
    }

    protected Theme saveTheme(Theme theme) {
        return themeRepository.save(theme);
    }

    protected Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    protected Reservation saveReservation(Reservation reservation) {
        assert reservation.getStatus() == ReservationStatus.RESERVED;
        return reservationRepository.save(reservation);
    }

    protected Reservation saveWaiting(Reservation reservation) {
        assert reservation.getStatus() == ReservationStatus.PENDING;
        return reservationRepository.save(reservation);
    }

    protected String accessToken(long memberId) {
        return jwtTokenProvider.createToken(memberId);
    }

//    protected ValidatableResponse assertPostResponseWithToken(final Object request, final String email,
//            final String path, final int statusCode
//    ) {
//        final String accessToken = accessToken(email);
//        return RestAssured.given().log().all()
//                .cookie("token", accessToken)
//                .contentType(ContentType.JSON)
//                .body(request)
//                .when().post(path)
//                .then().log().all()
//                .statusCode(statusCode);
//    }
//
//    protected ValidatableResponse assertPostResponse(final Object request, final String path, final int statusCode) {
//        return RestAssured.given().log().all()
//                .contentType(ContentType.JSON)
//                .body(request)
//                .when().post(path)
//                .then().log().all()
//                .statusCode(statusCode);
//    }
//
//    protected ValidatableResponse assertGetResponse(final String path, final int statusCode) {
//        return RestAssured.given().log().all()
//                .when().get(path)
//                .then().log().all()
//                .statusCode(statusCode);
//    }
//
//    protected ValidatableResponse assertGetResponseWithToken(final String token, final String path,
//            final int statusCode
//    ) {
//        return RestAssured.given().log().all()
//                .cookie("token", token)
//                .when().get(path)
//                .then().log().all()
//                .statusCode(statusCode);
//    }
//
//    protected void assertDeleteResponse(final String path, final Long id, final int statusCode) {
//        RestAssured.given().log().all()
//                .when().delete(path + id)
//                .then().log().all()
//                .statusCode(statusCode);
//    }
}
