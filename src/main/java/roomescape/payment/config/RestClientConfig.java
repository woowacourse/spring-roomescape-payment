package roomescape.payment.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

@Configuration
public class RestClientConfig {

    private Timeout CONNECTION_TIMEOUT = Timeout.ofSeconds(3);
    private Timeout SOCKET_TIMEOUT = Timeout.ofSeconds(2);
    private Timeout READ_TIMEOUT = Timeout.ofSeconds(30);

    @Bean
    public Builder restClientBuilder() {
        PoolingHttpClientConnectionManager connManager = getPoolingHttpClientConnectionManager();

        RequestConfig requestConfig = getRequestConfig();

        CloseableHttpClient httpClient = getHttpClient(connManager, requestConfig);

        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    private PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();

        return PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(connectionConfig)
                .build();
    }

    private RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setResponseTimeout(READ_TIMEOUT)
                .build();
    }

    private CloseableHttpClient getHttpClient(PoolingHttpClientConnectionManager connManager, RequestConfig requestConfig) {
        return HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
