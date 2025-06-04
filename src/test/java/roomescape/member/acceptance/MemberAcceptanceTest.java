package roomescape.member.acceptance;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import fixture.MemberFixture;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.helper.TestHelper;
import roomescape.member.dto.request.MemberCreateRequest;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;
import roomescape.member.repository.MemberRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("회원을 생성한다.")
    void createMember() {
        // given
        Member member = MemberFixture.createDefault();
        var request = new MemberCreateRequest(member.getName(), member.getEmail(), member.getPassword());

        // when & then
        TestHelper.post("/members", request)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", is(member.getName()))
                .body("email", is(member.getEmail()))
                .body("role", is(member.getRole().getValue()));
    }

    @Test
    @DisplayName("모든 회원을 조회한다.")
    void getAllMembers() {
        // given
        Member member = MemberFixture.createDefault();
        memberRepository.save(member);
        TestHelper.login(member.getEmail(), member.getPassword());

        // when & then
        TestHelper.get("/members")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].name", is(member.getName()));
    }

    @Test
    @DisplayName("회원을 삭제한다.")
    void deleteMember() {
        // given
        Member member = MemberFixture.create(RoleType.ADMIN);
        memberRepository.save(member);
        String token = TestHelper.login(member.getEmail(), member.getPassword());

        // when & then
        TestHelper.deleteWithToken("/members/" + 1, token)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        TestHelper.get("/members")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }
}
