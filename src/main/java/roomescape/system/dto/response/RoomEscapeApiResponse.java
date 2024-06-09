package roomescape.system.dto.response;

public record RoomEscapeApiResponse<T>(String message, T data) {

    private static final String SUCCESS_MESSAGE = "요청이 성공적으로 수행되었습니다.";

    public static <T> RoomEscapeApiResponse<T> success(T data) {
        return new RoomEscapeApiResponse<>(SUCCESS_MESSAGE, data);
    }

    public static <T> RoomEscapeApiResponse<T> success() {
        return new RoomEscapeApiResponse<>(SUCCESS_MESSAGE, null);
    }
}
