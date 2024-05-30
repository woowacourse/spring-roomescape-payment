package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.model.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;


    @DisplayName("아이디와 비밀번호로 사용자를 조회한다.")
    @Test
    void should_find_member_when_given_email_and_password() {
        Member member = memberRepository.findByEmailAndPassword("sun@email.com", "1111").get();

        assertThat(member.getId()).isEqualTo(1L);
    }

    @DisplayName("아이디로 사용자를 조회한다.")
    @Test
    void should_find_member_when_given_member_id() {
        Member member = memberRepository.findById(1L).get();

        assertThat(member.getId()).isEqualTo(1L);
    }

    @DisplayName("모든 사용자를 조회한다.")
    @Test
    void should_find_all_member() {
        List<Member> members = memberRepository.findAll();

        assertThat(members.size()).isEqualTo(2);
    }
}
