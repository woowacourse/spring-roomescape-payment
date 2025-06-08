package roomescape.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.client.dto.TossPaymentConfirmResponse;
import roomescape.client.dto.TossServerErrorResponse;
import roomescape.domain.payment.OrderItem;
import roomescape.dto.reservation.TossPaymentConfirmRequestDto;

import java.util.Base64;
import roomescape.exception.PaymentConfirmClientException;
import roomescape.exception.PaymentConfirmServerException;
import roomescape.exception.common.BadRequestException;

@Component
public class PaymentClient {

    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1";
    private static final String PAYMENT_CONFIRM_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    public static final String PAYMENT_AUTHORIZATION_HEADER =
            "Basic " + Base64.getEncoder().encodeToString(PAYMENT_CONFIRM_SECRET_KEY.getBytes());
    public static final long FIXED_ORDER_QUANTITY = 1L;

    private final ObjectMapper objectMapper;

    private final RestClient restClient;

    public PaymentClient(ObjectMapper objectMapper, final RestClient restClient) {
        this.objectMapper = objectMapper;
        this.restClient = restClient.mutate()
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (req, res) -> {
                    TossServerErrorResponse tossServerErrorResponse = this.objectMapper.readValue(
                            res.getBody(), TossServerErrorResponse.class);
                    if (tossServerErrorResponse.isInvisibleError()) {
                        throw new PaymentConfirmServerException(tossServerErrorResponse.getMessage(), tossServerErrorResponse.getCode());
                    }
                    throw new PaymentConfirmClientException(tossServerErrorResponse.getMessage(), tossServerErrorResponse.getCode());
                })
                .defaultStatusHandler(HttpStatusCode::is5xxServerError, (req, res) -> {
                    TossServerErrorResponse tossServerErrorResponse = this.objectMapper.readValue(
                            res.getBody(), TossServerErrorResponse.class);
                    throw new PaymentConfirmServerException(tossServerErrorResponse.getMessage(), tossServerErrorResponse.getCode());
                })
                .build();
    }

    public TossPaymentConfirmResponse confirmPayment(TossPaymentConfirmRequestDto requestDto) {
        validatePaymentAmount(requestDto.orderId(), requestDto.amount());
        return restClient.post()
                .uri(TOSS_API_URL + "/payments/confirm")
                .header("Authorization", PAYMENT_AUTHORIZATION_HEADER)
                .body(requestDto)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(TossPaymentConfirmResponse.class)
                .getBody();
    }

    private void validatePaymentAmount(String orderId, Long orderAmount) {
        OrderItem orderItem = OrderItem.findByOrderIdPrefix(orderId);
        if (!orderItem.isSameAmount(orderAmount, FIXED_ORDER_QUANTITY)) {
            throw new BadRequestException("결제 요청 금액과 실제 주문 가격이 불일치합니다.");
        }
    }
}
