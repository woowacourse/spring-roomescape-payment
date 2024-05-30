package roomescape.service.reservationwaiting;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationInfo;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.reservationwaiting.ReservationWaitingRepository;
import roomescape.exception.reservation.NotFoundReservationException;
import roomescape.exception.reservationwaiting.CannotCreateWaitingForOwnedReservationException;
import roomescape.exception.reservationwaiting.DuplicatedReservationWaitingException;
import roomescape.exception.reservationwaiting.InvalidDateTimeWaitingException;
import roomescape.exception.reservationwaiting.NotFoundReservationWaitingException;
import roomescape.service.reservationwaiting.dto.ReservationWaitingListResponse;
import roomescape.service.reservationwaiting.dto.ReservationWaitingRequest;
import roomescape.service.reservationwaiting.dto.ReservationWaitingResponse;

@Service
@Transactional
public class ReservationWaitingService {
    private final ReservationWaitingRepository reservationWaitingRepository;
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    public ReservationWaitingService(ReservationWaitingRepository reservationWaitingRepository,
                                     ReservationRepository reservationRepository,
                                     Clock clock) {
        this.reservationWaitingRepository = reservationWaitingRepository;
        this.reservationRepository = reservationRepository;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public ReservationWaitingListResponse findAllReservationWaiting() {
        List<ReservationWaiting> waitings = reservationWaitingRepository.findAll();
        return new ReservationWaitingListResponse(waitings.stream()
                .map(ReservationWaitingResponse::new)
                .toList());
    }

    public ReservationWaitingResponse saveReservationWaiting(ReservationWaitingRequest request, Member member) {
        ReservationInfo info = new ReservationInfo(request.getDate(), request.getTimeId(), request.getThemeId());
        Reservation reservation = findByInfo(info);
        validateReservationForWaiting(reservation, member);

        ReservationWaiting reservationWaiting = new ReservationWaiting(reservation, member);
        ReservationWaiting savedReservationWaiting = reservationWaitingRepository.save(reservationWaiting);
        return new ReservationWaitingResponse(savedReservationWaiting);
    }

    private Reservation findByInfo(ReservationInfo info) {
        return reservationRepository.findByInfo(info)
                .orElseThrow(NotFoundReservationException::new);
    }

    private void validateReservationForWaiting(Reservation reservation, Member member) {
        validateOwnedReservation(reservation, member);
        validateDuplicateWaiting(reservation, member);
        validateDateTimeWaiting(reservation);
    }

    private void validateOwnedReservation(Reservation reservation, Member member) {
        if (reservation.isOwnedBy(member)) {
            throw new CannotCreateWaitingForOwnedReservationException();
        }
    }

    private void validateDuplicateWaiting(Reservation reservation, Member member) {
        if (reservationWaitingRepository.existsByReservationAndMember(reservation, member)) {
            throw new DuplicatedReservationWaitingException();
        }
    }

    private void validateDateTimeWaiting(Reservation reservation) {
        if (reservation.isPast(LocalDateTime.now(clock))) {
            throw new InvalidDateTimeWaitingException();
        }
    }

    public void deleteReservationWaiting(Long reservationId, Member member) {
        ReservationWaiting waiting = findReservationWaitingByReservationIdAndMember(reservationId, member.getId());
        reservationWaitingRepository.delete(waiting);
    }

    private ReservationWaiting findReservationWaitingByReservationIdAndMember(Long reservationId, Long memberId) {
        return reservationWaitingRepository.findByReservationIdAndMemberId(reservationId, memberId)
                .orElseThrow(NotFoundReservationWaitingException::new);
    }

    public void deleteAdminReservationWaiting(Long waitingId) {
        ReservationWaiting waiting = findReservationWaitingById(waitingId);
        reservationWaitingRepository.delete(waiting);
    }

    private ReservationWaiting findReservationWaitingById(Long id) {
        return reservationWaitingRepository.findById(id)
                .orElseThrow(NotFoundReservationWaitingException::new);
    }
}
