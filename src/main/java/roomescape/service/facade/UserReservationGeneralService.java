package roomescape.service.facade;

import org.springframework.stereotype.Service;

import roomescape.controller.dto.CreateReservationRequest;
import roomescape.controller.dto.CreateReservationResponse;
import roomescape.controller.dto.CreateUserReservationRequest;
import roomescape.service.PaymentService;
import roomescape.service.UserReservationService;

@Service
public class UserReservationGeneralService {

    private final UserReservationService userReservationService;
    private final PaymentService paymentService;

    public UserReservationGeneralService(UserReservationService userReservationService, PaymentService paymentService) {
        this.userReservationService = userReservationService;
        this.paymentService = paymentService;
    }

    public CreateReservationResponse reserve(Long memberId, CreateUserReservationRequest request) {
        paymentService.pay(request);
        return userReservationService.reserve(CreateReservationRequest.to(memberId, request));
    }
}
