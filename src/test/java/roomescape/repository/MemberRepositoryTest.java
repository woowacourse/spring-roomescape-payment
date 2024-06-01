package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.USER_EMAIL;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;

@Sql("/test-data.sql")
class MemberRepositoryTest extends RepositoryBaseTest{

    @Autowired
    MemberRepository memberRepository;

    @Test
    void 등록된_이메일로_멤버_조회() {
        // given
        Email email = new Email(USER_EMAIL);

        // when
        Member member = memberRepository.findByEmail(email).orElseThrow();

        // then
        assertThat(member.getEmail()).isEqualTo(email.getEmail());
    }
}
