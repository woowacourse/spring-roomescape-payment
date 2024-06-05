package roomescape.presentation.api.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.restassured.RestAssured;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.fixture.Fixture;
import roomescape.presentation.BaseControllerTest;

class AdminMemberControllerTest extends BaseControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("모든 회원을 조회할 경우 성공하면 200을 반환한다.")
    void getAllMembers() {
        Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
        String token = tokenProvider.createToken(admin.getId().toString());

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/members")
                .then().log().all()
                .assertThat()
                .body("size()", equalTo(1))
                .body("id", hasItems(1))
                .body("name", hasItems("어드민"));
    }
}
