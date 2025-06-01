package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.MemberRole;
import roomescape.test_util.RepositoryTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MemberRepositoryTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("사용자의 이메일을 통해 사용자를 찾는다")
    void findByEmailTest() {
        // given
        Member member = insertMember("이메일", "비밀번호", "이름", MemberRole.USER);

        // when
        final Optional<Member> found = memberRepository.findByEmail(member.getEmail());

        // then
        assertAll(
                () -> assertThat(found).isPresent(),
                () -> assertThat(found.get().getId()).isEqualTo(member.getId())
        );
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 사용자를 검색한다")
    void findByNonExistEmailTest() {
        // given
        String email = "non-exist@example.com";

        // when
        final Optional<Member> found = memberRepository.findByEmail(email);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("이메일이 존재하는 지 확인한다")
    void existByEmailTest() {
        // given
        Member member = insertMember("이메일", "비밀번호", "이름", MemberRole.USER);
        String nonExistEmail = "non-exist@example.com";

        // when
        final boolean exist = memberRepository.existByEmail(member.getEmail());
        final boolean nonExist = memberRepository.existByEmail(nonExistEmail);

        // then
        assertAll(
                () -> assertThat(exist).isTrue(),
                () -> assertThat(nonExist).isFalse()
        );
    }

    @Test
    @DisplayName("이름으로 사용자가 존재하는 지 확인한다")
    void findByNameTest() {
        // given
        Member member = insertMember("이메일", "비밀번호", "이름", MemberRole.USER);
        String nonExistName = "non-exist";

        // when
        final boolean exist = memberRepository.existByName(member.getName());
        final boolean nonExist = memberRepository.existByName(nonExistName);

        // then
        assertThat(exist).isTrue();
        assertThat(nonExist).isFalse();
    }
}
