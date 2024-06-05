package roomescape.presentation.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import static roomescape.fixture.Fixture.MEMBER_1;
import static roomescape.fixture.Fixture.THEME_1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.ReservationTimeRepository;
import roomescape.domain.reservation.detail.Theme;
import roomescape.domain.reservation.detail.ThemeRepository;
import roomescape.presentation.BaseControllerTest;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class ReservationTimeControllerTest extends BaseControllerTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("이용한 가능한 시간들을 조회하고, 성공하면 200을 반환한다.")
    void getAllReservationTimes() {
        reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 30)));

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/times")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .and()
                .body("size()", equalTo(1))
                .body("id", hasItems(1))
                .body("startAt", hasItems("10:30"));

    }

    @Test
    @DisplayName("예약 가능한 시간을 조회하고 성공하면 200을 반환한다.")
    void getAvailableReservationTimes() {
        LocalDateTime now = LocalDateTime.of(2024, 4, 8, 10, 0);
        LocalDate date = LocalDate.of(2024, 4, 9);
        Theme theme = themeRepository.save(THEME_1);

        ReservationTime time1 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(9, 0)));
        reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));

        ReservationDetail detail = new ReservationDetail(date, time1, theme);

        Member member = memberRepository.save(MEMBER_1);

        reservationRepository.save(Reservation.create(now, detail, member));

        RestAssured.given().log().all()
                .param("date", date.toString())
                .param("themeId", theme.getId())
                .when().get("/times/available")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .and()
                .body("size()", equalTo(2))
                .body("timeId", hasItems(1, 2))
                .body("startAt", hasItems("09:00", "10:00"))
                .body("alreadyBooked", hasItems(true, false));
    }
}
