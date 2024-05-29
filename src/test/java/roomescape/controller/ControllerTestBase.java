package roomescape.controller;

import static org.mockito.ArgumentMatchers.any;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import roomescape.service.payment.PaymentRestClient;
import roomescape.service.payment.dto.PaymentResult;
import roomescape.service.reservation.dto.ReservationRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public abstract class ControllerTestBase {
    @LocalServerPort
    private int port;

    @MockBean
    protected PaymentRestClient restClient;

    @BeforeEach
    void initPort() {
        RestAssured.port = port;
        Mockito.when(restClient.confirm(any())).thenReturn(new PaymentResult(1000, "orderId", "payKey"));
    }
}
