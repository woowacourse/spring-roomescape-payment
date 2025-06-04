package roomescape.payment.service;

import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.dto.TossPaymentConfirmRequest;
import roomescape.payment.dto.TossPaymentConfirmResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.processor.PaymentType;
import roomescape.payment.processor.toss.TossPaymentProcessor;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

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
    private ThemeRepository themeRepository;

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
        final Theme theme = themeRepository.save(new Theme("공포", "설명", "썸네일"));
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
