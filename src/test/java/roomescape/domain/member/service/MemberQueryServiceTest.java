package roomescape.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import roomescape.domain.member.domain.Member;
import roomescape.domain.member.domain.Role;
import roomescape.domain.member.response.FindAllMemberResponse;
import roomescape.exception.NotFoundException;

@DataJpaTest
@Import(value = {MemberQueryService.class})
class MemberQueryServiceTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private MemberQueryService memberQueryService;

    @Nested
    @DisplayName("ID를 통해 유저를 조회할 수 있다.")
    public class getMemberById {

        @DisplayName("ID를 통해 유저를 조회할 수 있다.")
        @Test
        void canGetMemberById() {
            // given
            Member expectedMember = entityManager.persist(
                    Member.createWithoutId(Role.GENERAL, "회원", "member@test.com", "password123!"));

            entityManager.flush();

            // when
            Member actualMember = memberQueryService.getMemberById(expectedMember.getId());

            // then
            assertThat(actualMember).isEqualTo(expectedMember);
        }

        @DisplayName("유저가 없는 경우 유저를 조회할 수 없다.")
        @Test
        void cannotGetMemberById() {
            // given
            long wrongMemberId = 100L;

            // when & then
            assertThatThrownBy(() -> memberQueryService.getMemberById(wrongMemberId))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("ID에 해당하는 회원을 찾을 수 없습니다.");
        }
    }

    @DisplayName("모든 유저의 프로필을 조회할 수 있다.")
    @Test
    void canFindAllMemberProfile() {
        // given
        Member firstMember = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member1@test.com", "password123!"));
        Member secondMember = entityManager.persist(
                Member.createWithoutId(Role.GENERAL, "회원", "member2@test.com", "password123!"));

        entityManager.flush();

        // when
        List<FindAllMemberResponse> allMemberProfile = memberQueryService.findAllMemberProfile();

        // then
        assertThat(allMemberProfile)
                .extracting(FindAllMemberResponse::id)
                .containsExactlyInAnyOrder(firstMember.getId(), secondMember.getId());
    }
}
