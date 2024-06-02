package roomescape.service.login.dto;

import jakarta.validation.constraints.NotBlank;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberEmail;
import roomescape.domain.member.MemberName;
import roomescape.domain.member.MemberPassword;
import roomescape.domain.member.MemberRole;

public class SignupRequest {
    @NotBlank(message = "email 값이 null 또는 공백일 수 없습니다.")
    private final String email;
    @NotBlank(message = "password 값이 null 또는 공백일 수 없습니다.")
    private final String password;
    @NotBlank(message = "name 값이 null 또는 공백일 수 없습니다.")
    private final String name;

    public SignupRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public Member toMember(MemberRole role) {
        return new Member(
                new MemberName(name),
                new MemberEmail(email),
                new MemberPassword(password),
                role
        );
    }

    public MemberEmail toMemberEmail() {
        return new MemberEmail(email);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
