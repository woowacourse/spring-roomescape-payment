package roomescape.reservation.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.application.MemberDataService;
import roomescape.member.domain.Member;
import roomescape.payment.application.PaymentApplicationService;
import roomescape.payment.domain.Payment;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.reservation.application.dto.request.ConfirmedReservationByCriteriaWebRequest;
import roomescape.reservation.application.dto.request.ConfirmedReservationCreateRequest;
import roomescape.reservation.application.dto.request.ReservationCreateWebRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.presentation.dto.response.ConfirmedReservationWebResponse;
import roomescape.reservationslot.application.ReservationSlotDataService;
import roomescape.reservationslot.domain.ReservationSlot;
import roomescape.reservationslot.presentation.dto.response.MyReservationResponse;
import roomescape.reservationtime.application.ReservationTimeDataService;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.application.ThemeDataService;
import roomescape.theme.domain.Theme;

@Service
public class ConfirmedReservationApplicationService {

    private final ReservationSlotDataService reservationSlotDataService;
    private final ReservationTimeDataService reservationTimeDataService;
    private final ThemeDataService themeDataService;
    private final MemberDataService memberDataService;
    private final ReservationDataService reservationDataService;
    private final PaymentApplicationService paymentApplicationService;

    public ConfirmedReservationApplicationService(final ReservationSlotDataService reservationSlotDataService,
                                                  final ReservationTimeDataService reservationTimeDataService,
                                                  final ThemeDataService themeDataService,
                                                  final MemberDataService memberDataService,
                                                  final ReservationDataService slotReservationDataService,
                                                  PaymentApplicationService paymentApplicationService) {
        this.reservationSlotDataService = reservationSlotDataService;
        this.reservationTimeDataService = reservationTimeDataService;
        this.themeDataService = themeDataService;
        this.memberDataService = memberDataService;
        this.reservationDataService = slotReservationDataService;
        this.paymentApplicationService = paymentApplicationService;
    }

    public ConfirmedReservationWebResponse create(final ConfirmedReservationCreateRequest request) {
        reservationSlotDataService.validateReservationSlotNotExists(request.date(), request.timeId(),
                request.themeId());

        ReservationSlot slot = createReservationSlot(
                new ReservationCreateWebRequest(request.date(), request.timeId(), request.themeId()));
        Member member = memberDataService.getById(request.memberId());
        slot.addReservation(member, request.now());
        ReservationSlot savedSlot = reservationSlotDataService.save(slot);

        return ConfirmedReservationWebResponse.of(savedSlot);
    }

    @Transactional
    public ConfirmedReservationWebResponse createWithPayment(final ConfirmedReservationCreateRequest confirmedReservationCreateRequest, final PaymentApproveRequest paymentApproveRequest) {
        ConfirmedReservationWebResponse confirmedReservationWebResponse = create(confirmedReservationCreateRequest);
        paymentApplicationService.approveReservationPayment(paymentApproveRequest, confirmedReservationWebResponse.id());
        return confirmedReservationWebResponse;
    }

    public List<ConfirmedReservationWebResponse> findByCriteria(
            final ConfirmedReservationByCriteriaWebRequest request) {
        List<Reservation> reservations = reservationDataService.findConfirmedByCriteria(request.themeId(),
                request.memberId(), request.startDate(), request.endDate());
        return reservations
                .stream()
                .map(Reservation::getReservationSlot)
                .map(ConfirmedReservationWebResponse::of)
                .toList();
    }

    public List<MyReservationResponse> findMyReservations(final Long memberId) {
        memberDataService.validateExists(memberId);
        List<Reservation> reservations = reservationDataService.findMemberReservations(memberId);
        return reservations.stream()
                .map(reservation -> {
                    ReservationSlot reservationSlot = reservation.getReservationSlot();
                    Payment payment = paymentApplicationService.findPaymentOfReservation(reservation.getId());
                    return generateMyReservationResponses(reservation, reservationSlot, payment);
                })
                .toList();
    }

    private MyReservationResponse generateMyReservationResponses(Reservation reservation, ReservationSlot reservationSlot, Payment payment) {
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            return new MyReservationResponse(reservationSlot.getId(), reservationSlot.getTheme().getName(),
                    reservationSlot.getDate().toString(), reservationSlot.getTime().getStartAt().toString(), reservation.getStatus(),
                    payment.getPaymentKey(), payment.getAmount(), reservationSlot.findRank(reservation));
        }
        return new MyReservationResponse(reservationSlot.getId(), reservationSlot.getTheme().getName(),
                reservationSlot.getDate().toString(), reservationSlot.getTime().getStartAt().toString(), reservation.getStatus(),
                null, null, reservationSlot.findRank(reservation));
    }

    public void cancel(final Long reservationId) {
        Reservation reservation = reservationDataService.getById(reservationId);
        reservationDataService.deleteById(reservationId);
        ReservationSlot reservationSlot = reservation.getReservationSlot();
        cleanupEmptyReservationSlot(reservationSlot.getId());
        reservationSlot.getReservations().remove(reservation);
        updateFirstWaitingToPaymentPending(reservationSlot);
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

    private void updateFirstWaitingToPaymentPending(ReservationSlot reservationSlot) {
        Optional<Reservation> firstWaiting = reservationSlot.getReservations().stream().findFirst();
        firstWaiting.ifPresent(Reservation::toPaymentPending);
    }
}
