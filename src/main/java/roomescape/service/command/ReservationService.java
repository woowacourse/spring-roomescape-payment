package roomescape.service.command;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.Waiting;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.dto.business.ReservationCreationContent;
import roomescape.dto.response.ReservationResponse;
import roomescape.exception.BadRequestException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;
import roomescape.service.query.MemberQueryService;
import roomescape.service.query.ReservationQueryService;
import roomescape.service.query.ReservationTimeQueryService;
import roomescape.service.query.ThemeQueryService;
import roomescape.service.query.WaitingQueryService;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentService paymentService;
    private final MemberQueryService memberQueryService;
    private final ThemeQueryService themeQueryService;
    private final ReservationTimeQueryService timeQueryService;
    private final ReservationQueryService reservationQueryService;
    private final WaitingQueryService waitingQueryService;

    public ReservationService(
            ReservationRepository reservationRepository,
            WaitingRepository waitingRepository,
            MemberQueryService memberQueryService,
            ThemeQueryService themeQueryService,
            ReservationTimeQueryService timeQueryService,
            PaymentService paymentService, ReservationQueryService reservationQueryService,
            WaitingQueryService waitingQueryService
    ) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.memberQueryService = memberQueryService;
        this.themeQueryService = themeQueryService;
        this.timeQueryService = timeQueryService;
        this.paymentService = paymentService;
        this.reservationQueryService = reservationQueryService;
        this.waitingQueryService = waitingQueryService;
    }

    public ReservationResponse addReservation(
            long memberId,
            ReservationCreationContent request
    ) {
        Member member = memberQueryService.getMemberById(memberId);
        Theme theme = themeQueryService.getThemeById(request.themeId());
        ReservationTime time = timeQueryService.getReservationTimeById(request.timeId());

        Reservation reservation = Reservation.createWithoutIdAndPaymentHistory(
                request.date(), time, theme, member);

        return saveReservation(reservation);
    }

    public ReservationResponse addReservation(
            long memberId,
            ReservationCreationContent reservationCreationContent,
            PaymentHistoryCreationContent paymentHistoryCreationContent
    ) {
        Member member = memberQueryService.getMemberById(memberId);
        Theme theme = themeQueryService.getThemeById(reservationCreationContent.themeId());
        ReservationTime time = timeQueryService.getReservationTimeById(reservationCreationContent.timeId());

        Payment paymentHistory = paymentService.savePayment(paymentHistoryCreationContent);
        Reservation reservation = Reservation.createWithoutId(reservationCreationContent.date(), time, theme, member,
                paymentHistory);

        return saveReservation(reservation);
    }

    public void deleteReservationById(long reservationId) {
        Reservation reservation = reservationQueryService.getReservationById(reservationId);
        reservationRepository.delete(reservation);
        addReservationWithWaiting(reservation);
    }

    private void addReservationWithWaiting(Reservation deletedReservation) {
        Optional<Waiting> firstWaiting = waitingQueryService.findFirstWaitingByReservation(deletedReservation);
        if (firstWaiting.isPresent()) {
            Waiting waiting = firstWaiting.get();
            long memberId = waiting.getMember().getId();
            ReservationCreationContent creationContent = new ReservationCreationContent(waiting);
            addReservation(memberId, creationContent);
            waitingRepository.delete(waiting);
        }
    }

    private ReservationResponse saveReservation(Reservation newReservation) {
        validateDuplicateReservation(
                newReservation.getTheme(), newReservation.getDate(), newReservation.getReservationTime());
        validatePastReservationCreation(newReservation);
        Reservation savedReservation = reservationRepository.save(newReservation);
        return new ReservationResponse(savedReservation);
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
}
