package roomescape.reservation.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.ViolationException;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.event.ReservationFailedEvent;
import roomescape.reservation.event.ReservationSavedEvent;

import java.time.LocalDate;
import java.util.List;

public abstract class ReservationManageService {
    protected static final int MAX_RESERVATION_NUMBER_IN_TIME_SLOT = 1;

    protected final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReservationManageService(ReservationRepository reservationRepository,
                                    ApplicationEventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.eventPublisher = eventPublisher;
    }

    abstract protected void correctReservationStatus(int bookingCount, Reservation reservation);

    abstract protected void scheduleAfterDeleting(Reservation deletedReservation);

    abstract protected void validateReservationStatus(Reservation reservation);

    abstract protected void validatePermissionForDeleting(Reservation reservation, Member agent);

    @Transactional
    public Reservation create(Reservation reservation) {
        validateReservationDate(reservation);
        validateDuplicatedReservation(reservation);
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation createWithPayment(Reservation reservation, Payment payment) {
        eventPublisher.publishEvent(new ReservationFailedEvent(payment));
        eventPublisher.publishEvent(new ReservationSavedEvent(payment));
        return create(reservation);
    }

    private void validateReservationDate(Reservation reservation) {
        LocalDate today = LocalDate.now();
        if (reservation.isBeforeOrOnToday(today)) {
            throw new ViolationException("이전 날짜 혹은 당일은 예약할 수 없습니다.");
        }
    }

    private void validateDuplicatedReservation(Reservation reservation) {
        boolean existDuplicatedReservation = reservationRepository.existsByDateAndTimeAndThemeAndMember(
                reservation.getDate(), reservation.getTime(), reservation.getTheme(), reservation.getMember());
        if (existDuplicatedReservation) {
            throw new ViolationException("동일한 사용자의 중복된 예약입니다.");
        }
    }

    @Transactional
    public Reservation scheduleRecentReservation(Reservation reservation) {
        List<Reservation> bookings = reservationRepository.findAllByDateAndTimeAndThemeAndStatus(
                reservation.getDate(), reservation.getTime(), reservation.getTheme(), ReservationStatus.BOOKING);
        correctReservationStatus(bookings.size(), reservation);
        return reservation;
    }

    @Transactional
    public void delete(Long id, Member agent) {
        reservationRepository.findById(id).ifPresent(reservation -> {
            validateReservationStatus(reservation);
            validatePermissionForDeleting(reservation, agent);
            reservationRepository.delete(reservation);
            scheduleAfterDeleting(reservation);
        });
    }
}
