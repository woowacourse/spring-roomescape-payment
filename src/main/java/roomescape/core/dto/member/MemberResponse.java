package roomescape.core.dto.member;

import roomescape.core.domain.Member;

public class MemberResponse {
    private final Long id;
    private final String name;

    public MemberResponse(final Member member) {
        this.id = member.getId();
        this.name = member.getName();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
