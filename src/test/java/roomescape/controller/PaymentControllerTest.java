package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.controller.doc.DocumentFilter;
import roomescape.domain.*;
import roomescape.domain.repository.*;
import roomescape.infrastructure.auth.JwtProvider;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.response.PaymentDto;
import roomescape.web.controller.request.PaymentRequest;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static roomescape.Fixture.*;

public class PaymentControllerTest extends ControllerTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @MockBean
    private PaymentManager paymentManager;

    @DisplayName("예약 결제를 저장한다. -> 201")
    @Test
    void savePayment() {
        ReservationTime time = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        Theme theme = themeRepository.save(VALID_THEME);
        String date = LocalDate.now().plusMonths(1).toString();
        Member member = memberRepository.save(VALID_MEMBER);
        Reservation reservation = reservationRepository.save(new Reservation(member, new ReservationDate(date), time, theme));
        String token = jwtProvider.createToken(member.getEmail().getEmail());
        PaymentRequest paymentRequest = new PaymentRequest(reservation.getId(), "paymentKey", "orderId", theme.getPrice());
        when(paymentManager.approve(new PaymentApproveDto("paymentKey", "orderId", theme.getPrice())))
                .thenReturn(new PaymentDto("paymentKey", "orderId", theme.getPrice()));

        RestAssured.given(spec)
                .filter(DocumentFilter.SAVE_PAYMENT.getValue())
                .cookie("token", token)
                .log().all()
                .contentType(ContentType.JSON)
                .body(paymentRequest)
                .when().post("/payments")
                .then().log().all()
                .statusCode(201);
    }
}
