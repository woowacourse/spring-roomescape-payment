package roomescape.service;

import java.net.SocketTimeoutException;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.dto.reservation.ReservationCreateRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.exception.TossPaymentException;
import roomescape.util.AuthorizationHeaderProvider;

@Service
public class ReservationPaymentService {
    private final String secretKey;
    private final ReservationService reservationService;
    private final PaymentClientService paymentClientService;
    private final PaymentService paymentService;
    private final AuthorizationHeaderProvider authorizationHeaderProvider;

    public ReservationPaymentService(@Value("${toss.secret-key}") String secretKey,
                                     ReservationService reservationService,
                                     PaymentClientService paymentClientService,
                                     PaymentService paymentService,
                                     AuthorizationHeaderProvider authorizationHeaderProvider) {
        this.secretKey = secretKey;
        this.reservationService = reservationService;
        this.paymentClientService = paymentClientService;
        this.paymentService = paymentService;
        this.authorizationHeaderProvider = authorizationHeaderProvider;
    }

    @Transactional
    public ReservationResponse confirmPaymentAndAddReservation(ReservationCreateRequest reservationRequest,
                                                               PaymentConfirmRequest paymentRequest) {
        ReservationResponse reservation = reservationService.createReservation(reservationRequest);
        String providedAuthorization = authorizationHeaderProvider.provide(secretKey);
        try {
            PaymentConfirmResponse paymentConfirm = paymentClientService.confirm(providedAuthorization, paymentRequest);
            paymentService.createPayment(paymentConfirm, reservation.id());
            return reservation;
        } catch (Exception ex) {
            if (TossPaymentException.isTimeoutException(ex)) {
                throw new TossPaymentException("결제 승인 요청이 타임아웃되었습니다.");
            }
            throw new TossPaymentException("결제 승인 중 예외가 발생했습니다.");
        }
    }
}