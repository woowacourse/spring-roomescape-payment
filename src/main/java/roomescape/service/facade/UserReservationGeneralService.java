package roomescape.service.facade;

import org.springframework.stereotype.Service;

import roomescape.controller.dto.request.CreateReservationRequest;
import roomescape.controller.dto.response.CreateReservationResponse;
import roomescape.controller.dto.request.CreateUserReservationRequest;
import roomescape.domain.reservation.payment.Payment;
import roomescape.service.PaymentService;
import roomescape.service.UserReservationService;
import roomescape.service.dto.PaymentRequest;

@Service
public class UserReservationGeneralService {
    private final UserReservationService userReservationService;
    private final PaymentService paymentService;

    public UserReservationGeneralService(UserReservationService userReservationService, PaymentService paymentService) {
        this.userReservationService = userReservationService;
        this.paymentService = paymentService;
    }

    public CreateReservationResponse reserve(Long memberId, CreateUserReservationRequest request) {
        PaymentRequest paymentRequest = new PaymentRequest(request.orderId(), request.amount(), request.paymentKey());
        Payment payment = paymentService.pay(paymentRequest);
        return userReservationService.reserve(CreateReservationRequest.to(memberId, request), payment.getId());
    }
}
