package roomescape.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.USER_EMAIL;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.exception.RoomEscapeException;

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

    @Test
    void 존재하지_않는_id로_조회시_예외_발생() {
        // when, then
        assertThatThrownBy(() -> memberRepository.findByIdOrThrow(6L))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    void 존재하지_않는_이메일로_조회시_예외_발생() {
        // given
        Email email = new Email("testEmail");

        // when, then
        assertThatThrownBy(() -> memberRepository.findByEmailOrThrow(email))
                .isInstanceOf(RoomEscapeException.class);
    }
}
