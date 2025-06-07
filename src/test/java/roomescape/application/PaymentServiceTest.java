package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import roomescape.domain.Payment;
import roomescape.infrastructure.repository.PaymentRepository;
import roomescape.infrastructure.thirdparty.TossPaymentRestClient;
import roomescape.presentation.dto.request.PaymentProcessRequest;
import roomescape.presentation.dto.response.PaymentResponse;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private TossPaymentRestClient tossPaymentRestClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void 결제를_진행한다() throws JsonProcessingException {
        PaymentProcessRequest paymentProcessRequest = new PaymentProcessRequest("test", "test", "1000");
        Payment payment = Payment.create("test", "test", 1000);
        String paymentString = """
                {
                    "paymentKey": "test",
                    "orderId": "test",
                    "totalAmount": 1000
                }
                """;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(paymentString, HttpStatus.OK);

        JsonNode jsonNode = new ObjectMapper().readTree(paymentString);

        when(objectMapper.readTree(paymentString)).thenReturn(jsonNode);
        when(tossPaymentRestClient.getPaymentResponse(paymentProcessRequest)).thenReturn(responseEntity);
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentResponse paymentResponse = paymentService.process(paymentProcessRequest);
        assertThat(paymentResponse.paymentKey()).isEqualTo(payment.getPaymentKey());
        verify(tossPaymentRestClient, times(1)).getPaymentResponse(paymentProcessRequest);
    }
}
