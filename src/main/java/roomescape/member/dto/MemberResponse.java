package roomescape.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.model.Member;

public record MemberResponse(Long id, String name,
                             @Schema(example = "user@mail.com")
                             String email) {
    public static MemberResponse from(final Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName().getValue(),
                member.getEmail().getValue()
        );
    }
}
