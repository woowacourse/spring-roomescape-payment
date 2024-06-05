package roomescape.service.login.dto;

import jakarta.validation.constraints.NotBlank;
import roomescape.domain.member.MemberEmail;
import roomescape.domain.member.MemberPassword;

public class LoginRequest {
    @NotBlank(message = "email 값이 null 또는 공백일 수 없습니다.")
    private final String email;
    @NotBlank(message = "password 값이 null 또는 공백일 수 없습니다.")
    private final String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public MemberEmail toMemberEmail() {
        return new MemberEmail(email);
    }

    public MemberPassword toMemberPassword() {
        return new MemberPassword(password);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
