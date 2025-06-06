package roomescape.common.configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientConfig {

    private final int CONNECT_TIME_LIMIT = 3000;
    private final int READ_TIME_LIMIT = 7000;

    @Bean
    public RestClient restClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIME_LIMIT);
        requestFactory.setReadTimeout(READ_TIME_LIMIT);

        return RestClient.builder()
            .baseUrl("https://api.tosspayments.com")
            .requestFactory(requestFactory)
            .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Room Escape API 문서")
                .version("v1.0.0")
                .description("방탈출 REST API 명세입니다.")
            );
    }
}
