package roomescape.member.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import fixture.MemberFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.member.dto.request.MemberCreateRequest;
import roomescape.member.entity.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.member.service.MemberService;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원을 생성한다.")
    void createMember() {
        // given
        Member member = MemberFixture.createDefault();
        var request = new MemberCreateRequest(member.getName(), member.getEmail(), member.getPassword());

        // when
        var response = memberService.createMember(request);

        // then
        assertAll(
                () -> assertThat(response.id()).isNotNull(),
                () -> assertThat(response.name()).isEqualTo(member.getName()),
                () -> assertThat(response.email()).isEqualTo(member.getEmail()),
                () -> assertThat(response.password()).isEqualTo(member.getPassword()),
                () -> assertThat(response.role()).isEqualTo(member.getRole())
        );
    }

    @Test
    @DisplayName("모든 회원을 조회한다.")
    void getAllMembers() {
        // given
        List<Member> members = MemberFixture.createDefaultList(2);
        memberRepository.saveAll(members);

        // when
        var responses = memberService.getAllMembers();

        // then
        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> assertThat(responses.get(0).name()).isEqualTo(members.get(0).getName()),
                () -> assertThat(responses.get(1).name()).isEqualTo(members.get(1).getName())
        );
    }

    @Test
    @DisplayName("회원을 삭제한다.")
    void deleteMember() {
        // given
        var member = MemberFixture.createDefault();
        memberRepository.save(member);

        // when
        memberService.deleteMember(member.getId());

        // then
        var members = memberService.getAllMembers();
        assertThat(members).isEmpty();
    }
} 
