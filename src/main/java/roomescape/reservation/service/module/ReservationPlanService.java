package roomescape.reservation.service.module;

import java.util.Optional;
import org.springframework.stereotype.Service;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;

@Service
public class ReservationPlanService {

    private final ReservationRepository reservationRepository;

    public ReservationPlanService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void validateSaveReservation(Reservation reservation) {
        validateMemberReservationUnique(reservation);
        validateReservationAvailable(reservation);
    }

    private void validateMemberReservationUnique(Reservation reservation) {
        if (findMemberReservation(reservation).isPresent()) {
            throw new IllegalArgumentException("이미 회원이 예약한 내역이 있습니다.");
        }
    }

    private void validateReservationAvailable(Reservation reservation) {
        if (findReservation(reservation).isPresent()) {
            throw new IllegalArgumentException("이미 예약이 다 찼습니다. 예약 대기를 걸어주세요.");
        }
    }

    private Optional<Reservation> findMemberReservation(Reservation reservation) {
        return reservationRepository.findFirstByDateAndReservationTimeAndThemeAndMember(
                reservation.getDate(),
                reservation.getTime(),
                reservation.getTheme(),
                reservation.getMember()
        );
    }

    private Optional<Reservation> findReservation(Reservation reservation) {
        return reservationRepository.findFirstByDateAndReservationTimeAndTheme(
                reservation.getDate(),
                reservation.getTime(),
                reservation.getTheme()
        );
    }
}
