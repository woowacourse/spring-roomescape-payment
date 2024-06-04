package roomescape.member.domain;

import java.util.Collections;
import java.util.List;

import roomescape.member.entity.Member;

public class Members {
    private final List<Member> members;

    public Members(List<Member> members) {
        this.members = members;
    }

    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }
}
