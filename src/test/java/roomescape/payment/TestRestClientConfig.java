package roomescape.payment;

import java.time.Duration;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@TestConfiguration
public class TestRestClientConfig {

    private static final int TEST_TIMEOUT = (int) Duration.ofSeconds(3).toMillis();

    @Bean
    @Primary
    public RestClient testRestClient(MockWebServer mockWebServer) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TEST_TIMEOUT);
        requestFactory.setReadTimeout(TEST_TIMEOUT);

        return RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .requestFactory(requestFactory)
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public MockWebServer mockWebServer() {
        return new MockWebServer();
    }
}
