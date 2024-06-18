package roomescape.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "API 응답 시에 사용합니다.")
public record RoomEscapeApiResponse<T>(
        @Schema(description = "응답 메시지", defaultValue = SUCCESS_MESSAGE) String message,
        @Schema(description = "응답 바디") T data
) {

    private static final String SUCCESS_MESSAGE = "요청이 성공적으로 수행되었습니다.";

    public static <T> RoomEscapeApiResponse<T> success(T data) {
        return new RoomEscapeApiResponse<>(SUCCESS_MESSAGE, data);
    }

    public static <T> RoomEscapeApiResponse<T> success() {
        return new RoomEscapeApiResponse<>(SUCCESS_MESSAGE, null);
    }
}
