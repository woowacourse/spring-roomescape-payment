package roomescape.reservation.application;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.application.MemberDataService;
import roomescape.member.domain.Member;
import roomescape.payment.application.PaymentApplicationService;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.reservation.application.dto.request.WaitingConfirmRequest;
import roomescape.reservation.application.dto.request.WaitingReservationCreateRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.exception.ConfirmedReservationAlreadyExistsException;
import roomescape.reservation.presentation.dto.response.WaitingWebResponse;
import roomescape.reservationslot.application.ReservationSlotDataService;
import roomescape.reservationslot.domain.ReservationSlot;
import roomescape.reservationslot.presentation.dto.response.ReservationResponse;

@Service
public class WaitingReservationApplicationService {

    private final ReservationSlotDataService reservationSlotDataService;
    private final MemberDataService memberDataService;
    private final ReservationDataService reservationDataService;
    private final PaymentApplicationService paymentApplicationService;

    public WaitingReservationApplicationService(final ReservationSlotDataService reservationSlotDataService,
                                                final MemberDataService memberDataService,
                                                final ReservationDataService reservationDataService,
                                                PaymentApplicationService paymentApplicationService) {
        this.reservationSlotDataService = reservationSlotDataService;
        this.memberDataService = memberDataService;
        this.reservationDataService = reservationDataService;
        this.paymentApplicationService = paymentApplicationService;
    }

    public ReservationResponse create(final WaitingReservationCreateRequest createRequest) {
        ReservationSlot slot = reservationSlotDataService.getReservationSlotByDateAndTimeAndTheme(createRequest.date(), createRequest.timeId(), createRequest.themeId());
        Member member = memberDataService.getById(createRequest.memberId());
        Reservation reservation = slot.addReservation(member, LocalDateTime.now());
        reservationDataService.save(reservation);

        return ReservationResponse.from(reservation);
    }

    public List<WaitingWebResponse> findAll() {
        List<Reservation> reservations = reservationDataService.findAllWaitingReservations();
        return reservations.stream()
                .map(WaitingWebResponse::from)
                .toList();
    }

    public void cancelByReservationSlotIdAndMemberId(final Long reservationSlotId, final Long memberId) {
        Reservation reservation = reservationDataService.getByReservationSlotIdAndMemberId(reservationSlotId, memberId);
        reservationDataService.deleteByReservationSlotIdAndMemberId(reservationSlotId, memberId);
        ReservationSlot reservationSlot = reservationSlotDataService.getById(reservationSlotId);
        reservationSlot.getReservations().remove(reservation);
    }

    public void cancel(final Long reservationId) {
        Reservation reservation = reservationDataService.getById(reservationId);
        reservationDataService.cancel(reservation);
        ReservationSlot reservationSlot = reservationSlotDataService.getById(reservation.getReservationSlot().getId());
        reservationSlot.getReservations().remove(reservation);
    }

    @Transactional
    public void confirm(WaitingConfirmRequest waitingConfirmRequest, final PaymentApproveRequest paymentApproveRequest) {
        ReservationSlot reservationSlot = reservationSlotDataService.getById(waitingConfirmRequest.reservationSlotId());
        validateConfirmedReservationNotExists(reservationSlot);
        Reservation paymentPendingReservation = reservationSlot.findPaymentPendingReservation();
        paymentPendingReservation.toConfirmed();
        paymentApplicationService.approveReservationPayment(paymentApproveRequest, paymentPendingReservation.getId());
    }

    private void validateConfirmedReservationNotExists(ReservationSlot reservationSlot) {
        if (reservationSlot.isConfirmedReservationExist()) {
            throw new ConfirmedReservationAlreadyExistsException("이미 예약이 존재하여 진행할 수 없습니다.");
        }
    }
}
