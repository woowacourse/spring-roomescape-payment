package roomescape.business.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.LoginInfo;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.vo.Id;
import roomescape.exception.payment.PaymentNotFoundException;
import roomescape.infrastructure.PaymentRepository;
import roomescape.infrastructure.payment.PaymentClient;
import roomescape.infrastructure.payment.toss.dto.TossPaymentApproveRequest;
import roomescape.presentation.api.PaymentApproveRequest;
import roomescape.presentation.dto.request.PaymentAndReservationRequest;
import roomescape.presentation.dto.request.ReservationRequest;
import roomescape.presentation.dto.response.PaymentResponse;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final ReservationService reservationService;
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(ReservationService reservationService, PaymentRepository paymentRepository,
                          PaymentClient paymentClient) {
        this.reservationService = reservationService;
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public PaymentResponse createPaymentAndReservation(LoginInfo loginInfo, PaymentAndReservationRequest request) {
        ReservationRequest reservationRequest = new ReservationRequest(request.themeId(), request.timeId(),
                request.date());
        Reservation reservation = reservationService.createReservation(loginInfo, reservationRequest);
        Payment payment = paymentRepository.save(Payment.create(reservation));
        return PaymentResponse.from(payment);
    }

    @Transactional
    public void approvePayment(String paymentId, PaymentApproveRequest request) {
        Payment payment = paymentRepository.findById(Id.create(paymentId))
                .orElseThrow(PaymentNotFoundException::new);
        payment.approve(request.paymentKey(), request.amount());
        paymentClient.approvePayment(
                new TossPaymentApproveRequest(request.paymentKey(), paymentId, request.amount()));
    }
}
