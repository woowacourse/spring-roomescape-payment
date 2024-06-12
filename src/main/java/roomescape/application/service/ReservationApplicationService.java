package roomescape.application.service;

import org.springframework.stereotype.Service;

import roomescape.exception.RoomescapeException;
import roomescape.exception.type.RoomescapeExceptionType;
import roomescape.member.domain.LoginMember;
import roomescape.payment.api.PaymentClient;
import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.dto.CancelReason;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationPaymentResponse;
import roomescape.reservation.service.ReservationService;

@Service
public class ReservationApplicationService {
    private final PaymentClient paymentClient;
    private final ReservationService reservationService;
    private final PaymentRepository paymentRepository;

    public ReservationApplicationService(
            PaymentClient paymentClient,
            ReservationService reservationService,
            PaymentRepository paymentRepository
    ) {
        this.paymentClient = paymentClient;
        this.reservationService = reservationService;
        this.paymentRepository = paymentRepository;
    }

    public ReservationPaymentResponse saveReservationPayment(
            LoginMember loginMember,
            ReservationPaymentRequest reservationPaymentRequest
    ) {
        PaymentInfo paymentInfo = paymentClient.payment(reservationPaymentRequest.toPaymentRequest());
        return reservationService.saveReservationPayment(loginMember, reservationPaymentRequest.toReservationRequest(), paymentInfo);
    }

    public void cancelReservationPayment(long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RoomescapeException(RoomescapeExceptionType.NOT_FOUND_RESERVATION_PAYMENT, reservationId));
        paymentClient.cancel(payment.getPaymentKey(), new CancelReason("관리자 권한 취소"));
        reservationService.cancelReservationPayment(reservationId, payment.getId());
    }
}
