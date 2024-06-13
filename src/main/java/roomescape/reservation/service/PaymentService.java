package roomescape.reservation.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import roomescape.reservation.model.Payment;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.exception.PaymentException;
import roomescape.reservation.dto.PaymentRequest;
import roomescape.reservation.dto.PaymentResponse;
import roomescape.reservation.encoder.TossSecretKeyEncoder;
import roomescape.reservation.model.Reservation;

@Service
public class PaymentService {

    private final RestClient tossRestClient;
    private final PaymentRepository paymentRepository;

    @Value("${custom.security.toss-payment.secret-key}")
    private String tossSecretKey;

    public PaymentService(RestClient tossRestClient, PaymentRepository paymentRepository) {
        this.tossRestClient = tossRestClient;
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse requestTossPayment(PaymentRequest paymentRequest) {
        String authorization = TossSecretKeyEncoder.encode(tossSecretKey);

        return tossRestClient.post()
                .uri("/confirm")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .body(paymentRequest)
                .retrieve()
                .onStatus(r -> !r.is2xxSuccessful(), (request, response) -> {
                    String errorMessage = parseErrorMessage(response);
                    throw new PaymentException(
                            "결제 오류가 발생했습니다. " + errorMessage, HttpStatus.valueOf(response.getStatusCode().value())
                    );
                })
                .toEntity(PaymentResponse.class)
                .getBody();
    }

    public void savePayment(PaymentResponse paymentResponse, Reservation reservation) {
        paymentRepository.save(Payment.of(paymentResponse.paymentKey(),
                paymentResponse.orderId(),
                paymentResponse.totalAmount(),
                reservation));
    }

    private String parseErrorMessage(ClientHttpResponse response) throws IOException {
        InputStream body = response.getBody();
        Reader inputStreamReader = new InputStreamReader(body, StandardCharsets.UTF_8);
        JsonObject jsonObject = (JsonObject) JsonParser.parseReader(inputStreamReader);
        return jsonObject.get("message").toString().replace("\"", "");
    }

    public Payment getPayment(Reservation reservation) {
        return paymentRepository.findByReservation(reservation);
    }
}
