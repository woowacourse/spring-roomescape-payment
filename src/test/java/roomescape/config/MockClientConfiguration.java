package roomescape.config;

import org.springframework.boot.test.web.client.MockServerRestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

@Configuration
public class MockClientConfiguration {

    private MockRestServiceServer mockRestServiceServer;

    @Bean
    public RestClient restClient() {
        MockServerRestClientCustomizer customizer = new MockServerRestClientCustomizer();
        RestClient.Builder builder = RestClient.builder();
        customizer.customize(builder);
        RestClient restClient = builder.build();
        mockRestServiceServer = customizer.getServer();
        return restClient;
    }

    @Bean
    public MockRestServiceServer server(RestClient restClient) {
        return mockRestServiceServer;
    }
}
