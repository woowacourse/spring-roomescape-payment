package roomescape.global.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

@Configuration
public class RestClientConfig {

    private final Timeout connectionTimeOut;
    private final Timeout socketTimeOut;
    private final Timeout readTimeOut;

    public RestClientConfig(
            @Value("${server.http.connection-timeout}") long connectionTimeOut,
            @Value("${server.http.socket-timeout}") long socketTimeOut,
            @Value("${server.http.read-timeout}") long readTimeOut) {
        this.connectionTimeOut = Timeout.ofSeconds(connectionTimeOut);
        this.socketTimeOut = Timeout.ofSeconds(socketTimeOut);
        this.readTimeOut = Timeout.ofSeconds(readTimeOut);
    }

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
                .setConnectTimeout(connectionTimeOut)
                .setSocketTimeout(socketTimeOut)
                .build();

        return PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(connectionConfig)
                .build();
    }

    private RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setResponseTimeout(readTimeOut)
                .build();
    }

    private CloseableHttpClient getHttpClient(PoolingHttpClientConnectionManager connManager, RequestConfig requestConfig) {
        return HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
