package roomescape.member.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.dto.request.LoginMember;

@Schema(description = "로그인 멤버")
public record LoginMemberDocs(
        @Schema(description = "회원 ID", example = "1")
        Long id) {

    public LoginMember toLoginMember() {
        return new LoginMember(this.id);
    }
} 