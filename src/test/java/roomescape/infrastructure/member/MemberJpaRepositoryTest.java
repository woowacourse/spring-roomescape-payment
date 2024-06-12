package roomescape.infrastructure.member;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;

@DataJpaTest
class MemberJpaRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("이메일로 회원이 존재하는지 확인한다.")
    void existsByEmailTest() {
        entityManager.persist(new Member("name", "email@test.com", "password"));
        boolean existsByEmail = memberRepository.existsByEmail(new Email("email@test.com"));
        assertThat(existsByEmail).isTrue();
    }
}
