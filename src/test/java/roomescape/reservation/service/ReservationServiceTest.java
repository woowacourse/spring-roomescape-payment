package roomescape.reservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.member.domain.Member;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.Status;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservation.presentation.dto.ReservationResponse;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationDomainService reservationDomainService;

    private final ReservationRequest request = new ReservationRequest(
        LocalDate.now().plusDays(1), 1L, 1L, "pay-key", "order-5", 1000
    );
    private final Long memberId = 1L;

    @Test
    @DisplayName("정상 결제 시 예약이 저장된다")
    void success_creates_reservation() {
        Reservation reservation = Reservation.createWithId(
            5L,
            request.date(),
            ReservationTime.createWithId(1L, LocalTime.of(10, 0)),
            Theme.createWithId(1L, "테마1", "재밌음", "1"),
            Member.createWithId(1L, "유저1", "member1@email.com", "password"),
            Status.RESERVED
        );
        when(reservationDomainService.saveReservation(any(), any())).thenReturn(reservation);

        ReservationResponse response = reservationService.createReservation(request, memberId);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.theme().name()).isEqualTo("테마1");
        assertThat(response.member().id()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("confirmPayment 실패 시 아무것도 저장되지 않는다")
    void confirmPayment_fails_and_rolls_back() {
        doThrow(new RuntimeException("결제 실패")).when(paymentService).confirmPayment(any());

        assertThatThrownBy(() -> reservationService.createReservation(request, memberId))
            .isInstanceOf(RuntimeException.class);

        assertThat(reservationRepository.findById(5L)).isEmpty();
    }

    @Test
    @DisplayName("saveReservation 실패 시 전체 롤백된다")
    void saveReservation_fails_and_rolls_back() {
        doThrow(new RuntimeException("예약 실패")).when(reservationDomainService).saveReservation(any(), any());

        assertThatThrownBy(() -> reservationService.createReservation(request, memberId))
            .isInstanceOf(RuntimeException.class);

        assertThat(reservationRepository.findById(5L)).isEmpty();
    }

    @TestConfiguration
    static class SpyBeanConfig {

        @Bean
        @Primary
        public PaymentService paymentServiceSpy() {
            PaymentService mock = mock(PaymentService.class);
            doNothing().when(mock).confirmPayment(any());
            return mock;
        }

        @Bean
        @Primary
        public ReservationDomainService reservationDomainServiceSpy(ReservationDomainService target) {
            ReservationDomainService mock = mock(ReservationDomainService.class);
            when(mock.saveReservation(any(), any())).thenReturn(mock(Reservation.class));
            return mock;
        }
    }
}
