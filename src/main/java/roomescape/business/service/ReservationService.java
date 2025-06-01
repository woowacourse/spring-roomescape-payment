package roomescape.business.service;

import static roomescape.exception.ErrorCode.RESERVATION_DUPLICATED;
import static roomescape.exception.ErrorCode.RESERVATION_NOT_EXIST;
import static roomescape.exception.ErrorCode.THEME_NOT_EXIST;
import static roomescape.exception.ErrorCode.USER_NOT_EXIST;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.entity.ReservationTime;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.User;
import roomescape.business.model.entity.Waiting;
import roomescape.business.model.vo.Id;
import roomescape.business.model.vo.ReservationDate;
import roomescape.exception.business.DuplicatedException;
import roomescape.exception.business.NotFoundException;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.infrastructure.ThemeRepository;
import roomescape.infrastructure.UserRepository;
import roomescape.infrastructure.WaitingRepository;
import roomescape.infrastructure.payment.TossPaymentClient;
import roomescape.infrastructure.payment.dto.PaymentApproveDto;
import roomescape.presentation.dto.response.ReservationResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final WaitingService waitingService;

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final WaitingRepository waitingRepository;

    private final TossPaymentClient paymentClient;

    public ReservationResponse addAndGet(final LocalDate date, final String timeIdValue, final String themeIdValue,
                                         final String userIdValue, final String paymentKey, final String orderId,
                                         final Long amount) {
        User user = getUser(userIdValue);
        ReservationTime reservationTime = getReservationTime(timeIdValue);
        Theme theme = getTheme(themeIdValue);

        if (reservationRepository.existsByDate_ValueAndTime_StartTime_ValueAndThemeId(date,
                reservationTime.startTimeValue(),
                theme.getId())) {
            throw new DuplicatedException(RESERVATION_DUPLICATED);
        }
        Reservation reservation = Reservation.create(user, date, reservationTime, theme);
        paymentClient.approvePayment(new PaymentApproveDto(paymentKey, orderId, amount));
        reservationRepository.save(reservation);
        return ReservationResponse.from(reservation);
    }

    public ReservationResponse addAndGetWithoutPayment(final LocalDate date, final String timeIdValue,
                                                       final String themeIdValue,
                                                       final String userIdValue) {
        User user = getUser(userIdValue);
        ReservationTime reservationTime = getReservationTime(timeIdValue);
        Theme theme = getTheme(themeIdValue);

        if (reservationRepository.existsByDate_ValueAndTime_StartTime_ValueAndThemeId(date,
                reservationTime.startTimeValue(),
                theme.getId())) {
            throw new DuplicatedException(RESERVATION_DUPLICATED);
        }
        Reservation reservation = Reservation.create(user, date, reservationTime, theme);
        reservationRepository.save(reservation);
        return ReservationResponse.from(reservation);
    }

    private Theme getTheme(String themeIdValue) {
        Theme theme = themeRepository.findById(Id.create(themeIdValue))
                .orElseThrow(() -> new NotFoundException(THEME_NOT_EXIST));
        return theme;
    }

    private ReservationTime getReservationTime(String timeIdValue) {
        ReservationTime reservationTime = reservationTimeRepository.findById(Id.create(timeIdValue))
                .orElseThrow(() -> new NotFoundException(RESERVATION_NOT_EXIST));
        return reservationTime;
    }

    private User getUser(String userIdValue) {
        User user = userRepository.findById(Id.create(userIdValue))
                .orElseThrow(() -> new NotFoundException(USER_NOT_EXIST));
        return user;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAllReservations(final String themeIdValue, final String userIdValue,
                                                         final LocalDate dateFrom,
                                                         final LocalDate dateTo) {
        return reservationRepository.findAllReservationWithFilter(Id.create(themeIdValue),
                        Id.create(userIdValue), dateFrom, dateTo)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void cancelReservationAndPromoteWait(String id) {
        Reservation reservation = reservationRepository.findById(Id.create(id))
                .orElseThrow(() -> new NotFoundException(RESERVATION_NOT_EXIST));
        reservationRepository.delete(reservation);
        ReservationTime time = reservation.getTime();
        ReservationDate date = reservation.getDate();
        waitingRepository.findFirstByDateAndTimeAndThemeOrderById(date, time, reservation.getTheme())
                .ifPresent(this::promoteWaitingToReservation);
    }

    private void promoteWaitingToReservation(Waiting waiting) {
        Reservation newReservation = waiting.convertToReservation();
        reservationRepository.save(newReservation);
        waitingRepository.delete(waiting);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(final String userIdValue) {
        Id userId = Id.create(userIdValue);
        return reservationRepository.findAllReservationWithFilter(null, userId, null, null)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
