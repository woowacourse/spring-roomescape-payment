package roomescape.mvc.reservation.service;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.BadRequestException;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.service.MemberQueryService;
import roomescape.mvc.payment.domain.Payment;
import roomescape.mvc.payment.service.PaymentQueryService;
import roomescape.mvc.reservation.domain.Reservation;
import roomescape.mvc.reservation.dto.ReservationCreationContent;
import roomescape.mvc.reservation.repository.ReservationRepository;
import roomescape.mvc.reservation.response.AddReservationByAdmin;
import roomescape.mvc.reservation.response.AddReservationByMember;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.theme.service.ThemeQueryService;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.mvc.time.service.ReservationTimeQueryService;
import roomescape.mvc.waiting.domain.Waiting;
import roomescape.mvc.waiting.repository.WaitingRepository;
import roomescape.mvc.waiting.service.WaitingQueryService;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentQueryService paymentQueryService;
    private final MemberQueryService memberQueryService;
    private final ThemeQueryService themeQueryService;
    private final ReservationTimeQueryService timeQueryService;
    private final ReservationQueryService reservationQueryService;
    private final WaitingQueryService waitingQueryService;

    public ReservationService(
            ReservationRepository reservationRepository,
            WaitingRepository waitingRepository,
            PaymentQueryService paymentQueryService,
            MemberQueryService memberQueryService,
            ThemeQueryService themeQueryService,
            ReservationTimeQueryService timeQueryService,
            ReservationQueryService reservationQueryService,
            WaitingQueryService waitingQueryService
    ) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.paymentQueryService = paymentQueryService;
        this.memberQueryService = memberQueryService;
        this.themeQueryService = themeQueryService;
        this.timeQueryService = timeQueryService;
        this.reservationQueryService = reservationQueryService;
        this.waitingQueryService = waitingQueryService;
    }

    public AddReservationByAdmin addReservationWithoutPayment(
            long memberId,
            ReservationCreationContent request
    ) {
        Member member = memberQueryService.getMemberById(memberId);
        Theme theme = themeQueryService.getThemeById(request.themeId());
        ReservationTime time = timeQueryService.getReservationTimeById(request.timeId());

        Reservation reservation = Reservation.createWithoutIdAndPaymentHistory(
                request.date(), time, theme, member);

        validateDuplicateReservation(reservation.getTheme(), reservation.getDate(), reservation.getReservationTime());
        validatePastReservationCreation(reservation);

        Reservation savedReservation = reservationRepository.save(reservation);
        return new AddReservationByAdmin(savedReservation);
    }

    public AddReservationByMember addReservationWithPayment(
            long memberId,
            long paymentId,
            ReservationCreationContent reservationCreationContent
    ) {
        Payment payment = paymentQueryService.getPaymentById(paymentId);
        Member member = memberQueryService.getMemberById(memberId);
        Theme theme = themeQueryService.getThemeById(reservationCreationContent.themeId());
        ReservationTime time = timeQueryService.getReservationTimeById(reservationCreationContent.timeId());

        Reservation reservation = Reservation.createWithoutId(
                reservationCreationContent.date(), time, theme, member, payment);

        validateDuplicateReservation(reservation.getTheme(), reservation.getDate(), reservation.getReservationTime());
        validatePastReservationCreation(reservation);

        Reservation savedReservation = reservationRepository.save(reservation);
        return new AddReservationByMember(savedReservation);
    }

    public void deleteReservationById(long reservationId) {
        Reservation reservation = reservationQueryService.getReservationById(reservationId);
        reservationRepository.delete(reservation);
        replaceWaitingToReservation(reservation);
    }

    private void replaceWaitingToReservation(Reservation deletedReservation) {
        Optional<Waiting> firstWaiting = waitingQueryService.findFirstWaitingByReservation(deletedReservation);
        if (firstWaiting.isEmpty()) {
            return;
        }
        Waiting waiting = firstWaiting.get();
        addReservationWithWaiting(waiting.getMemberIdInWaiting(), waiting);
        waitingRepository.delete(waiting);
    }

    private void validateDuplicateReservation(Theme theme, LocalDate date, ReservationTime time) {
        boolean isDuplicatedReservation = reservationQueryService.existsAlreadyReservation(theme, date, time);
        if (isDuplicatedReservation) {
            throw new BadRequestException("중복된 예약 입니다.");
        }
    }

    private void validatePastReservationCreation(Reservation reservation) {
        if (reservation.isPastDateTime()) {
            throw new BadRequestException("과거 예약은 생성할 수 없습니다.");
        }
    }

    private void addReservationWithWaiting(long memberId, Waiting waiting) {
        ReservationCreationContent creationContent = new ReservationCreationContent(waiting);
        if (waiting.hasEmptyPayment()) {
            addReservationWithoutPayment(memberId, creationContent);
            return;
        }
        addReservationWithPayment(memberId, waiting.getPaymentIdInWaiting(), creationContent);
    }
}
