package roomescape.auth.dto;

import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import roomescape.advice.exception.ExceptionTitle;
import roomescape.advice.exception.RoomEscapeException;

public class RequestCookies {

    private final List<Cookie> values;

    public RequestCookies(Cookie[] values) {
        if (values == null) {
            throw new RoomEscapeException("인증이 되지 않은 유저입니다.", ExceptionTitle.AUTHENTICATION_FAILED);
        }
        this.values = Arrays.asList(values);
    }

    public Map<String, String> toMap() {
        return values.stream()
                .collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
    }
}
