package roomescape.domain.member.controller;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import roomescape.domain.auth.dto.AccessTokenContent;
import roomescape.domain.member.domain.Member;
import roomescape.domain.member.domain.Role;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.utility.JwtTokenProvider;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MemberApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    public JwtTokenProvider tokenProvider;

    @AfterEach
    void setup() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("모든 회원의 프로필을 조회할 수 있다.")
    void canFindAllMember() {
        // given
        AccessTokenContent tokenContent = new AccessTokenContent(100L, Role.ADMIN, "qwer1234!");
        String accessToken = tokenProvider.createAccessToken(tokenContent);

        memberRepository.save(Member.createWithoutId(Role.GENERAL, "회원1", "test1@test.com", "qwer1234!"));
        memberRepository.save(Member.createWithoutId(Role.GENERAL, "회원2", "test2@test.com", "qwer1234!"));
        memberRepository.save(Member.createWithoutId(Role.GENERAL, "회원3", "test3@test.com", "qwer1234!"));

        // when & then
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie("access", accessToken)
                .port(port)
                .when().get("/members")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(3));
    }
}
