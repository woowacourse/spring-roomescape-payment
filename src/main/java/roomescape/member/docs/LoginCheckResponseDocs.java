package roomescape.member.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.dto.response.LoginCheckResponse;

@Schema(description = "로그인 체크 응답")
public record LoginCheckResponseDocs(
        @Schema(description = "회원 이름", example = "홍길동")
        String name) {

    public static LoginCheckResponseDocs from(LoginCheckResponse response) {
        return new LoginCheckResponseDocs(response.name());
    }
} 