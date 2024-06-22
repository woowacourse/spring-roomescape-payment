package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;

import static roomescape.fixture.MemberFixture.memberFixture;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import roomescape.domain.member.Member;
import roomescape.domain.member.Name;
import roomescape.domain.member.Role;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("사용자를 저장한다.")
    void saveMember() {
        // given
        Member member = new Member(new Name("미르"), "mir@email.com", "1234", Role.MEMBER);

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember.getId()).isNotNull();
    }

    @Test
    @DisplayName("Id에 해당하는 사용자를 조회한다.")
    void findMemberById() {
        Member member = new Member(new Name("미르"), "mir@email.com", "1234", Role.MEMBER);
        Member saved = memberRepository.save(member);

        var actual = memberRepository.findById(member.getId());

        assertThat(actual).hasValue(saved);
    }

    @Test
    @DisplayName("Id에 해당하는 사용자가 없으면 빈 옵셔널을 조회한다.")
    void returnEmptyOptionalWhenFindMemberByNotExistingId() {
        // given
        final Long notExistingId = 0L;

        // when
        final Optional<Member> actual = memberRepository.findById(notExistingId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("email에 해당하는 사용자를 조회한다.")
    void findMemberByEmail() {
        Member member = new Member(new Name("미르"), "mir@email.com", "1234", Role.MEMBER);

        Member saved = memberRepository.save(member);

        // when
        final Optional<Member> actual = memberRepository.findByEmail(member.getEmail());

        // then
        assertThat(actual).hasValue(saved);
    }

    @Test
    @DisplayName("email에 해당하는 사용자가 없으면 빈 옵셔널을 조회한다.")
    void returnEmptyOptionalWhenFindMemberByNotExistingEmail() {
        // given
        final String notExistingEmail = "odd@email.com";

        // when
        final Optional<Member> actual = memberRepository.findByEmail(notExistingEmail);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("사용자 전체 목록을 조회한다.")
    void findAllMembers() {
        memberRepository.save(memberFixture(2L));
        memberRepository.save(memberFixture(3L));
        memberRepository.save(memberFixture(4L));

        final List<Member> actual = memberRepository.findAll();
        assertThat(actual).hasSize(3);
    }
}
