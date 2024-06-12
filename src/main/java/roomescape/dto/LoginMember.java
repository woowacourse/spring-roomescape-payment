package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;

@Schema(description = "Logged-In Member Information Model")
public record LoginMember(@Schema(description = "Member ID", example = "123")
                          Long id) {

    public static LoginMember from(Member member) {
        return new LoginMember(member.getId());
    }
}
