package roomescape.domain.member;

public interface MemberPasswordEncoder {
    String encode(final String password);
}
