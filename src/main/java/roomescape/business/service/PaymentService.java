package roomescape.business.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.LoginInfo;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.vo.Id;
import roomescape.exception.payment.PaymentNotFoundException;
import roomescape.infrastructure.PaymentRepository;
import roomescape.presentation.dto.request.PaymentAndReservationRequest;
import roomescape.presentation.dto.request.ReservationRequest;
import roomescape.presentation.dto.response.PaymentResponse;

@Service
//@Transactional(readOnly = true)
public class PaymentService {

    private final ReservationService reservationService;
    private final PaymentRepository paymentRepository;

    public PaymentService(ReservationService reservationService, PaymentRepository paymentRepository) {
        this.reservationService = reservationService;
        this.paymentRepository = paymentRepository;
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
    public void completePayment(String paymentId, String paymentKey, Long amount) {
        Payment payment = findPaymentById(paymentId);
        payment.approve(paymentKey, amount);
    }

    @Transactional
    public void deletePaymentById(String paymentId) {
        Payment payment = findPaymentById(paymentId);
        paymentRepository.delete(payment);
    }

    private Payment findPaymentById(String paymentId) {
        return paymentRepository.findById(Id.create(paymentId))
                .orElseThrow(PaymentNotFoundException::new);
    }
}
