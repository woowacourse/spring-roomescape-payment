package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.BasicAcceptanceTest;
import roomescape.dto.request.member.MemberSignUpRequest;
import roomescape.exception.RoomescapeException;

class MemberServiceTest extends BasicAcceptanceTest {
    @Autowired
    private MemberService memberService;

    @DisplayName("요청으로 들어온 회원 정보가 예외 조건에 해당되지 않을 때 해당 회원의 정보를 저장한다.")
    @Test
    void save() {
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("새멤버", "newmember@wooteco.com", "wootecoCrew6!");
        memberService.save(memberSignUpRequest);

        assertThat(memberService.findAll()).hasSize(1);
    }

    @DisplayName("이미 존재하는 이메일을 저장할 시 예외를 발생시킨다")
    @Test
    void duplicateEmail() {
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("새멤버", "newmember@wooteco.com", "wootecoCrew6!");
        memberService.save(memberSignUpRequest);

        assertThatThrownBy(() -> memberService.save(memberSignUpRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage("이미 존재하는 아이디입니다.");
    }

    @DisplayName("저장되어 있는 회원 목록을 불러온다.")
    @Test
    void findAll() {
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("새멤버", "newmember@wooteco.com", "wootecoCrew6!");
        memberService.save(memberSignUpRequest);

        assertThat(memberService.findAll()).hasSize(1);
    }
}
