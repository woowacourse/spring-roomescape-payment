package roomescape.web.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.request.time.ReservationTimeRequest;
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Member;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.TimeFixture;
import roomescape.infrastructure.repository.MemberJpaRepository;
import roomescape.support.DatabaseCleanupListener;


@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AdminReservationTimeControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private MemberJpaRepository memberRepository;

    String adminToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        Member member = memberRepository.save(MemberFixture.MEMBER_SOLAR.create());
        adminToken = jwtProvider.encode(member);
    }

    @DisplayName("예약 시간을 생성하는데 성공하면 응답과 201 상태 코드를 반환한다.")
    @Test
    void return_201_when_create_reservation_time() {
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.parse("10:00"));

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("예약 시간을 삭제하는데 성공하면 응답과 204 상태 코드를 반환한다.")
    @Test
    void return_204_when_delete_reservation_time() {
        timeRepository.save(TimeFixture.ONE_PM.create());

        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().delete("/admin/times/1")
                .then().log().all()
                .statusCode(204);
    }
}
