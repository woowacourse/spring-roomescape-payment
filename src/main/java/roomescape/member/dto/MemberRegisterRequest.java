package roomescape.member.dto;

public record MemberRegisterRequest(
        String email,
        String password,
        String name
) {
}
