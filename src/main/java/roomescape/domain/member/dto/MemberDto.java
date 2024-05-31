package roomescape.domain.member.dto;

import roomescape.domain.member.model.Member;
import roomescape.domain.member.model.MemberEmail;
import roomescape.domain.member.model.MemberName;
import roomescape.domain.member.model.MemberPassword;
import roomescape.domain.member.model.MemberRole;

public record MemberDto(
        Long id,
        MemberRole role,
        MemberEmail email,
        MemberName name,
        MemberPassword password
) {
    public static MemberDto from(Member member) {
        return new MemberDto(
                member.getId(),
                member.getRole(),
                member.getEmail(),
                member.getName(),
                member.getPassword()
        );
    }
}
