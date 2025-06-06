package roomescape;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled("필요 시 해제")
@ActiveProfiles("default")
@SpringBootTest
class RoomescapeApplicationTest {

        @Value("${toss.payments.base-url}")
        private String baseUrl;

        @Value("${toss.payments.widget-secret-key}")
        private String secretKey;

        @Test
        void contextLoads() {
        }

        @Test
        @DisplayName("Toss Payments 설정이 올바르게 로드되는지 확인")
        void checkTossPaymentsConfiguration() {
                assertThat(baseUrl)
                        .isNotNull()
                        .isNotBlank()
                        .isEqualTo("https://api.tosspayments.com");

                assertThat(secretKey)
                        .isNotNull()
                        .isNotBlank()
                        .doesNotContain("${TOSS_SECRET_KEY}");
        }
}
