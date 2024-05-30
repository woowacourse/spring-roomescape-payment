package roomescape.paymenthistory.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doNothing;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.paymenthistory.domain.PaymentRestClient;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.repository.PaymentHistoryRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryServiceTest {

    @InjectMocks
    private PaymentHistoryService paymentHistoryService;

    @Mock
    private PaymentRestClient paymentRestClient;
    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;


    @DisplayName("결제를 승인한다.")
    @Test
    void approvePayment() {
        PaymentCreateRequest paymentCreateRequest = new PaymentCreateRequest("paymentKey", "orderId", 1000,
                new Reservation(1L,
                        MemberFixture.MEMBER_BRI, LocalDate.now().plusDays(1), TimeFixture.TIME_1, ThemeFixture.THEME_1,
                        ReservationStatus.RESERVED));

        doNothing().when(paymentRestClient).approvePayment(paymentCreateRequest);

        assertThatCode(() -> paymentHistoryService.approvePayment(paymentCreateRequest))
                .doesNotThrowAnyException();
    }
}
