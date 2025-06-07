package roomescape.application.facade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.application.payment.OrderAmountVerificationCache;
import roomescape.application.payment.toss.TossPaymentClient;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.TossPayment;
import roomescape.domain.payment.repository.TossPaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationPayment;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.repository.ReservationPaymentRepository;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;
import roomescape.infrastructure.error.exception.PaymentException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class ReservationTossPaymentFacadeTest {

    @Autowired
    private ReservationTossPaymentFacade reservationTossPaymentFacade;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderAmountVerificationCache orderAmountVerificationCache;

    @Autowired
    private TossPaymentRepository tossPaymentRepository;

    @Autowired
    private ReservationPaymentRepository reservationPaymentRepository;

    @MockitoBean
    private TossPaymentClient tossPaymentClient;

    @Autowired
    private Clock clock;

    @Test
    void 예약과_결제는_API_호출_전_PENDING으로_저장되고_승인_API_성공시_APPROVED로_갱신된다() {
        // given
        final Member member = memberRepository.save(
                new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        final Theme theme = themeRepository.save(
                new Theme("테마", "설명", "이미지"));
        final ReservationTime time = reservationTimeRepository.save(
                new ReservationTime(LocalTime.of(13, 0)));
        final String orderId = "orderId";
        final long amount = 10_000L;

        orderAmountVerificationCache.register(orderId, amount);
        final CreateReservationWithPaymentCommand command = new CreateReservationWithPaymentCommand(
                LocalDate.now(clock).plusDays(1),
                time.getId(),
                theme.getId(),
                member.getId(),
                "paymentKey",
                orderId,
                amount,
                "NORMAL"
        );
        doNothing().when(tossPaymentClient).approve(command.toPaymentCommand());

        // when
        final Long id = reservationTossPaymentFacade.reserveWithPayment(command);

        // then
        final Optional<Reservation> reservation = reservationRepository.findById(id);
        assertThat(reservation).isPresent();
        final Long reservationId = reservation.get().getId();
        assertThat(reservationId).isEqualTo(id);
        final Optional<ReservationPayment> reservationPayment =
                reservationPaymentRepository.findByReservationId(reservationId);
        assertThat(reservationPayment).isPresent();
        final Long paymentId = reservationPayment.get().getPaymentId();
        final Optional<TossPayment> tossPayment = tossPaymentRepository.findById(paymentId);
        assertThat(tossPayment.get().getPaymentStatus()).isEqualTo(PaymentStatus.APPROVED);
    }

    @Test
    void 승인_API_실패시_FAILED로_갱신된다() {
        // given
        reservationRepository.deleteAll();

        final Member member = memberRepository.save(
                new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        final Theme theme = themeRepository.save(
                new Theme("테마", "설명", "이미지"));
        final ReservationTime time = reservationTimeRepository.save(
                new ReservationTime(LocalTime.of(13, 0)));
        final String orderId = "orderId";
        final long amount = 10_000L;
        orderAmountVerificationCache.register(orderId, amount);

        final CreateReservationWithPaymentCommand command = new CreateReservationWithPaymentCommand(
                LocalDate.now(clock).plusDays(1),
                time.getId(),
                theme.getId(),
                member.getId(),
                "paymentKey",
                orderId,
                amount,
                "NORMAL"
        );
        doThrow(new PaymentException("toss payment server 예외")).when(tossPaymentClient).approve(command.toPaymentCommand());

        // when
        // then
        assertThatCode(() -> reservationTossPaymentFacade.reserveWithPayment(command))
                .isInstanceOf(PaymentException.class)
                .hasMessage("toss payment server 예외");

        final List<Reservation> all = reservationRepository.findAll();
        assertThat(all).hasSize(1);
        final Reservation reservation = all.getFirst();
        final Long reservationId = reservation.getId();
        final Optional<ReservationPayment> reservationPayment =
                reservationPaymentRepository.findByReservationId(reservationId);
        assertThat(reservationPayment).isPresent();
        final Long paymentId = reservationPayment.get().getPaymentId();
        final Optional<TossPayment> tossPayment = tossPaymentRepository.findById(paymentId);
        assertThat(tossPayment.get().getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    @Test
    void 주문한_금액과_승인할_결제_요청의_금액이_다르면_예약할_수_없다() {
        // given
        final Member member = memberRepository.save(
                new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        final Theme theme = themeRepository.save(
                new Theme("테마", "설명", "이미지"));
        final ReservationTime time = reservationTimeRepository.save(
                new ReservationTime(LocalTime.of(13, 0)));
        final String orderId = "orderId";
        final long amount = 10_000L;
        final long invalidAmount = amount + 1;

        orderAmountVerificationCache.register(orderId, invalidAmount);

        final CreateReservationWithPaymentCommand command = new CreateReservationWithPaymentCommand(
                LocalDate.now(clock).plusDays(1),
                time.getId(),
                theme.getId(),
                member.getId(),
                "paymentKey",
                orderId,
                amount,
                "NORMAL"
        );

        // when
        // then
        assertThatCode(() -> reservationTossPaymentFacade.reserveWithPayment(command))
                .isInstanceOf(PaymentException.class)
                .hasMessage("요청 금액과 승인 금액이 일치하지 않습니다. 현재 결제 금액: 10001, 요청 금액: 10000");
    }

    @Test
    void 승인할_결제가_존재하지_않으먄_예약할_수_없다() {
        // given
        final Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        final Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        final ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        final String orderId = "orderId";
        final long amount = 10_000L;

        final CreateReservationWithPaymentCommand command = new CreateReservationWithPaymentCommand(
                LocalDate.now(clock).plusDays(1),
                time.getId(),
                theme.getId(),
                member.getId(),
                "paymentKey",
                orderId,
                amount,
                "NORMAL"
        );

        // when
        // then
        assertThatCode(() -> reservationTossPaymentFacade.reserveWithPayment(command))
                .isInstanceOf(PaymentException.class)
                .hasMessage("존재하지 않는 결제 정보입니다");
    }
}
