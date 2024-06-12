package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Exception Information Model")
public record ExceptionInfo(@Schema(description = "Error code", example = "404")
                            String code,

                            @Schema(description = "Error message", example = "Resource not found")
                            String message) {
}
