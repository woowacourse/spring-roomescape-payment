package roomescape.service.booking.reservation.module;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.domain.waiting.Waiting;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationfilterRequest;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

@Service
public class ReservationSearchService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public ReservationSearchService(ReservationRepository reservationRepository, WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    public ReservationResponse findReservation(Long reservationId) {
        Reservation reservation = findReservationById(reservationId);
        return ReservationResponse.from(reservation);
    }

    public List<ReservationResponse> findAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findReservationsByFilter(ReservationfilterRequest filter) {
        List<Reservation> reservations = reservationRepository.findByMemberOrThemeOrDateRange(
                filter.memberId(),
                filter.themeId(),
                filter.startDate(),
                filter.endDate()
        );

        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<Reservation> findReservationByMemberId(Long memberId) {
        return reservationRepository.findByMemberIdAndStatusIsNot(memberId, Status.DELETE);
    }

    public Reservation findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomEscapeException(
                        "잘못된 예약 정보 입니다.",
                        "reservation_id : " + reservationId
                ));
    }

    public Waiting findWaitingByReservationId(Long reservationId) {
        return waitingRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RoomEscapeException(
                        "예약 정보와 일치하는 대기 정보가 존재하지 않습니다.",
                        "reservation_id : " + reservationId
                ));
    }
}
