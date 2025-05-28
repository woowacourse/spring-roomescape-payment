package roomescape.global.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.global.converter.JsonStringToObject;
import roomescape.global.dto.ExternalApiErrorResponse;
import roomescape.global.exception.ExternalApiException;

@Component
public class CustomRestClient {
    private final JsonStringToObject jsonStringToObject;
    private final RestClient restClient;
    private final CustomRequestMapper mapper;

    public CustomRestClient(
            final JsonStringToObject jsonStringToObject,
            final RestClient restClient,
            final CustomRequestMapper mapper
    ) {
        this.jsonStringToObject = jsonStringToObject;
        this.restClient = restClient;
        this.mapper = mapper;
    }

     /*
    TODO
        예외 추상화 고민 ????
     */

    public <T> T post(CustomRequestUri customRequestUri, AuthToken authToken, Object body, Class<T> responseType) {
        try {
            return restClient.post()
                    .uri(customRequestUri.getUriPath())
                    .header(HttpHeaders.AUTHORIZATION, authToken.generateToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.convertMap(body))
                    .retrieve()
                    .body(responseType);
        } catch (RestClientResponseException e) {
            String responseBody = e.getResponseBodyAsString();
            throw new ExternalApiException(new ExternalApiErrorResponse(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    jsonStringToObject.convertTossApiErrorResponse(responseBody).message()
            ));
        }
    }
}
