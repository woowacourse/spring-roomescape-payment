package roomescape.member.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.member.domain.Email;
import roomescape.member.domain.Member;

@DataJpaTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void 멤버를_저장하고_id로_조회할_수_있다() {
        Optional<Member> found = memberJpaRepository.findById(1L);

        assertThat(found.get().getName()).isEqualTo("유저1");
    }

    @Test
    void 이메일로_멤버를_조회할_수_있다() {
        Optional<Member> found = memberJpaRepository.findByEmail(new Email("member1@email.com"));

        assertThat(found.get().getId()).isEqualTo(1L);
    }

    @Test
    void 이메일_존재_여부를_확인할_수_있다() {
        boolean exists = memberJpaRepository.existsByEmail(new Email("member1@email.com"));

        assertThat(exists).isTrue();
    }

    @Test
    void 모든_멤버를_조회할_수_있다() {
        List<Member> members = memberJpaRepository.findAll();

        assertThat(members)
            .extracting(Member::getId)
            .containsExactly(1L, 2L, 3L);
    }
}
