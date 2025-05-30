package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.application.dto.PaymentConfirmRequest;
import roomescape.payment.application.dto.PaymentDataRequest;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;

@Service
public interface PaymentService {

    Payment pay(
            PaymentDataRequest paymentDataRequest,
            PaymentConfirmRequest request,
            Reservation reservation
    );

    Payment await(PaymentDataRequest request, Reservation reservation);

}
