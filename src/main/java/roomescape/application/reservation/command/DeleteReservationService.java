package roomescape.application.reservation.command;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.reservation.event.ReservationCancelEvent;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.repository.ReservationRepository;

@Service
@Transactional
public class DeleteReservationService {

    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public DeleteReservationService(final ReservationRepository reservationRepository,
                                    final ApplicationEventPublisher applicationEventPublisher) {
        this.reservationRepository = reservationRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void cancelById(final Long reservationId) {
        final Reservation reservation = getReservation(reservationId);
        reservationRepository.delete(reservation);
        publishCancelEvent(reservation);
    }

    private Reservation getReservation(final Long reservationId) {
        return reservationRepository.findByIdWithTimeAndTheme(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 존재하지 않습니다."));
    }

    private void publishCancelEvent(final Reservation reservation) {
        applicationEventPublisher.publishEvent(createCancelEvent(reservation));
    }

    private ReservationCancelEvent createCancelEvent(final Reservation reservation) {
        return new ReservationCancelEvent(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId()
        );
    }
}
