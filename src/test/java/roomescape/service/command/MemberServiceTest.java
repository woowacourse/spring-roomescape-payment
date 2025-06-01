package roomescape.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import roomescape.domain.Member;
import roomescape.domain.Role;
import roomescape.dto.business.MemberCreationContent;
import roomescape.dto.response.MemberProfileResponse;
import roomescape.exception.BadRequestException;
import roomescape.repository.MemberRepository;
import roomescape.service.query.MemberQueryService;

@DataJpaTest
class MemberServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private MemberRepository memberRepository;

    private MemberService memberService;
    private MemberQueryService memberQueryService;

    @BeforeEach
    void setup() {
        memberQueryService = new MemberQueryService(memberRepository);
        memberService = new MemberService(memberRepository, memberQueryService);
    }


    @Nested
    @DisplayName("유저를 추가할 수 있다.")
    public class addMember {

        @DisplayName("정상적으로 유저를 추가할 수 있다.")
        @Test
        void canAddMember() {
            // given
            MemberCreationContent creationContent =
                    new MemberCreationContent(Role.GENERAL, "회원", "test@test.com", "qwer1234!");

            // when
            MemberProfileResponse response = memberService.addMember(creationContent);

            // then
            Member expectedMember = entityManager.find(Member.class, response.id());
            assertAll(
                    () -> assertThat(response.id()).isEqualTo(expectedMember.getId()),
                    () -> assertThat(response.name()).isEqualTo(creationContent.name()),
                    () -> assertThat(response.roleName()).isEqualTo(creationContent.role().toString())
            );
        }

        @DisplayName("이메일이 중복인 경우 회원 추가가 불가능하다.")
        @Test
        void cannotAddMember() {
            // given
            Member alreadySavedMember = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원", "member1@test.com", "password123!"));

            MemberCreationContent creationContent =
                    new MemberCreationContent(Role.GENERAL, "회원", alreadySavedMember.getEmail(), "qwer1234!");

            entityManager.flush();

            // when & then
            assertThatThrownBy(() -> memberService.addMember(creationContent))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 존재하는 계정입니다.");
        }
    }
}
