package roomescape.business.service;

import static roomescape.exception.ErrorCode.RESERVATION_DUPLICATED;
import static roomescape.exception.ErrorCode.RESERVATION_NOT_EXIST;
import static roomescape.exception.ErrorCode.THEME_NOT_EXIST;
import static roomescape.exception.ErrorCode.USER_NOT_EXIST;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.dto.PaymentApproveDto;
import roomescape.business.dto.ReservationDto;
import roomescape.business.dto.ReservationSpecDto;
import roomescape.business.dto.ReservationWithAheadDto;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.entity.ReservationTime;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.entity.User;
import roomescape.business.model.repository.ReservationRepository;
import roomescape.business.model.repository.ReservationTimeRepository;
import roomescape.business.model.repository.ThemeRepository;
import roomescape.business.model.repository.UserRepository;
import roomescape.business.model.vo.Id;
import roomescape.business.model.vo.ReservationStatus;
import roomescape.exception.business.DuplicatedException;
import roomescape.exception.business.NotFoundException;
import roomescape.presentation.dto.response.ReservationResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final WaitingService waitingService;
    private final PaymentService paymentService;

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationDto addAndGet(final ReservationSpecDto reservationSpecDto,
                                    final PaymentApproveDto paymentApproveDto) {
        User user = getUser(reservationSpecDto.userIdValue());
        ReservationTime reservationTime = getReservationTime(reservationSpecDto.timeIdValue());
        Theme theme = getTheme(reservationSpecDto.themeIdValue());
        ReservationStatus reservationStatus = reservationSpecDto.reservationStatus();

        if (reservationStatus == ReservationStatus.RESERVED &&
                reservationRepository.isDuplicateDateAndTimeAndTheme(reservationSpecDto.date(),
                        reservationTime.startTimeValue(),
                        theme.getId())) {
            throw new DuplicatedException(RESERVATION_DUPLICATED);
        }
        Reservation reservation = Reservation.create(user, reservationSpecDto.date(), reservationTime, theme,
                reservationStatus, LocalDateTime.now());
        reservationRepository.save(reservation);
        if (reservationStatus == ReservationStatus.RESERVED && paymentApproveDto != null) {
            paymentService.pay(reservation, paymentApproveDto);
        }
        return ReservationDto.fromEntity(reservation);
    }

    public ReservationDto addAndGetWithoutPayment(final ReservationSpecDto reservationSpecDto) {
        return addAndGet(reservationSpecDto, null);
    }

    private Theme getTheme(String themeIdValue) {
        return themeRepository.findById(Id.create(themeIdValue))
                .orElseThrow(() -> new NotFoundException(THEME_NOT_EXIST));
    }

    private ReservationTime getReservationTime(String timeIdValue) {
        return reservationTimeRepository.findById(Id.create(timeIdValue))
                .orElseThrow(() -> new NotFoundException(RESERVATION_NOT_EXIST));
    }

    private User getUser(String userIdValue) {
        return userRepository.findById(Id.create(userIdValue))
                .orElseThrow(() -> new NotFoundException(USER_NOT_EXIST));
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAll(final String themeIdValue, final String userIdValue, final LocalDate dateFrom,
                                       final LocalDate dateTo) {
        List<Reservation> reservations = reservationRepository.findAllReservationWithFilter(Id.create(themeIdValue),
                Id.create(userIdValue), dateFrom, dateTo, ReservationStatus.RESERVED);
        return ReservationDto.fromEntities(reservations);
    }

    public void deleteAndUpdateWaiting(final String reservationId) {
        Id id = Id.create(reservationId);
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(RESERVATION_NOT_EXIST));
        delete(id);
        waitingService.updateWaitingReservations(reservation);
    }

    public void delete(final Id reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    @Transactional(readOnly = true)
    public List<ReservationWithAheadDto> getMyReservations(final String userIdValue) {
        Id userId = Id.create(userIdValue);
        return reservationRepository.findReservationsWithAhead(userId);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllWaitingReservations() {
        return waitingService.getAllWaitingReservations();
    }
}
