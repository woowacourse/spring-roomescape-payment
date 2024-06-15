package roomescape;

import io.restassured.RestAssured;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.client.PaymentClient;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = {"classpath:clean_data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class BasicAcceptanceTest {
    @LocalServerPort
    private int port;

    @MockBean
    protected PaymentClient paymentClient;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        PaymentRequest paymentRequest = new PaymentRequest("orderId", BigDecimal.valueOf(1000), "paymentKey");
        BDDMockito.given(paymentClient.requestPayment(paymentRequest))
                .willReturn(new PaymentResponse("paymentKey", BigDecimal.valueOf(1000)));
    }
}
