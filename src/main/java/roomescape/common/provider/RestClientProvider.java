package roomescape.common.provider;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.common.config.ApiProperties;

public class RestClientProvider {

    public static RestClient createRestClient(ApiProperties apiProperties) {
        String credentials = apiProperties.getSecretKey() + ":";
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(apiProperties.getConnectTimeout());
        factory.setReadTimeout(apiProperties.getReadTimeout());

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(apiProperties.getBaseUrl())
                .defaultHeader("Authorization", "Basic " + encoded)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
