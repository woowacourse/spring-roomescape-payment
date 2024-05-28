package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentRequest;

public class PaymentClient {
    private final RestClient restClient;

    public PaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void addPayment(PaymentRequest paymentRequest) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("orderId", paymentRequest.orderId());
        obj.put("amount", paymentRequest.amount());
        obj.put("paymentKey", paymentRequest.paymentKey());

        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizations)
                .body(obj).toString();
    }
}
