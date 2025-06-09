package roomescape.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.domain.Member;

public record SignupResponse(
        @Schema(description = "회원 ID", example = "1")
        Long id,
        @Schema(description = "회원 이름", example = "홍길동")
        String name,
        @Schema(description = "이메일", example = "test@test.com")
        String email,
        @Schema(description = "비밀번호", example = "password123")
        String password) {

    public static SignupResponse from(Member member) {
        return new SignupResponse(member.getId(), member.getName(), member.getEmail(), member.getPassword());
    }
}
