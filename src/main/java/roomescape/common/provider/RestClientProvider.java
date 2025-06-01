package roomescape.common.provider;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.common.config.ApiProperties;

public class RestClientProvider {

    public static RestClient.Builder createRestClient(ApiProperties apiProperties) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(apiProperties.getConnectTimeout());
        factory.setReadTimeout(apiProperties.getReadTimeout());

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(apiProperties.getBaseUrl());
    }
}
