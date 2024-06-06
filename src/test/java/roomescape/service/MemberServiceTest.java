package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.request.MemberLoginRequest;
import roomescape.exception.AuthenticationException;
import roomescape.exception.NotFoundException;
import roomescape.model.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class MemberServiceTest {

    private MemberService memberService;

    @Autowired
    public MemberServiceTest(MemberService memberService) {
        this.memberService = memberService;
    }

    @DisplayName("아이디와 비밀번호가 같은 유저가 존재하면 해당 유저를 반환한다.")
    @Test
    void should_find_member_when_member_exist() {
        MemberLoginRequest request = new MemberLoginRequest("1111", "otter@email.com");

        Member findMember = memberService.getMemberByEmailAndPassword(request);

        assertThat(findMember.getName()).isEqualTo("수달");
    }

    @DisplayName("아이디와 비밀번호 같은 유저가 없으면 예외가 발생한다.")
    @Test
    void should_throw_exception_when_member_not_exist() {
        MemberLoginRequest request = new MemberLoginRequest("3333", "movin@email.com");

        assertThatThrownBy(() -> memberService.getMemberByEmailAndPassword(request))
                .isInstanceOf(AuthenticationException.class);
    }

    @DisplayName("아이디를 통해 사용자 이름을 조회한다.")
    @Test
    void should_find_member_when_give_id() {
        Member memberById = memberService.getMemberById(1L);

        assertThat(memberById.getId()).isEqualTo(1);
    }

    @DisplayName("주어진 아이디에 해당하는 사용자가 없으면 예외가 발생한다.")
    @Test
    void should_not_find_member_and_throw_exception_when_member_id_not_exist() {
        assertThatThrownBy(() -> memberService.getMemberById(99L))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("모든 사용자를 조회한다.")
    @Test
    void should_find_all_members() {
        List<Member> members = memberService.findAllMembers();

        assertThat(members).hasSize(2);
    }
}
