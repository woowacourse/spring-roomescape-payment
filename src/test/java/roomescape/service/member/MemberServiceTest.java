package roomescape.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.dto.member.MemberResponse;
import roomescape.exception.RoomEscapeException;
import roomescape.service.ServiceBaseTest;

class MemberServiceTest extends ServiceBaseTest {

    @Autowired
    MemberService memberService;

    @Test
    void 모든_멤버_조회() {
        // when
        List<MemberResponse> allMembers = memberService.findAllMembers();

        // then
        assertThat(allMembers).hasSize(5);
    }

    @Test
    void 존재하지_않는_id로_조회할_경우_예외_발생() {
        // given
        List<MemberResponse> allMembers = memberService.findAllMembers();
        Long notExistId = allMembers.size() + 1L;

        // when, then
        assertThatThrownBy(() -> memberService.getMemberById(notExistId))
                .isInstanceOf(RoomEscapeException.class);
    }
}
