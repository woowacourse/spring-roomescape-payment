package roomescape.integration.api.page;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.common.RestAssuredTestBase;
import roomescape.reservation.domain.PaymentInfo;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class LoginPageTest extends RestAssuredTestBase {

    @Test
    void 로그인_페이지_조회() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/login")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void temp() {
        RestClient restClient = RestClient.builder().baseUrl("http://jsonplaceholder.typicode.com").build();
        String url = "https://api.tosspayments.com/v1/payments/confirm";

        String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> requestBody = Map.of(
                "paymentKey", "tgen_20250527165422bQX84",
                "amount", 50000,
                "orderId", "MC45MjA0NzkzMzcwMTYy"
        );
        try {
            var response = restClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(PaymentInfo.class);
            System.out.println("결제 확인 응답: " + response);
        } catch (RestClientResponseException e) {
            System.err.println("요청 실패: " + e.getResponseBodyAsString());
        }
    }

    @Test
    void some() {
        String url = "https://api.tosspayments.com/v1/payments";

        RestClient restClient = RestClient.builder().baseUrl("http://jsonplaceholder.typicode.com").build();

        String secretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("orderId", generateOrderId());
        requestBody.put("orderName", "토스 티셔츠 외 2건");
        requestBody.put("amount", 15000);
        requestBody.put("successUrl", "https://yourdomain.com/success");
        requestBody.put("failUrl", "https://yourdomain.com/fail");
        requestBody.put("customerName", "홍길동");
        requestBody.put("customerEmail", "gil@example.com");
        requestBody.put("customerMobilePhone", "01012341234");

        try {
            var response = restClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(PaymentInfo.class);
            System.out.println("결제 확인 응답: " + response);
        } catch (RestClientResponseException e) {
            System.err.println("요청 실패: " + e.getResponseBodyAsString());
        }
    }

    public static String generateOrderId() {
        String prefix = "order_";
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 12); // 12자리 추출
        String timestamp = String.valueOf(System.currentTimeMillis()); // 현재 시간 (밀리초)
        return prefix + uuid + "_" + timestamp;
    }
}
