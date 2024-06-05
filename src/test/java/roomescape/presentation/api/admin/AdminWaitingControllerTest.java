package roomescape.presentation.api.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import java.time.LocalDate;
import java.time.LocalTime;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.application.dto.response.ReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.WaitingRepository;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.ReservationTimeRepository;
import roomescape.domain.reservation.detail.Theme;
import roomescape.domain.reservation.detail.ThemeRepository;
import roomescape.fixture.Fixture;
import roomescape.presentation.BaseControllerTest;

class AdminWaitingControllerTest extends BaseControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Test
    @DisplayName("예약 대기 목록을 조회하고 성공하면 200을 반환한다.")
    void getReservationWaitings() {
        Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
        String token = tokenProvider.createToken(admin.getId().toString());

        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));
        Member member = memberRepository.save(new Member("ex@gmail.com", "password", "유저", Role.USER));
        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationDetail reservationDetail = new ReservationDetail(date, reservationTime, theme);
        waitingRepository.save(new Waiting(reservationDetail, member));

        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().get("/admin/waitings")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1))
                .body("member.name", hasItems("유저"))
                .body("time.startAt", hasItems("11:00"))
                .body("theme.name", hasItems("테마 이름"));

    }

    @Test
    @DisplayName("예약 대기에서 예약으로 변경을 승인하고 성공하면 200을 반환한다.")
    void approveReservationWaiting() {
        Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
        String token = tokenProvider.createToken(admin.getId().toString());

        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));
        Member member = memberRepository.save(new Member("ex@gmail.com", "password", "유저", Role.USER));
        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationDetail reservationDetail = new ReservationDetail(date, reservationTime, theme);
        Waiting savedWaiting = waitingRepository.save(new Waiting(reservationDetail, member));

        ReservationResponse reservationResponse = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .pathParam("id", savedWaiting.getId())
                .when().post("/admin/waitings/{id}/approve")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(ReservationResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(waitingRepository.findById(savedWaiting.getId())).isEmpty();
            softly.assertThat(reservationRepository.findById(reservationResponse.id())).isPresent();
        });
    }

    @Test
    @DisplayName("예약 대기에서 예약으로 변경을 거부하고 성공하면 200을 반환한다.")
    void rejectReservationWaiting() {
        Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
        String token = tokenProvider.createToken(admin.getId().toString());

        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));
        Member member = memberRepository.save(new Member("ex@gmail.com", "password", "유저", Role.USER));
        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationDetail reservationDetail = new ReservationDetail(date, reservationTime, theme);
        Waiting savedWaiting = waitingRepository.save(new Waiting(reservationDetail, member));

        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().delete("/admin/waitings/" + savedWaiting.getId() + "/reject")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value());

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(waitingRepository.findById(savedWaiting.getId())).isEmpty();
        });
    }
}
