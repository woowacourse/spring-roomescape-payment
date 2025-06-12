package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationSearchFilter;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRepository;
import roomescape.domain.waiting.Waiting;
import roomescape.domain.waiting.WaitingRepository;
import roomescape.exception.AlreadyExistedException;
import roomescape.exception.NotFoundException;
import roomescape.infrastructure.ReservationSpecifications;

@Service
public class ReservationService {

    private final PaymentService paymentService;
    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;

    public ReservationService(
            final PaymentService paymentService,
            final ReservationRepository reservationRepository,
            final WaitingRepository waitingRepository,
            final TimeSlotRepository timeSlotRepository,
            final ThemeRepository themeRepository,
            final UserRepository userRepository
    ) {
        this.paymentService = paymentService;
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.themeRepository = themeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Reservation saveReservationWithAdminPrivileges(final long userId,
                                                          final LocalDate date,
                                                          final long timeId,
                                                          final long themeId) {
        User user = getUserById(userId);
        TimeSlot timeSlot = getTimeSlotById(timeId);
        Theme theme = getThemeById(themeId);
        validateDuplicateReservation(date, timeSlot, theme);

        Reservation reservation = Reservation.registerWithAdminPrivileges(user, date, timeSlot, theme);

        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation saveReservationWithUserPrivileges(final PaymentInfo paymentInfo,
                                                         final long userId,
                                                         final LocalDate date,
                                                         final long timeId,
                                                         final long themeId) {
        User user = getUserById(userId);
        TimeSlot timeSlot = getTimeSlotById(timeId);
        Theme theme = getThemeById(themeId);
        validateDuplicateReservation(date, timeSlot, theme);

        Payment payment = paymentService.savePayment(paymentInfo);

        Reservation reservation = Reservation.registerWithUserPrivileges(user, date, timeSlot, theme, payment);

        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findReservationsByFilter(ReservationSearchFilter filter) {
        return reservationRepository.findAll(ReservationSpecifications.byFilter(filter));
    }

    @Transactional
    public void removeById(final long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다."));

        approveNextWaitingIfExists(reservation);

        reservationRepository.deleteById(id);
    }

    private void validateDuplicateReservation(final LocalDate date, final TimeSlot timeSlot, final Theme theme) {
        Optional<Reservation> reservation =
                reservationRepository.findByDateAndTimeSlotIdAndThemeId(date, timeSlot.id(), theme.id());

        if (reservation.isPresent()) {
            throw new AlreadyExistedException("이미 예약된 날짜, 시간, 테마에 대한 예약은 불가능합니다.");
        }
    }

    private User getUserById(final long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private TimeSlot getTimeSlotById(final long timeId) {
        return timeSlotRepository.findById(timeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 타임 슬롯입니다."));
    }

    private Theme getThemeById(final long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다."));
    }

    private void approveNextWaitingIfExists(Reservation reservation) {
        Optional<Waiting> nextWaitingOpt =
                waitingRepository.findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(
                        reservation.date(),
                        reservation.timeSlot().id(),
                        reservation.theme().id());

        nextWaitingOpt.ifPresent(nextWaiting -> {
            Reservation approvedReservation = Reservation.fromWaiting(nextWaiting);
            reservationRepository.save(approvedReservation);
            waitingRepository.deleteById(nextWaiting.id());
        });
    }
}
