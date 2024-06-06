package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRestClient;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.payment.dto.RestClientPaymentApproveResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final Reservation RESERVATION = new Reservation(1L,
            MemberFixture.MEMBER_BRI, LocalDate.now().plusDays(1), TimeFixture.TIME_1, ThemeFixture.THEME_1,
            ReservationStatus.RESERVED);

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRestClient paymentRestClient;
    @Mock
    private PaymentRepository paymentRepository;


    @DisplayName("결제를 승인한다.")
    @Test
    void approvePayment() {
        PaymentCreateRequest paymentCreateRequest = new PaymentCreateRequest("paymentKey",
                "orderId", BigDecimal.valueOf(1000), RESERVATION);
        RestClientPaymentApproveResponse response = new RestClientPaymentApproveResponse(
                "paymentKey", "orderId", BigDecimal.valueOf(1000), ZonedDateTime.now()
        );

        when(paymentRestClient.approvePayment(paymentCreateRequest))
                .thenReturn(response);

        assertThatCode(() -> paymentService.approvePayment(paymentCreateRequest))
                .doesNotThrowAnyException();
    }

    @DisplayName("결제를 취소한다.")
    @Test
    void cancelPayment() {
        doNothing().when(paymentRestClient).cancelPayment("paymentKey");
        when(paymentRepository.findByReservation_Id(1L))
                .thenReturn(Optional.of(new Payment(RESERVATION, "paymentKey", BigDecimal.valueOf(1000), "orderId",
                        LocalDateTime.now())));

        assertThatCode(() -> paymentService.cancelPayment(1L))
                .doesNotThrowAnyException();
    }
}
