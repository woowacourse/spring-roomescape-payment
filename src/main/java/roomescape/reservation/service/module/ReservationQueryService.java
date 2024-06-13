package roomescape.reservation.service.module;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.reservation.controller.dto.request.ReservationSearchCondRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationWithPayment;
import roomescape.reservation.repository.ReservationRepository;

@Service
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;

    public ReservationQueryService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public List<Reservation> findAllWithSearchCond(ReservationSearchCondRequest request) {
        return reservationRepository.findAllByThemeIdAndMemberIdAndDateBetween(
                request.themeId(),
                request.memberId(),
                request.dateFrom(),
                request.dateTo()
        );
    }

    public List<ReservationWithPayment> findReservationWithPaymentsByMemberId(Long memberId) {
        return reservationRepository.findReservationWithPaymentsByMemberId(memberId);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }
}
