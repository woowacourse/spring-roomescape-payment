package roomescape.reservation.application;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.application.MemberDataService;
import roomescape.member.domain.Member;
import roomescape.reservation.application.dto.request.ConfirmedReservationByCriteriaWebRequest;
import roomescape.reservation.application.dto.request.ConfirmedReservationCreateRequest;
import roomescape.reservation.application.dto.request.ReservationCreateWebRequest;
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
        reservationSlotDataService.validateReservationSlotNotExists(request.reservationDate(), request.timeId(),
                request.themeId());

        ReservationSlot slot = createReservationSlot(
                new ReservationCreateWebRequest(request.reservationDate(), request.timeId(), request.themeId()));
        Member member = memberDataService.getById(request.memberId());
        slot.addConfirmedReservation(member, request.reservationDateTime(), request.orderId());
        ReservationSlot savedSlot = reservationSlotDataService.save(slot);

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
        Reservation reservation = reservationDataService.getById(reservationId);
        ReservationSlot reservationSlot = reservation.getReservationSlot();
        cleanupEmptyReservationSlot(reservationSlot.getId());
        reservationDataService.deleteById(reservationId);
        List<Reservation> reservations = reservationSlot.getReservations();
        reservations.remove(reservation);

        if (!reservations.isEmpty()) {
            Reservation highestPriorityReservation = reservationSlot.findHighestPriorityReservation();
            eventPublisher.publishEvent(new ReservationPromoteEvent(highestPriorityReservation.getId()));
        }
    }

    private ReservationSlot createReservationSlot(final ReservationCreateWebRequest reservationCreateWebRequest) {
        ReservationTime time = reservationTimeDataService.getById(reservationCreateWebRequest.timeId());
        Theme theme = themeDataService.getById(reservationCreateWebRequest.themeId());
        return new ReservationSlot(reservationCreateWebRequest.date(), time, theme);
    }

    private void cleanupEmptyReservationSlot(final Long slotId) {
        if (reservationSlotDataService.hasSingleReservation(slotId)) {
            reservationSlotDataService.deleteById(slotId);
        }
    }
}
