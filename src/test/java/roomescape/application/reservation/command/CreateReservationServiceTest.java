package roomescape.application.reservation.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.application.AbstractServiceIntegrationTest;
import roomescape.application.payment.TossPaymentClient;
import roomescape.application.reservation.command.dto.CreateReservationCommand;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRole;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.repository.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationTime;
import roomescape.domain.reservation.Theme;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;
import roomescape.domain.reservation.repository.ThemeRepository;
import roomescape.infrastructure.error.exception.MemberException;
import roomescape.infrastructure.error.exception.PaymentException;
import roomescape.infrastructure.error.exception.ReservationException;
import roomescape.infrastructure.error.exception.ReservationTimeException;
import roomescape.infrastructure.error.exception.ThemeException;

class CreateReservationServiceTest extends AbstractServiceIntegrationTest {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private TossPaymentClient tossPaymentClient;

    private CreateReservationService createReservationService;

    @BeforeEach
    void setUp() {
        createReservationService = new CreateReservationService(
                reservationRepository,
                reservationTimeRepository,
                themeRepository,
                memberRepository,
                tossPaymentClient,
                paymentRepository,
                clock
        );
    }

    @Test
    void 결제_이후_예약을_생성할_수_있다() {
        // given
        Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        String orderId = "orderId";
        long amount = 10_000L;
        CreateReservationWithPaymentCommand command = new CreateReservationWithPaymentCommand(
                LocalDate.now(clock).plusDays(1),
                time.getId(),
                theme.getId(),
                member.getId(),
                "paymentKey",
                orderId,
                amount,
                "NORMAL"
        );
        doNothing().when(tossPaymentClient).approve(command.getPaymentCommand());
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(new Payment(orderId, amount)));

        // when
        Long id = createReservationService.reserve(command);

        // then
        assertThat(reservationRepository.findById(id)).isPresent();
    }

    @Test
    void 결제에서_문제가_생기면_예약을_생성할_수_없다() {
        // given
        Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        String orderId = "orderId";
        long amount = 10_000L;
        CreateReservationWithPaymentCommand command = new CreateReservationWithPaymentCommand(
                LocalDate.now(clock).plusDays(1),
                time.getId(),
                theme.getId(),
                member.getId(),
                "paymentKey",
                orderId,
                amount,
                "NORMAL"
        );
        doThrow(new PaymentException("toss payment server 예외"))
                .when(tossPaymentClient)
                .approve(command.getPaymentCommand());
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(new Payment(orderId, amount)));

        // when
        // then
        assertThatCode(() -> createReservationService.reserve(command))
                .isInstanceOf(PaymentException.class)
                .hasMessage("toss payment server 예외");
    }

    @Test
    void 주문한_금액과_승인할_결제_요청의_금액이_다르면_예약할_수_없다() {
        // given
        Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        String orderId = "orderId";
        long amount = 10_000L;
        long invalidAmount = amount + 1;
        CreateReservationWithPaymentCommand command = new CreateReservationWithPaymentCommand(
                LocalDate.now(clock).plusDays(1),
                time.getId(),
                theme.getId(),
                member.getId(),
                "paymentKey",
                orderId,
                amount,
                "NORMAL"
        );
        doThrow(new PaymentException("toss payment server 예외"))
                .when(tossPaymentClient)
                .approve(command.getPaymentCommand());
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(new Payment(orderId, invalidAmount)));

        // when
        // then
        assertThatCode(() -> createReservationService.reserve(command))
                .isInstanceOf(PaymentException.class)
                .hasMessage("요청 금액과 승인 금액이 일치하지 않습니다. 현재 결제 금액: 10001, 요청 금액: 10000");
    }

    @Test
    void 승인할_결제가_존재하지_않으먄_예약할_수_없다() {
        // given
        Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        String orderId = "orderId";
        long amount = 10_000L;
        CreateReservationWithPaymentCommand command = new CreateReservationWithPaymentCommand(
                LocalDate.now(clock).plusDays(1),
                time.getId(),
                theme.getId(),
                member.getId(),
                "paymentKey",
                orderId,
                amount,
                "NORMAL"
        );
        doThrow(new PaymentException("toss payment server 예외"))
                .when(tossPaymentClient)
                .approve(command.getPaymentCommand());
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatCode(() -> createReservationService.reserve(command))
                .isInstanceOf(PaymentException.class)
                .hasMessage("존재하지 않는 결제입니다.");
    }

    @Test
    void 예약을_생성할_수_있다() {
        // given
        Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));

        CreateReservationCommand command = new CreateReservationCommand(LocalDate.now(clock).plusDays(1), time.getId(),
                theme.getId(), member.getId());

        // when
        Long id = createReservationService.reserve(command);

        // then
        assertThat(reservationRepository.findById(id)).isPresent();
    }

    @Test
    void 존재하지_않는_회원으로_예약할_수_없다() {
        // given
        Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        CreateReservationCommand command = new CreateReservationCommand(
                LocalDate.now(clock),
                time.getId(),
                theme.getId(),
                999L
        );

        // when
        // then
        assertThatThrownBy(() -> createReservationService.reserve(command))
                .isInstanceOf(MemberException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    void 존재하지_않는_예약시간으로_예약할_수_없다() {
        // given
        Long invalidTimeId = 999L;
        Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        CreateReservationCommand command = new CreateReservationCommand(
                LocalDate.now(clock),
                invalidTimeId,
                theme.getId(),
                member.getId()
        );

        // when
        // then
        assertThatThrownBy(() -> createReservationService.reserve(command))
                .isInstanceOf(ReservationTimeException.class)
                .hasMessage("존재하지 않는 예약 시간입니다.");
    }

    @Test
    void 존재하지_않는_테마로_예약할_수_없다() {
        // given
        Long invalidThemeId = 999L;
        Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        CreateReservationCommand command = new CreateReservationCommand(LocalDate.now(clock), time.getId(),
                invalidThemeId,
                member.getId());

        // when
        // then
        assertThatThrownBy(() -> createReservationService.reserve(command))
                .isInstanceOf(ThemeException.class)
                .hasMessage("존재하지 않는 테마입니다.");
    }

    @Test
    void 같은_날짜와_같은_시간과_같은_테마에_예약이_존재한다면_예약을_생성하면_예외가_발생한다() {
        // given
        Member member = memberRepository.save(new Member("벨로", new Email("test@email.com"), "pw", MemberRole.NORMAL));
        Theme theme = themeRepository.save(new Theme("테마", "설명", "이미지"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(13, 0)));
        reservationRepository.save(new Reservation(member, LocalDate.now(clock), time, theme));
        CreateReservationCommand command = new CreateReservationCommand(
                LocalDate.now(clock),
                time.getId(),
                theme.getId(),
                member.getId()
        );

        // when
        // then
        assertThatThrownBy(() -> createReservationService.reserve(command))
                .isInstanceOf(ReservationException.class)
                .hasMessage("날짜와 시간이 중복된 예약이 존재합니다.");
    }
}
