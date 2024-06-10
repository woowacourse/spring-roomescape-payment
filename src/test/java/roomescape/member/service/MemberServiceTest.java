package roomescape.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.member.dto.MemberResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MemberServiceTest {
    @LocalServerPort
    private int port;
    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("멤버를 모두 조회할 수 있다.")
    @Test
    void findMembersTest() {
        List<MemberResponse> expected = List.of(
                new MemberResponse(1L, "관리자"),
                new MemberResponse(2L, "브라운"),
                new MemberResponse(3L, "브리"),
                new MemberResponse(4L, "오리"));

        List<MemberResponse> actual = memberService.findMembers();

        assertThat(actual).isEqualTo(expected);
    }
}
