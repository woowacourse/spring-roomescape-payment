package roomescape.global.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.global.converter.CustomResponseMapper;
import roomescape.global.dto.ExternalApiErrorResponse;
import roomescape.global.exception.ExternalApiException;

@Component
public class CustomRestClient {
    private final CustomResponseMapper customResponseMapper;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public CustomRestClient(
            final CustomResponseMapper customResponseMapper,
            final RestClient restClient,
            final ObjectMapper objectMapper
    ) {
        this.customResponseMapper = customResponseMapper;
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public <T> T post(
            CustomRequestUri customRequestUri,
            AuthToken authToken,
            Object body,
            Class<T> responseType,
            Class errorResponseType
            ) {
        try {
            return restClient.post()
                    .uri(customRequestUri.getUriPath())
                    .header(HttpHeaders.AUTHORIZATION, authToken.generateToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper.convertValue(body, Map.class))
                    .retrieve()
                    .body(responseType);
        } catch (RestClientResponseException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new ExternalApiException(new ExternalApiErrorResponse(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    customResponseMapper.convertJsonToErrorResponse(responseBody, errorResponseType).getMessage()
            ));
        }
    }
}
