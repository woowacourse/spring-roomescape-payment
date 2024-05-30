package roomescape.infrastructure.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossErrorResponse(
        @JsonProperty("code")
        String errorCode,
        String message
) {
}
