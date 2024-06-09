package roomescape.security.authentication;

import roomescape.domain.member.Member;

public class DefaultAuthentication implements Authentication {
    private final Member member;

    private DefaultAuthentication(Member member) {
        this.member = member;
    }

    public static Authentication from(Member member) {
        return new DefaultAuthentication(member);
    }

    @Override
    public Member getPrincipal() {
        return member;
    }

    @Override
    public boolean isNotAdmin() {
        return member.isNotAdmin();
    }
}
