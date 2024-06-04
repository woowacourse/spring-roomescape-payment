package roomescape.web.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.response.time.AvailableReservationTimeResponse;
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberReservationTimeControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("예약 시간 목록을 조회하는데 성공하면 응답과 200 상태 코드를 반환한다.")
    @Test
    void return_200_when_find_all_reservation_times() {
        Member member = memberRepository.save(MemberFixture.MEMBER_SUN.create());
        String memberToken = jwtProvider.encode(member);

        timeRepository.save(TimeFixture.ONE_PM.create());
        timeRepository.save(TimeFixture.TWO_PM.create());
        timeRepository.save(TimeFixture.THREE_PM.create());

        RestAssured.given().log().all()
                .cookie("token", memberToken)
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(3));
    }

    @DisplayName("예약 가능한 시간을 조회하는데 성공하면 응답과 200 상태 코드를 반환한다.")
    @Test
    void return_200_when_find_available_reservation_times() {
        Member member = memberRepository.save(MemberFixture.MEMBER_SUN.create());
        LocalDate date = LocalDate.parse("2024-06-04");

        Theme java = themeRepository.save(ThemeFixture.THEME_JAVA.create());
        ReservationTime onePm = timeRepository.save(TimeFixture.ONE_PM.create());
        ReservationTime twoPm = timeRepository.save(TimeFixture.TWO_PM.create());
        ReservationTime threePm = timeRepository.save(TimeFixture.THREE_PM.create());
        reservationRepository.save(new Reservation(member, java, date, onePm, Status.RESERVED));

        List<AvailableReservationTimeResponse> actualResponse = RestAssured.given().log().all()
                .when().get("/times/available?date=2024-06-04&themeId=1")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", AvailableReservationTimeResponse.class);

        AvailableReservationTimeResponse response1 = new AvailableReservationTimeResponse(
                onePm.getId(), onePm.getStartAt(), true);
        AvailableReservationTimeResponse response2 = new AvailableReservationTimeResponse(
                twoPm.getId(), twoPm.getStartAt(), false);
        AvailableReservationTimeResponse response3 = new AvailableReservationTimeResponse(
                threePm.getId(), threePm.getStartAt(), false);

        assertThat(actualResponse)
                .usingRecursiveComparison()
                .isEqualTo(List.of(response1, response2, response3));
    }
}
