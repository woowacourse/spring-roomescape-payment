package roomescape;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.util.PaymentClientTestConfiguration;

@SpringBootTest(
        classes = PaymentClientTestConfiguration.class
)
class RoomescapeApplicationTest {

    @Test
    void contextLoads() {
    }

}
