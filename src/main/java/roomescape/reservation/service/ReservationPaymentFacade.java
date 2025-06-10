package roomescape.reservation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import roomescape.client.TossPaymentClient;
import roomescape.client.dto.request.TossPaymentConfirmRequest;
import roomescape.client.dto.response.TossPaymentResponse;
import roomescape.common.exception.InternalServerException;
import roomescape.member.dto.request.LoginMember;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.ReservationWithPaymentResponse;

@Service
public class ReservationPaymentFacade {

    private static final Logger log = LoggerFactory.getLogger(ReservationPaymentFacade.class);
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final TossPaymentClient tossPaymentClient;

    public ReservationPaymentFacade(ReservationService reservationService, PaymentService paymentService, TossPaymentClient tossPaymentClient) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.tossPaymentClient = tossPaymentClient;
    }

    public ReservationWithPaymentResponse createReservationAndSavePayment(
            ReservationWithPaymentRequest request,
            LoginMember loginMember
    ) {
        TossPaymentConfirmRequest confirmRequest = new TossPaymentConfirmRequest(
                request.orderId(),
                request.amount(),
                request.paymentKey()
        );
        ReservationWithPaymentResponse reservationWithPendingPayment = reservationService.createReservationWithPendingPayment(request, loginMember.id());

        ResponseEntity<TossPaymentResponse> response = tossPaymentClient.confirmPayment(confirmRequest);
        if (response.getStatusCode().is2xxSuccessful() || isPaymentConfirmValid(request, response.getBody())) {
            paymentService.confirm(reservationWithPendingPayment.paymentId());
            log.info("예약 생성 완료 - reservationId: {}", reservationWithPendingPayment.id());
            return reservationWithPendingPayment;
        }

        log.warn("결제 확인 실패 - 예약 삭제 및 결제 취소 처리 시작 - reservationId: {}, paymentId: {}", reservationWithPendingPayment.id(), reservationWithPendingPayment.paymentId());

        reservationService.deleteReservationById(reservationWithPendingPayment.id());
        log.info("예약 삭제 완료 - reservationId: {}", reservationWithPendingPayment.id());

        paymentService.cancel(reservationWithPendingPayment.paymentId());
        log.info("결제 취소 완료 - paymentId: {}", reservationWithPendingPayment.paymentId());

        tossPaymentClient.handleTosPaymentException(response);
        throw new InternalServerException();
    }

    private boolean isPaymentConfirmValid(ReservationWithPaymentRequest request, TossPaymentResponse response) {
        if (request == null || response == null) {
            return false;
        }
        boolean paymentKeyMatch = request.paymentKey().equals(response.paymentKey());
        boolean orderIdMatch = request.orderId().equals(response.orderId());
        boolean amountMatch = request.amount() == response.totalAmount();

        return paymentKeyMatch && orderIdMatch && amountMatch;
    }
}
