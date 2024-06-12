package roomescape.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

import roomescape.client.fake.FakeRestClient;

@TestConfiguration
public class ClientConfig {

    @Bean
    @Primary
    public RestClient fakeRestClient() {
        return new FakeRestClient();
    }
}
