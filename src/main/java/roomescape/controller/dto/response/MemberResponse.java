package roomescape.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;

public record MemberResponse(
        @Schema(description = "회원 고유 번호", example = "1")
        Long id,
        @Schema(description = "이메일", example = "kyummi@woowha.com")
        String email,
        @Schema(description = "회원명", example = "켬미")
        String name,
        @Schema(description = "역할", example = "USER")
        Role role
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getRole()
        );
    }
}
