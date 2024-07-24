package roomescape.service.booking.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.UserReservationPaymentRequest;
import roomescape.service.booking.reservation.module.PaymentService;

@Sql("/all-test-data.sql")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @MockBean
    PaymentService paymentService;

    @Test
    void 정상_결제시_예약_등록() {
        // given
        UserReservationPaymentRequest userReservationPaymentRequest = new UserReservationPaymentRequest(
                LocalDate.now().plusDays(7), 1L, 1L, 1L, "paymentKey", "orderId",  BigDecimal.valueOf(1000), "paymentType");

        PaymentRequest paymentRequest = PaymentRequest.from(userReservationPaymentRequest);
        PaymentResponse paymentResponse = new PaymentResponse(paymentRequest.paymentKey(), paymentRequest.orderId(), paymentRequest.amount());
        Mockito.when(paymentService.payByToss(userReservationPaymentRequest)).thenReturn(paymentResponse);

        // when
        ReservationResponse reservationResponse = reservationService.registerReservationPayments(
                userReservationPaymentRequest,
                userReservationPaymentRequest.memberId(), paymentResponse);

        // then
        assertThat(reservationResponse).isNotNull();
    }
}
