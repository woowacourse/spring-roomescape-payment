package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.event.ReservationCancelledEvent;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.reserved.ReservedRepository;
import roomescape.domain.reservation.reserved.ReservedSearchFilter;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRepository;
import roomescape.exception.AlreadyExistedException;
import roomescape.exception.NotFoundException;
import roomescape.infrastructure.ReservedSpecifications;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservedService {

    private final ReservedRepository reservedRepository;
    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public Reserved saveReservedWithPurchase(
            final long userId, final LocalDate date, final long timeId,
            final long themeId, final PaymentInfo paymentInfo
    ) {
        log.info("예약 등록 호출 - userId: {}, date: {}, timeId: {}, themeId: {}", userId, date, timeId, themeId);

        Reserved reserved = registerReserved(userId, date, timeId, themeId);
        paymentService.requestPayment(reserved, paymentInfo);

        log.info("예약 등록 성공 - 예약ID: {}, userId: {}", reserved.getId(), userId);
        return reserved;
    }

    @Transactional
    public Reserved saveReservedWithoutPurchase(
            final long userId, final LocalDate date, final long timeId,
            final long themeId
    ) {
        log.info("관리자 예약 등록 호출 - userId: {}, date: {}, timeId: {}, themeId: {}", userId, date, timeId, themeId);

        Reserved reserved = registerReserved(userId, date, timeId, themeId);

        log.info("관리자 예약 등록 성공 - 예약ID: {}, userId: {}", reserved.getId(), userId);
        return reserved;
    }

    @Transactional(readOnly = true)
    public List<Reserved> findReservedByFilter(ReservedSearchFilter filter) {
        return reservedRepository.findAll(ReservedSpecifications.byFilter(filter));
    }

    @Transactional(readOnly = true)
    public Reserved findById(final long id) {
        return reservedRepository.findById(id).orElseThrow(() -> new NotFoundException("존재하지 않는 예약입니다."));
    }

    @Transactional
    public void removeById(final long id) {
        log.info("예약 삭제 호출 - reservationId = {}", id);

        Reserved reserved = findById(id);

        eventPublisher.publishEvent(
                new ReservationCancelledEvent(
                        this, reserved.getDate(), reserved.getTimeSlot().getId(),
                        reserved.getTheme().getId()
                ));

        reservedRepository.deleteById(id);

        log.info("예약 삭제 성공 - reservationId = {}", id);
    }

    private Reserved registerReserved(final long userId, final LocalDate date, final long timeId, final long themeId) {
        validateDuplicateReservation(date, timeId, themeId, userId);
        User user = getUserById(userId);
        TimeSlot timeSlot = getTimeSlotById(timeId);
        Theme theme = getThemeById(themeId);

        return reservedRepository.save(Reserved.register(user, date, timeSlot, theme));
    }

    private void validateDuplicateReservation(
            final LocalDate date, final Long timeSlotId, final Long themeId,
            final Long userId
    ) {
        boolean hasDuplicatedReservation = reservationRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(
                date,
                timeSlotId, themeId, userId
        );

        if (hasDuplicatedReservation) {
            throw new AlreadyExistedException("이미 해당 날짜, 시간, 테마에 대한 예약이 존재합니다.");
        }
    }

    private User getUserById(final long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private TimeSlot getTimeSlotById(final long timeId) {
        return timeSlotRepository.findById(timeId).orElseThrow(() -> new NotFoundException("존재하지 않는 타임 슬롯입니다."));
    }

    private Theme getThemeById(final long themeId) {
        return themeRepository.findById(themeId).orElseThrow(() -> new NotFoundException("존재하지 않는 테마입니다."));
    }
}
