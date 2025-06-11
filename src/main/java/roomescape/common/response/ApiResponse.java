package roomescape.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "API 응답의 공통 포맷. 성공 여부, 데이터, 메시지를 포함합니다.")
public class ApiResponse<T> {
    @Schema(description = "API 성공 여부")
    private final boolean success;
    @Schema(description = "성공시의 응답 데이터 (nullable) (실패시 null)", nullable = true)
    private final T data;
    @Schema(description = "에러 메시지 (nullable) (성공 시 null)", nullable = true)
    private final String message;

    public static <T> ApiResponse<T> createSuccess(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> createSuccessWithNoData() {
        return new ApiResponse<>(true, null, null);
    }

    public static ApiResponse<?> createError(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
