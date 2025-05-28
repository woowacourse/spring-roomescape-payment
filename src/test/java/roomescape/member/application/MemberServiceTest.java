package roomescape.member.application;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static roomescape.fixture.domain.MemberFixture.notSavedMember1;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.exception.resource.ResourceNotFoundException;
import roomescape.fixture.config.TestConfig;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.ui.dto.MemberResponse;
import roomescape.member.ui.dto.SignUpRequest;

@DataJpaTest
@Import(TestConfig.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 회원을_추가한다() {
        // given
        final SignUpRequest request = new SignUpRequest(
                notSavedMember1().getEmail(),
                notSavedMember1().getPassword(),
                notSavedMember1().getName()
        );

        // when
        final MemberResponse.IdName savedMember = memberService.create(request);

        // then
        assertAll(
                () -> assertNotNull(savedMember),
                () -> assertNotNull(savedMember.id()),
                () -> assertEquals(request.name(), savedMember.name())
        );
    }

    @Test
    void 회원을_삭제한다() {
        // given
        final Member member = notSavedMember1();
        final Member savedMember = memberRepository.save(member);

        // when
        memberService.delete(savedMember.getId());

        // then
        Assertions.assertThatThrownBy(() -> memberRepository.getById(savedMember.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
