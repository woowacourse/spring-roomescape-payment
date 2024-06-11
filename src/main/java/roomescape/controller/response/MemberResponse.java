package roomescape.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.model.Member;
import roomescape.model.Role;

public class MemberResponse {

    @Schema(description = "멤버 ID", example = "1")
    private Long id;
    @Schema(description = "멤버 이름", example = "수달")
    private String name;
    @Schema(description = "멤버 역할", example = "MEMBER")
    private Role role;
    @Schema(description = "이메일", example = "otter@email.com")
    private String email;
    @Schema(description = "비밀번호", example = "1234")
    private String password;

    private MemberResponse() {
    }

    public MemberResponse(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.role = member.getRole();
        this.email = member.getEmail();
        this.password = member.getPassword();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
