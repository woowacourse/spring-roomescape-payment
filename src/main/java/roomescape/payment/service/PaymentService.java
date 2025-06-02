package roomescape.payment.service;

import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.ReservationPaymentRequest;

public interface PaymentService {

    void saveReservationPayment(ReservationPaymentRequest request, LoginMember loginMember);

    void confirmPayment(ReservationPaymentRequest request);
}
