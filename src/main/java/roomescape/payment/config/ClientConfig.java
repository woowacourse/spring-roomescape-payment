package roomescape.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.payment.resolver.PaymentResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class ClientConfig {

    @Bean
    public PaymentResolver todoRestClient() {
        return new PaymentResolver(
                RestClient.builder()
                        .baseUrl("https://api.tosspayments.com")
                        .defaultHeader("Content-Type", "application/json")
                        .requestInterceptor(loggingInterceptor()) // 인터셉터 추가
                        .build()
        );
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            System.out.println("=== RestClient 요청 정보 ===");
            System.out.println("HTTP Method: " + request.getMethod());
            System.out.println("URI: " + request.getURI());
            System.out.println("Headers: " + request.getHeaders());
            System.out.println("Body: " + new String(body, StandardCharsets.UTF_8));
            System.out.println("========================");

            ClientHttpResponse response = execution.execute(request, body);

            System.out.println("=== RestClient 응답 정보 ===");
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Headers: " + response.getHeaders());
            System.out.println("=========================");

            return response;
        };
    }
}
