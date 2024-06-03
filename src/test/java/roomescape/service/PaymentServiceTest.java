package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.config.RestTemplateConfig;
import roomescape.controller.PaymentApproveResponse;
import roomescape.controller.dto.PaymentApproveRequest;
import roomescape.exception.customexception.PaymentException;


@RestClientTest(value = PaymentService.class)
@Import(RestTemplateConfig.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MockRestServiceServer mockServer;

    private PaymentApproveRequest request = new PaymentApproveRequest("paymentKey", "orderId", "amount");
    private String expectedAPIUrl = "https://api.tosspayments.com/v1/payments/confirm";
    private String expectedError = """
            {
               "code": "NOT_FOUND_PAYMENT",
               "message": "존재하지 않는 결제 입니다."
            }
            """;

    @DisplayName("결제 API 요청 성공")
    @Test
    void success() {
        // given
        String expectedJson = """
                {
                  "mId": "tosspayments",
                  "lastTransactionKey": "9C62B18EEF0DE3EB7F4422EB6D14BC6E",
                  "paymentKey": "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                  "orderId": "MC4wODU4ODQwMzg4NDk0",
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
                    "receiptUrl": "https://dashboard.tosspayments.com/receipt/redirection?transactionId=tviva20240213121757MvuS8&ref=PX",
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
                  "easyPayAmount": 0,
                  "easyPayDiscountAmount": 0,
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
                  "method": "카드",
                  "version": "2022-11-16"
                }
                """;
        mockServer.expect(requestTo(expectedAPIUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedJson, MediaType.APPLICATION_JSON));

        // when
        PaymentApproveResponse response = paymentService.pay(request);
        mockServer.verify();

        // then
        Assertions.assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("400에러 반환시 custom exception으로 예외가 전환된다.")
    void is4XXException_PaymentException(){
        // given
        mockServer.expect(requestTo(expectedAPIUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest().body(expectedError).contentType(MediaType.APPLICATION_JSON));

        // when & then
        assertThatThrownBy(() -> paymentService.pay(request))
                .isInstanceOf(PaymentException.class);

        mockServer.verify();
    }

    @Test
    @DisplayName("500에러 반환시 custom exeption으로 예외가 전환된다.")
    void is5XXException_PaymentException(){
        // given
        mockServer.expect(requestTo(expectedAPIUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError().body(expectedError).contentType(MediaType.APPLICATION_JSON));

        // when & then
        assertThatThrownBy(() -> paymentService.pay(request))
                .isInstanceOf(PaymentException.class);

        mockServer.verify();
    }
}
