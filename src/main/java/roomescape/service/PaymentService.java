package roomescape.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.controller.request.PaymentCancelRequest;
import roomescape.controller.request.PaymentRequest;
import roomescape.controller.request.ReservationRequest;
import roomescape.controller.request.ReservationWithPaymentRequest;
import roomescape.exception.InvalidPaymentInformationException;
import roomescape.exception.PaymentException;
import roomescape.exception.PaymentServerErrorException;
import roomescape.model.PaymentInfo;
import roomescape.model.Reservation;
import roomescape.repository.PaymentInfoRepository;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Transactional
@Service
public class PaymentService {

    private final static long RESERVATION_PRICE = 1999999;
    private final RestClient restClient;
    private final PaymentInfoRepository paymentInfoRepository;
    @Value("${payment.toss.payment-confirm-url}")
    private String confirmUrl;
    @Value("${payment.toss.cancel}")
    private String cancelUrl;

    public PaymentService(RestClient restClient, PaymentInfoRepository paymentInfoRepository) {
        this.restClient = restClient;
        this.paymentInfoRepository = paymentInfoRepository;
    }

    public void confirmReservationPayments(ReservationRequest request) {
        validatePayments(request.amount());
        RestClient.ResponseSpec paymentRequest = restClient.post()
                .uri(confirmUrl)
                .contentType(APPLICATION_JSON)
                .body(new PaymentRequest(request.paymentKey(), request.paymentId(), request.amount()))
                .retrieve();
        handleServerError(handleClientError(paymentRequest)).toBodilessEntity();
    }

    public void confirmReservationPayments(ReservationWithPaymentRequest request) {
        validatePayments(request.amount());
        RestClient.ResponseSpec paymentRequest = restClient.post()
                .uri(confirmUrl)
                .contentType(APPLICATION_JSON)
                .body(new PaymentRequest(request.paymentKey(), request.paymentId(), request.amount()))
                .retrieve();
        handleServerError(handleClientError(paymentRequest)).toBodilessEntity();
    }

    private void validatePayments(long amount) {
        if (RESERVATION_PRICE != amount) {
            throw new PaymentException("클라이언트의 지불 정보가 일치하지 않습니다. 금액 정보 : [%d]".formatted(amount));
        }
    }

    public PaymentInfo addPayment(ReservationRequest request, Reservation reservation) {
        PaymentInfo paymentInfo = new PaymentInfo(request.paymentKey(), request.paymentId(), request.amount(), reservation);
        return paymentInfoRepository.save(paymentInfo);
    }

    public PaymentInfo addPayment(ReservationWithPaymentRequest request, Reservation reservation) {
        PaymentInfo paymentInfo = new PaymentInfo(request.paymentKey(), request.paymentId(), request.amount(), reservation);
        return paymentInfoRepository.save(paymentInfo);
    }

    public void cancelPayment(Long reservationId) {
        PaymentInfo paymentInfo = paymentInfoRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new PaymentException("id:[%s] 인 예약의 결제 정보가 존재하지 않습니다.".formatted(reservationId)));
        String paymentKey = paymentInfo.getPaymentKey();
        RestClient.ResponseSpec paymentRequest = restClient.post()
                .uri("/" + paymentKey + cancelUrl)
                .contentType(APPLICATION_JSON)
                .body(new PaymentCancelRequest("고객 요청"))
                .retrieve();
        handleServerError(handleClientError(paymentRequest)).toBodilessEntity();
        paymentInfoRepository.deleteById(paymentInfo.getId());
    }

    private RestClient.ResponseSpec handleClientError(RestClient.ResponseSpec responseSpec) {
        return responseSpec.onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
            throw new InvalidPaymentInformationException();
        });
    }

    private RestClient.ResponseSpec handleServerError(RestClient.ResponseSpec responseSpec) {
        return responseSpec.onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
            throw new PaymentServerErrorException();
        });
    }
}
