package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import roomescape.application.dto.PaymentProcessRequest;
import roomescape.application.exception.PaymentException;
import roomescape.domain.Payment;
import roomescape.infrastructure.repository.PaymentRepository;
import roomescape.infrastructure.thirdparty.PaymentRestClient;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRestClient paymentRestClient;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void 결제를_진행한다() throws JsonProcessingException {
        PaymentProcessRequest paymentProcessRequest = new PaymentProcessRequest("test", "test", "1000");
        Payment payment = Payment.create("test", "test");
        String paymentString = """
                {
                    "paymentKey": "test",
                    "orderId": "test"
                }
                """;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(paymentString, HttpStatus.OK);

        JsonNode jsonNode = new ObjectMapper().readTree(paymentString);

        when(objectMapper.readTree(paymentString)).thenReturn(jsonNode);
        when(paymentRestClient.getPaymentResponse(paymentProcessRequest)).thenReturn(responseEntity);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment resultPayment = paymentService.process(paymentProcessRequest);
        assertThat(resultPayment.getPaymentKey()).isEqualTo(payment.getPaymentKey());
        verify(paymentRestClient, times(1)).getPaymentResponse(paymentProcessRequest);
    }

    @Test
    void 사용자_에러를_반환한다() throws JsonProcessingException {
        PaymentProcessRequest paymentProcessRequest = new PaymentProcessRequest("test", "test", "1000");
        String paymentString = """
                {
                    "code": "test",
                    "message": "test"
                }
                """;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(paymentString, HttpStatus.BAD_REQUEST);

        JsonNode jsonNode = new ObjectMapper().readTree(paymentString);

        when(objectMapper.readTree(paymentString)).thenReturn(jsonNode);
        when(paymentRestClient.getPaymentResponse(paymentProcessRequest)).thenReturn(responseEntity);

        assertThatThrownBy(() -> paymentService.process(paymentProcessRequest))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    void 서버_에러를_반환한다() throws JsonProcessingException {
        PaymentProcessRequest paymentProcessRequest = new PaymentProcessRequest("test", "test", "1000");
        String paymentString = """
                {
                    "code": "test",
                    "message": "test"
                }
                """;
        ResponseEntity<String> responseEntity = new ResponseEntity<>(paymentString, HttpStatus.INTERNAL_SERVER_ERROR);

        JsonNode jsonNode = new ObjectMapper().readTree(paymentString);

        when(objectMapper.readTree(paymentString)).thenReturn(jsonNode);
        when(paymentRestClient.getPaymentResponse(paymentProcessRequest)).thenReturn(responseEntity);

        assertThatThrownBy(() -> paymentService.process(paymentProcessRequest))
                .isInstanceOf(PaymentException.class);
    }
}
