package roomescape.integration.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.GlobalConfig;
import roomescape.global.config.ClockConfig;
import roomescape.member.controller.MemberService;
import roomescape.member.controller.dto.MemberResponse;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberEncodedPassword;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;

@Transactional
@SpringBootTest
@Import(GlobalConfig.class)
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    void 모든_멤버를_조회한다() {
        // given
        Member member1 = memberRepository.save(
                new Member(
                        null,
                        new MemberName("짭한스1"),
                        new MemberEmail("leehyeonsu4848@gmail.com"),
                        new MemberEncodedPassword("gdgd"),
                        MemberRole.MEMBER
                )
        );

        Member member2 = memberRepository.save(
                new Member(null,
                        new MemberName("짭한스2"),
                        new MemberEmail("leehyeonsu488@gmail.com"),
                        new MemberEncodedPassword("gdgdsad"),
                        MemberRole.MEMBER
                )
        );

        // when
        List<MemberResponse> allMembers = memberService.findAllMembers();

        // then
        assertThat(allMembers).containsExactly(
                new MemberResponse(member1.getId(), member1.getName().name()),
                new MemberResponse(member2.getId(), member2.getName().name())
        );
    }
}
