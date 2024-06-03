package roomescape.service;

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
import roomescape.service.util.TossPaymentErrorCodeUtils;

@Service
public class TossPaymentService {

    private final TossPaymentConfigProperties properties;
    private final RestTemplate restTemplate;
    private final TossPaymentErrorCodeUtils errorCodeUtils;

    public TossPaymentService(TossPaymentConfigProperties properties,
        RestTemplate restTemplate,
        TossPaymentErrorCodeUtils errorCodeUtils
    ) {
        this.properties = properties;
        this.restTemplate = restTemplate;
        this.errorCodeUtils = errorCodeUtils;
    }

    @Transactional
    public void pay(String orderId, long amount, String paymentKey) {
        try {
            TossPaymentResponseDto responseDto = restTemplate.postForEntity(
                properties.getPaymentApprovalUrl(),
                new TossPaymentRequestDto(orderId, amount, paymentKey),
                TossPaymentResponseDto.class
            ).getBody();
        } catch (RestClientResponseException e) {
            PaymentErrorMessageResponse response = e.getResponseBodyAs(PaymentErrorMessageResponse.class);
            if (response == null || errorCodeUtils.isNotDisplayableErrorCode(response.code())) {
                throw new PaymentException("결제가 승인되지 않았습니다. 같은 문제가 반복된다면 은행이나 카드사로 문의 바랍니다.");
            }
            throw new PaymentException(response.message());
        } catch (ResourceAccessException e) {
            throw new PaymentException("결제 서버와의 연결에 실패하였습니다.");
        }
    }
}
