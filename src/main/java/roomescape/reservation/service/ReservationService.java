package roomescape.reservation.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.InvalidArgumentException;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.service.TossPaymentService;
import roomescape.reservation.controller.request.ReservePaymentRequest;
import roomescape.reservation.controller.response.MyReservationResponse;
import roomescape.reservation.controller.response.ReservationResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.exception.InAlreadyReservationException;
import roomescape.reservation.service.command.ReserveCommand;
import roomescape.reservation.service.manager.ReservationManager;
import roomescape.waiting.exception.InAlreadyWaitingException;
import roomescape.waiting.service.WaitingQueryService;
import roomescape.waiting.service.WaitingService;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final WaitingService waitingService;
    private final ReservationManager reservationManager;
    private final WaitingQueryService waitingQueryService;
    private final ReservedQueryService reservedQueryService;
    private final TossPaymentService tossPaymentService;

    @Transactional
    public ReservationResponse reserve(ReserveCommand reserveCommand) {
        Reservation reserved = reservationManager.reserved(reserveCommand);

        return ReservationResponse.from(reserved);
    }

    @Transactional
    public ReservationResponse waiting(ReserveCommand reserveCommand) {
        validateAvailableWaiting(reserveCommand);

        Reservation waiting = reservationManager.waiting(reserveCommand);

        return ReservationResponse.from(waiting);
    }

    @Transactional
    public ReservationResponse reserve(ReservePaymentRequest request, Long memberId) {
        ReserveCommand reserveCommand = ReserveCommand.byPayment(request, memberId);
        Reservation reserved = reservationManager.reserved(reserveCommand);

        PaymentRequest paymentRequest = new PaymentRequest(request.paymentKey(), request.orderId(), request.amount());
        PaymentResponse paymentResponse = tossPaymentService.confirmPayment(paymentRequest);

        return ReservationResponse.from(reserved);
    }

    private void validateAvailableWaiting(ReserveCommand reserveCommand) {
        if (reservedQueryService.notExistsReserved(reserveCommand.date(), reserveCommand.timeId())) {
            throw new InvalidArgumentException("예약 대기를 할 수 없습니다!");
        }

        if (reservedQueryService.existsReserved(reserveCommand.memberId(), reserveCommand.date(),
                reserveCommand.timeId())) {
            throw new InAlreadyReservationException("이미 예약한 사람입니다.");
        }

        if (waitingQueryService.existWaiting(reserveCommand.memberId(), reserveCommand.date(),
                reserveCommand.timeId())) {
            throw new InAlreadyWaitingException("이미 예약 대기가 존재하는 시간입니다.");
        }
    }

    @Transactional
    public void delete(Long id) {
        Reservation reservation = reservedQueryService.getReserved(id);
        waitingService.promoteFirstWaitingToReservation(reservation.getDate(), reservation.getTimeId());
        reservationManager.delete(reservation);
    }

    @Transactional(readOnly = true)
    public List<MyReservationResponse> getAllReservations(Long memberId) {
        List<MyReservationResponse> responses = new ArrayList<>();

        responses.addAll(reservedQueryService.getReservations(memberId));
        responses.addAll(waitingQueryService.getMyWaitings(memberId));

        return responses;
    }
}
