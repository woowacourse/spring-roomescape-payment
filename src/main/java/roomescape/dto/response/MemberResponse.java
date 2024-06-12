package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;

@Schema(description = "Member Response Model")
public record MemberResponse(@Schema(description = "Member ID", example = "SuperNovaCantStopHyperStella")
                             Long id,

                             @Schema(description = "Member name", example = "Karina")
                             String name) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }
}
