package roomescape.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfig {

    @Bean
    public RestClient restClient(
            @Value("${client.connection-timeout-ms}") int connectionTimeoutMs,
            @Value("${client.read-timeout-ms}") int readTimeoutMs
    ) {
        RestTemplate template = new RestTemplate(clientHttpRequestFactory(connectionTimeoutMs, readTimeoutMs));

        return RestClient.builder(template)
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory(int connectionTimeoutMs, int readTimeoutMs) {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeoutMs))
                .build();

        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofMilliseconds(readTimeoutMs))
                .build();

        PoolingHttpClientConnectionManager connectionManager = createConnectionManager(socketConfig, connectionConfig);

        HttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private PoolingHttpClientConnectionManager createConnectionManager(
            SocketConfig socketConfig,
            ConnectionConfig connectionConfig
    ) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultSocketConfig(socketConfig);
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        return connectionManager;
    }
}
