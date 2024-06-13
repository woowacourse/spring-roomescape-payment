package roomescape.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentErrorResponse(
        @JsonProperty("code")
        String errorCode,

        String message

) {

}
