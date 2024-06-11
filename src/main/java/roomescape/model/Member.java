package roomescape.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import roomescape.exception.BadRequestException;

import java.util.Objects;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "멤버 ID", example = "1")
    private Long id;
    @Schema(description = "멤버 이름", example = "수달")
    private String name;
    @Enumerated(EnumType.STRING)
    @Schema(description = "멤버 역할", example = "MEMBER")
    private Role role;
    @Schema(description = "이메일", example = "otter@email.com")
    private String email;
    @Schema(description = "비밀번호", example = "1234")
    private String password;

    protected Member() {
    }

    public Member(Long id, String name, Role role, String email, String password) {
        validateNullOrBlank(name, "name");
        this.id = id;
        this.name = name;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public Member(String name, Role role, String email, String password) {
        this(null, name, role, email, password);
    }

    public Member(Long id, String name, Role role) {
        this(id, name, role, null, null);
    }

    private void validateNullOrBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(value, fieldName);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Member member = (Member) object;
        return Objects.equals(getId(), member.getId()) && Objects.equals(getName(), member.getName())
                && getRole() == member.getRole() && Objects.equals(getEmail(), member.getEmail())
                && Objects.equals(getPassword(), member.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getRole(), getEmail(), getPassword());
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
