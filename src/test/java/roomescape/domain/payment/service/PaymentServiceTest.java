package roomescape.domain.payment.service;

import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.domain.member.Role;
import roomescape.domain.member.entity.Member;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.payment.dto.TossPaymentConfirmRequest;
import roomescape.domain.payment.dto.TossPaymentConfirmResponse;
import roomescape.domain.payment.entity.Payment;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.time.entity.ReservationTime;
import roomescape.infrastructure.member.MemberRepository;
import roomescape.infrastructure.payment.toss.TossPaymentProcessor;
import roomescape.infrastructure.reservation.ReservationRepository;
import roomescape.infrastructure.theme.JpaThemeRepository;
import roomescape.infrastructure.time.ReservationTimeRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private JpaThemeRepository jpaThemeRepository;

    @MockitoBean
    private TossPaymentProcessor tossPaymentProcessor;

    @Test
    void 결제를_할_수_있다() {
        // given
        final TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                10000,
                "orderId",
                "paymentKey"
        );
        final TossPaymentConfirmResponse expected = new TossPaymentConfirmResponse(
                "orderId",
                "paymentKey",
                10000
        );
        final Member member = memberRepository.save(new Member("우가", "wooga@gmail.com", "password", Role.USER));
        final ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(15, 15)));
        final Theme theme = jpaThemeRepository.save(new Theme("공포", "설명", "썸네일"));
        final Reservation reservation = new Reservation(member, LocalDate.of(2026, 10, 10), time, theme);
        final Reservation savedReservation = reservationRepository.save(reservation);

        when(tossPaymentProcessor.supports(PaymentType.TOSS)).thenReturn(true);
        when(tossPaymentProcessor.processPayment(any(TossPaymentConfirmRequest.class))).thenReturn(expected);

        // when
        final Payment payment = paymentService.processPayment(
                PaymentType.TOSS,
                request,
                savedReservation
        );

        // then
        Assertions.assertThat(payment.getId()).isNotNull();
    }

}
