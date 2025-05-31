package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.dto.response.MemberResponse;
import roomescape.service.member.MemberService;
import roomescape.test_util.ServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberServiceTest extends ServiceTest {

    @Autowired
    private MemberService memberService;

    @DisplayName("사용자를 정상적으로 추가한다")
    @Test
    void addMember() {
        // given
        final MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test", "test", "차니");

        // when
        final MemberRegisterResponse expected = memberService.addMember(memberRegisterRequest);

        // then
        assertAll(
                () -> assertThat(expected.name()).isEqualTo("차니"),
                () -> assertThat(expected.email()).isEqualTo("test")
        );
    }

    @DisplayName("이미 존재하는 이메일로 사용자를 추가할 시 예외가 발생한다.")
    @Test
    void addMemberWithDuplicateEmailTest() {
        // given
        final MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test", "test", "차니");
        memberService.addMember(memberRegisterRequest);

        // when, then
        final MemberRegisterRequest duplicateEmailMemberRegisterRequest = new MemberRegisterRequest("test", "test", "히포");
        assertThatThrownBy(() -> memberService.addMember(duplicateEmailMemberRegisterRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("모든 사용자를 조회한다")
    @Test
    void getAllMembers() {
        // given
        final MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test", "test", "차니");
        memberService.addMember(memberRegisterRequest);

        // when
        final List<MemberResponse> expected = memberService.getAllMembers();

        // then
        assertThat(expected).hasSize(1);

    }

    @DisplayName("사용자의 id로 사용자를 조회한다")
    @Test
    void getMemberById() {
        // given
        final MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest("test", "test", "차니");
        final MemberRegisterResponse saved = memberService.addMember(memberRegisterRequest);

        // when
        final Member expected = memberService.getMemberById(saved.id());

        // then
        assertAll(
                () -> assertThat(expected.getName()).isEqualTo("차니"),
                () -> assertThat(expected.getEmail()).isEqualTo("test")
        );
    }
}
