package roomescape.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    @NotNull
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private MemberRole role;

    private String sessionId;

    @Builder
    public Member(String email, String password, String name, MemberRole role) {
        //TODO : 도메인 필드 검증 로직 추가
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public void updateSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }
}
