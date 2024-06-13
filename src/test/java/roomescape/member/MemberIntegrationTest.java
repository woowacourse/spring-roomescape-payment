package roomescape.member;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;
import roomescape.util.IntegrationTest;
import roomescape.util.RestDocsConfiguration;

@IntegrationTest
class MemberIntegrationTest extends RestDocsConfiguration {

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void init() {
        RestAssured.port = this.port;
    }

    @Test
    @DisplayName("회원 목록 조회 성공")
    void getReservationTimes() {
        memberRepository.save(new Member("몰리", Role.USER, "login@naver.com", "hihi"));
        memberRepository.save(new Member("로키", Role.USER, "qwer@naver.com", "hihi"));

        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .when().get("/members")
                .then().log().all()

                .statusCode(200)
                .body("id", hasItems(1, 2))
                .body("size()", equalTo(2));
    }
}
