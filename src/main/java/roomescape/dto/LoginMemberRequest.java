package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;
import roomescape.domain.Name;
import roomescape.domain.Role;

@Schema(description = "로그인 된 회원 정보 DTO")
public record LoginMemberRequest(@Schema(example = "1") long id,
                                 @Schema(description = "회원 이름", example = "안돌") Name name,
                                 Role role) {
    public static LoginMemberRequest from(Member member) {
        return new LoginMemberRequest(member.getId(), member.getName(), member.getRole());
    }
}
