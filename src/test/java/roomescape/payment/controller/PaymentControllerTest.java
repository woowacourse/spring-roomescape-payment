package roomescape.payment.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentTimeoutException;
import roomescape.payment.infrastructure.TossRestClient;

@Sql("/data.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "rest-client.toss-payment.base-url=http://localhost:8089")
class PaymentControllerTest extends IntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TossRestClient tossRestClient;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void 결제_승인_정상_응답_확인() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createNormalBody())
                ));

        String paymentKey = "test_key";
        String orderId = "a4CWyWY5m89PNh7xJwhk1";
        Long amount = 1000L;
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        // when
        TossPaymentResponse response = tossRestClient.confirm(request);

        // then
        SoftAssertions.assertSoftly(soft -> {
            assertThat(response.paymentKey()).isEqualTo("test_key");
            assertThat(response.status()).isEqualTo("DONE");
        });
    }

    @Test
    void 결제_승인_잘못된_key_예외_처리() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createErrorBody())
                ));

        String paymentKey = "wrong_test_key";
        String orderId = "a4CWyWY5m89PNh7xJwhk1";
        Long amount = 1000L;
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        // when
        // then
        assertThatThrownBy(() -> tossRestClient.confirm(request))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("잘못된 시크릿키 연동 정보 입니다.");
    }

    @Test
    void 타임아웃_시간내_응답_시_정상_응답_확인() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createNormalBody())
                        .withFixedDelay(4_000)
                ));


        String paymentKey = "test_key";
        String orderId = "a4CWyWY5m89PNh7xJwhk1";
        Long amount = 1000L;
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        // when
        TossPaymentResponse response = tossRestClient.confirm(request);

        // then
        SoftAssertions.assertSoftly(soft -> {
            assertThat(response.paymentKey()).isEqualTo("test_key");
            assertThat(response.status()).isEqualTo("DONE");
        });
    }

    @Test
    void 타임아웃_시간외_응답_시_예외_처리() {
        // given
        wireMockServer.stubFor(post(urlEqualTo("/v1/payments/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(createNormalBody())
                        .withFixedDelay(5_000)
                ));


        String paymentKey = "test_key";
        String orderId = "a4CWyWY5m89PNh7xJwhk1";
        Long amount = 1000L;
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);

        // when
        // then
        assertThatThrownBy(() -> tossRestClient.confirm(request))
                .isInstanceOf(PaymentTimeoutException.class);
    }

    @Test
    void payment_저장_및_reservation_저장() {
        // given - data.sql
        LocalDate date = LocalDate.of(2999,5,5);
        Long themeId = 1L;
        Long timeId = 1L;
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 1000L;
        String paymentType = "paymentType";
        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(date, themeId, timeId,
                paymentKey, orderId, amount, paymentType);
        Claims claims = Jwts.claims()
                .subject("1")
                .build();
        String token = jwtTokenProvider.createToken(claims);

        // when
        // then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(reservationPaymentRequest)
                .when().post("/payments")
                .then().log().all()
                .statusCode(201);
    }

    private String createNormalBody() {
        return """
                                {
                                  "mId": "tosspayments",
                                  "lastTransactionKey": "9C62B18EEF0DE3EB7F4422EB6D14BC6E",
                                  "paymentKey": "test_key",
                                  "orderId": "a4CWyWY5m89PNh7xJwhk1",
                                  "orderName": "토스 티셔츠 외 2건",
                                  "taxExemptionAmount": 0,
                                  "status": "DONE",
                                  "requestedAt": "2024-02-13T12:17:57+09:00",
                                  "approvedAt": "2024-02-13T12:18:14+09:00",
                                  "useEscrow": false,
                                  "cultureExpense": false,
                                  "card": {
                                    "issuerCode": "71",
                                    "acquirerCode": "71",
                                    "number": "12345678****000*",
                                    "installmentPlanMonths": 0,
                                    "isInterestFree": false,
                                    "interestPayer": null,
                                    "approveNo": "00000000",
                                    "useCardPoint": false,
                                    "cardType": "신용",
                                    "ownerType": "개인",
                                    "acquireStatus": "READY",
                                    "amount": 1000
                                  },
                                  "virtualAccount": null,
                                  "transfer": null,
                                  "mobilePhone": null,
                                  "giftCertificate": null,
                                  "cashReceipt": null,
                                  "cashReceipts": null,
                                  "discount": null,
                                  "cancels": null,
                                  "secret": null,
                                  "type": "NORMAL",
                                  "easyPay": {
                                    "provider": "토스페이",
                                    "amount": 0,
                                    "discountAmount": 0
                                  },
                                  "country": "KR",
                                  "failure": null,
                                  "isPartialCancelable": true,
                                  "receipt": {
                                    "url": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tviva20240213121757MvuS8&ref=PX"
                                  },
                                  "checkout": {
                                    "url": "https://api.tosspayments.com/v1/payments/5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1/checkout"
                                  },
                                  "currency": "KRW",
                                  "totalAmount": 1000,
                                  "balanceAmount": 1000,
                                  "suppliedAmount": 909,
                                  "vat": 91,
                                  "taxFreeAmount": 0,
                                  "metadata": null,
                                  "method": "카드",
                                  "version": "2022-11-16"
                                }
                        """;
    }

    private String createErrorBody() {
        return """
              "code": "INVALID_API_KEY",
              "message": "잘못된 시크릿키 연동 정보 입니다."
            """;
    }
}
