package roomescape.application.reservation.command;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.client.TossPaymentClient;
import roomescape.application.payment.client.dto.PaymentResponse;
import roomescape.application.reservation.command.dto.CreateReservationCommand;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;
import roomescape.domain.member.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.repository.PaymentRepository;
import roomescape.domain.payment.repository.ReservationPaymentRepository;
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

@Service
@Transactional
public class CreateReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;
    private final ReservationPaymentRepository reservationPaymentRepository;
    private final Clock clock;

    public CreateReservationService(ReservationRepository reservationRepository,
                                    ReservationTimeRepository reservationTimeRepository,
                                    ThemeRepository themeRepository,
                                    MemberRepository memberRepository,
                                    TossPaymentClient tossPaymentClient,
                                    PaymentRepository paymentRepository,
                                    ReservationPaymentRepository reservationPaymentRepository,
                                    Clock clock) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.tossPaymentClient = tossPaymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationPaymentRepository = reservationPaymentRepository;
        this.clock = clock;
    }

    public Long reserve(CreateReservationCommand command) {
        Reservation savedReservation = createAndSaveReservation(
                command.memberId(),
                command.timeId(),
                command.themeId(),
                command.date()
        );
        return savedReservation.getId();
    }

    public Long reserve(CreateReservationWithPaymentCommand command) {
        Reservation savedReservation = createAndSaveReservation(
                command.memberId(),
                command.timeId(),
                command.themeId(),
                command.date()
        );
        Payment payment = getPayment(command.orderId());
        payment.validateApprovalAmount(command.amount());
        PaymentResponse approveResponse = tossPaymentClient.approve(command.getPaymentCommand());
        payment.approvePayment(approveResponse.paymentKey());
        reservationPaymentRepository.save(new ReservationPayment(savedReservation, payment));
        return savedReservation.getId();
    }

    private Reservation createAndSaveReservation(Long memberId, Long timeId, Long themeId, LocalDate date) {
        Member member = getMember(memberId);
        ReservationTime time = getTime(timeId);
        Theme theme = getTheme(themeId);
        validateDuplicateReservation(date, time, theme);
        Reservation reservation = new Reservation(member, date, time, theme);
        reservation.validateReservable(LocalDateTime.now(clock));
        return reservationRepository.save(reservation);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
    }

    private ReservationTime getTime(Long timeId) {
        return reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new ReservationTimeException("존재하지 않는 예약 시간입니다."));
    }

    private Theme getTheme(Long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new ThemeException("존재하지 않는 테마입니다."));
    }

    private Payment getPayment(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentException("존재하지 않는 결제입니다."));
    }

    private void validateDuplicateReservation(LocalDate date, ReservationTime time, Theme theme) {
        boolean duplicated = reservationRepository.existsByDateAndTimeIdAndThemeId(date, time.getId(), theme.getId());
        if (duplicated) {
            throw new ReservationException("날짜와 시간이 중복된 예약이 존재합니다.");
        }
    }
}
