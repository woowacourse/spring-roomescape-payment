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

@Service
public class TossPaymentService {

    private final TossPaymentConfigProperties properties;
    private final RestTemplate restTemplate;

    public TossPaymentService(TossPaymentConfigProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
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
            if (response == null) {
                throw new PaymentException("결제가 승인되지 않았습니다.");
            }
            throw new PaymentException(response.message());
        } catch (ResourceAccessException e) {
            throw new PaymentException("결제 서버와의 연결에 실패하였습니다.");
        }
    }
}
