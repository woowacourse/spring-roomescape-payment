package roomescape.presentation.api.reservation;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.infrastructure.security.JwtProvider;

import java.time.Clock;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private Clock clock;

    private Member savedMember;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        savedMember = memberRepository.save(new Member("tester", new Email("tester@email.com"), "password"));
    }

    @Test
    void 예약결제_토스서버오류() {
        // given
        final String requestBody = """
                    {
                        "date": "%s",
                        "timeId": 2,
                        "themeId": 1,
                        "paymentKey": "pk_test_123456789",
                        "orderId": "ORDER-123456",
                        "amount": 10000,
                        "paymentType": "NORMAL"
                    }
                """.formatted(LocalDate.now(clock).plusDays(1));

        // when
        final var response = RestAssured.given()
                .cookie("token", jwtProvider.issue(savedMember.getId()).value())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post("/reservations")
                .then()
                .extract()
                .response();

        // then
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(400);
            assertThat(response.asString()).isEqualTo("{\"message\":\"존재하지 않는 결제 정보입니다\"}");
        });
    }
}
