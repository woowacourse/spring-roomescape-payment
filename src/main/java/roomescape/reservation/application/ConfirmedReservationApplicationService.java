package roomescape.reservation.application;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.application.MemberDataService;
import roomescape.member.domain.Member;
import roomescape.reservation.application.dto.request.ConfirmedReservationByCriteriaWebRequest;
import roomescape.reservation.application.dto.request.ConfirmedReservationCreateRequest;
import roomescape.reservation.application.event.ReservationPromoteEvent;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.presentation.dto.response.ConfirmedReservationWebResponse;
import roomescape.reservationslot.application.ReservationSlotDataService;
import roomescape.reservationslot.domain.ReservationSlot;
import roomescape.reservationslot.presentation.dto.response.MyReservationResponse;
import roomescape.reservationtime.application.ReservationTimeDataService;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.application.ThemeDataService;
import roomescape.theme.domain.Theme;

@Service
@Transactional
@Slf4j
public class ConfirmedReservationApplicationService {

    private final ReservationSlotDataService reservationSlotDataService;
    private final ReservationTimeDataService reservationTimeDataService;
    private final ThemeDataService themeDataService;
    private final MemberDataService memberDataService;
    private final ReservationDataService reservationDataService;
    private final ApplicationEventPublisher eventPublisher;

    public ConfirmedReservationApplicationService(final ReservationSlotDataService reservationSlotDataService,
                                                  final ReservationTimeDataService reservationTimeDataService,
                                                  final ThemeDataService themeDataService,
                                                  final MemberDataService memberDataService,
                                                  final ReservationDataService reservationDataService,
                                                  final ApplicationEventPublisher eventPublisher) {
        this.reservationSlotDataService = reservationSlotDataService;
        this.reservationTimeDataService = reservationTimeDataService;
        this.themeDataService = themeDataService;
        this.memberDataService = memberDataService;
        this.reservationDataService = reservationDataService;
        this.eventPublisher = eventPublisher;
    }

    public ConfirmedReservationWebResponse create(final ConfirmedReservationCreateRequest request) {
        log.info("예약 생성 시도: memberId={}, themeId={}, date={}, timeId={}",
                request.memberId(), request.themeId(), request.reservationDate(), request.timeId());

        ReservationSlot slot = getOrCreateReservationSlot(
                request.reservationDate(), request.timeId(), request.themeId());
        Member member = memberDataService.getById(request.memberId());
        slot.addReservation(member, request.reservationDateTime());
        ReservationSlot savedSlot = reservationSlotDataService.save(slot);

        log.info("예약 생성 완료: reservationId={}, memberId={}, themeId={}, date={}",
                savedSlot.findHighestPriorityReservation().getId(), request.memberId(), request.themeId(), request.reservationDate());

        return ConfirmedReservationWebResponse.of(savedSlot);
    }

    public List<ConfirmedReservationWebResponse> findByCriteria(
            final ConfirmedReservationByCriteriaWebRequest request) {
        List<Reservation> reservations = reservationDataService.findFirstByCriteria(request.themeId(),
                request.memberId(), request.startDate(), request.endDate());
        return reservations
                .stream()
                .map(Reservation::getReservationSlot)
                .map(ConfirmedReservationWebResponse::of)
                .toList();
    }

    public List<MyReservationResponse> findReservationsByMemberId(final Long memberId) {
        memberDataService.validateExists(memberId);
        return reservationDataService.findReservationsByMemberId(memberId);
    }

    public void cancel(final Long reservationId) {
        log.info("예약 취소 시도: reservationId={}", reservationId);

        Reservation reservation = reservationDataService.getById(reservationId);
        ReservationSlot reservationSlot = reservation.getReservationSlot();
        cleanupEmptyReservationSlot(reservationSlot.getId());
        reservationDataService.deleteById(reservationId);
        List<Reservation> reservations = reservationSlot.getReservations();
        reservations.remove(reservation);

        if (!reservations.isEmpty()) {
            Reservation highestPriorityReservation = reservationSlot.findHighestPriorityReservation();
            eventPublisher.publishEvent(new ReservationPromoteEvent(highestPriorityReservation.getId()));

            log.info("예약 취소 및 대기자 승격: cancelledReservationId={}, promotedReservationId={}",
                    reservationId, highestPriorityReservation.getId());
            return;
        }
        log.info("예약 취소 완료: reservationId={}", reservationId);
    }

    private ReservationSlot getOrCreateReservationSlot(final LocalDate date, final Long timeId, final Long themeId) {
        if (reservationSlotDataService.existsByDateAndTimeIdAndThemeId(date, timeId, themeId)) {
            return reservationSlotDataService.getReservationSlotByDateAndTimeAndTheme(date, timeId, themeId);
        }
        ReservationTime time = reservationTimeDataService.getById(timeId);
        Theme theme = themeDataService.getById(themeId);
        return new ReservationSlot(date, time, theme);
    }

    private void cleanupEmptyReservationSlot(final Long slotId) {
        if (reservationSlotDataService.hasSingleReservation(slotId)) {
            reservationSlotDataService.deleteById(slotId);
        }
    }
}
