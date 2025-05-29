package roomescape;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import roomescape.client.TossPaymentsClient;

@TestConfiguration
public class TestClientConfig {

    public final String BASE_URL = "https://api.tosspayments.com/v1/payments";

    public final RestClient.Builder TEST_BUILDER = RestClient.builder()
            .baseUrl(BASE_URL);

    @Bean("MockRestServiceServer")
    public MockRestServiceServer mockRestServiceServer() {
        return MockRestServiceServer
                .bindTo(TEST_BUILDER)
                .build();
    }

    @Bean
    @DependsOn(value = {"MockRestServiceServer"})
    public TossPaymentsClient tossPaymentsClient() {
        return new TossPaymentsClient(TEST_BUILDER.build());
    }
}
