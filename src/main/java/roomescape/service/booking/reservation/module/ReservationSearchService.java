package roomescape.service.booking.reservation.module;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.waiting.Waiting;
import roomescape.dto.reservation.ReservationFilter;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.UserReservationResponse;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

@Service
@Transactional(readOnly = true)
public class ReservationSearchService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public ReservationSearchService(ReservationRepository reservationRepository, WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    public ReservationResponse findReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);
        return ReservationResponse.from(reservation);
    }

    public List<ReservationResponse> findAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findReservationsByFilter(ReservationFilter filter) {
        List<Reservation> reservations = reservationRepository.findByMemberOrThemeOrDateRange(
                filter.member(),
                filter.theme(),
                filter.dateFrom(),
                filter.dateTo()
        );

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<UserReservationResponse> findReservationByMemberId(Long memberId) {
        return reservationRepository.findByMemberId(memberId)
                .stream()
                .map(this::createUserReservationResponse)
                .toList();
    }

    private UserReservationResponse createUserReservationResponse(Reservation reservation) {
        if (reservation.isReserved()) {
            return UserReservationResponse.create(reservation);
        }

        Waiting waiting = waitingRepository.findByReservationIdOrThrow(reservation.getId());
        return UserReservationResponse.createByWaiting(waiting);
    }
}
