package roomescape.reservation;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

@SpringBootTest
@DirtiesContext
public abstract class BaseTest {

    @Autowired
    protected MockRestServiceServer server;

    @BeforeEach
    public void mockServer() {
        server.expect(ExpectedCount.manyTimes(), requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andRespond(withSuccess());
    }
}
