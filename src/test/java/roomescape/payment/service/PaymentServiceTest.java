package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static roomescape.fixture.ServerClientFixture.BASE_URL;
import static roomescape.fixture.ServerClientFixture.MAPPER;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.client.MockRestServiceServer;
import roomescape.TestClientConfig;
import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.client.dto.PaymentsConfirmResponse;
import roomescape.payment.domain.Payment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestClientConfig.class)
class PaymentServiceTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        server.reset();
    }

    @DisplayName("결제 승인 요청을 보내고 그 응답 결과를 저장한다.")
    @Test
    void testConfirmAndSavePayment() throws JsonProcessingException {
        // given
        PaymentsConfirmRequest request = new PaymentsConfirmRequest("aaa", "111", 1000L);
        PaymentsConfirmResponse expectedResponse = new PaymentsConfirmResponse("aaa", 1000L);
        server.expect(requestTo(BASE_URL + "/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .body(MAPPER.writeValueAsString(expectedResponse))
                        .contentType(MediaType.APPLICATION_JSON));
        // when
        Payment payment = paymentService.confirmAndSavePayment(request);
        // then
        assertAll(
                () -> assertThat(payment.getPaymentKey().getValue()).isEqualTo("aaa"),
                () -> assertThat(payment.getAmount().getValue()).isEqualTo(1000L)
        );
    }
}
