package roomescape.util;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.global.exception.RoomescapeException;

@Component
public class PaymentManager {

    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String TOSS_PAYMENTS_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String ORDER_ID = "orderId";
    private static final String AMOUNT = "amount";
    private static final String PAYMENT_KEY = "paymentKey";

    @Value("${toss-payment.test-secret-key}")
    private String secretKey;

    private final HttpURLConnection connection;
    private final JSONParser jsonParser;

    public PaymentManager() {
        this.connection = initializeConnection();
        this.jsonParser = new JSONParser();
    }

    public HttpURLConnection initializeConnection() {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder.encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));
            String authorizations = AUTHORIZATION_PREFIX + new String(encodedBytes);

            URL url = new URL(TOSS_PAYMENTS_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", authorizations);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            return connection;
        } catch (IOException e) {
            throw new RoomescapeException("결제 요청에 실패하였습니다.");
        }
    }

    public void requestPaymentApproval(String orderId, long amount, String paymentKey) {
        JSONObject obj = new JSONObject(Map.of(
            ORDER_ID, orderId,
            AMOUNT, amount,
            PAYMENT_KEY, paymentKey
        ));

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RoomescapeException("결제 요청에 실패하였습니다.");
        }
    }

    public String getErrorMessage() {
        InputStream responseStream = connection.getErrorStream();
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            return (String) jsonObject.get("message");
        } catch (IOException | ParseException e) {
            throw new RoomescapeException("결제 요청에 실패하였습니다.");
        }
    }

    public boolean isPaymentRequestFailed() {
        try {
            return connection.getResponseCode() != HTTP_OK;
        } catch (IOException e) {
            throw new RoomescapeException("결제 요청에 실패하였습니다.");
        }
    }
}
