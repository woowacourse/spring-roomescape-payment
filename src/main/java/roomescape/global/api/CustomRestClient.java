package roomescape.global.api;

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
    private final CustomRequestMapper customRequestMapper;
    private final RestClient restClient;

    public CustomRestClient(
            final CustomResponseMapper customResponseMapper,
            final CustomRequestMapper customRequestMapper,
            final RestClient restClient
    ) {
        this.customResponseMapper = customResponseMapper;
        this.customRequestMapper = customRequestMapper;
        this.restClient = restClient;
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
                    .body(customRequestMapper.convertMap(body))
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
