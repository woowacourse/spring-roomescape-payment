package roomescape.service;

import static roomescape.service.util.TossPaymentDisplayableErrorCode.NOT_DISPLAYABLE_ERROR;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import roomescape.controller.dto.PaymentErrorMessageResponse;
import roomescape.global.exception.PaymentException;
import roomescape.service.config.TossPaymentConfigProperties;
import roomescape.service.dto.TossPaymentRequestDto;
import roomescape.service.dto.TossPaymentResponseDto;
import roomescape.service.util.TossPaymentDisplayableErrorCode;

@Service
public class TossPaymentService {

    private final TossPaymentConfigProperties properties;
    private final RestTemplate restTemplate;

    public TossPaymentService(TossPaymentConfigProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public TossPaymentResponseDto pay(String orderId, long amount, String paymentKey) {
        try {
            return restTemplate.postForObject(
                properties.getPaymentApprovalUrl(),
                new TossPaymentRequestDto(orderId, amount, paymentKey),
                TossPaymentResponseDto.class
            );
        } catch (RestClientResponseException e) {
            PaymentErrorMessageResponse response = e.getResponseBodyAs(PaymentErrorMessageResponse.class);
            if (response == null || TossPaymentDisplayableErrorCode.from(response.code()) == NOT_DISPLAYABLE_ERROR) {
                throw new PaymentException("결제가 승인되지 않았습니다. 같은 문제가 반복된다면 은행이나 카드사로 문의 바랍니다.");
            }
            throw new PaymentException(response.message());
        } catch (ResourceAccessException e) {
            throw new PaymentException("결제 서버와의 연결에 실패하였습니다.");
        }
    }
}
