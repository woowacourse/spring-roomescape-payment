package roomescape.presentation.api;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import static roomescape.support.docs.DescriptorUtil.MEMBER_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.RESERVATION_TIME_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.THEME_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.WAITING_DESCRIPTOR;

import java.time.LocalDate;
import java.time.LocalTime;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
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
import roomescape.presentation.dto.request.WaitingWebRequest;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class WaitingControllerTest extends BaseControllerTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("예약 대기를 추가하고 성공할 경우 201을 반환한다.")
    void addReservationWaiting() {
        Member user = memberRepository.save(Fixture.MEMBER_USER);
        String token = tokenProvider.createToken(user.getId().toString());

        // given
        ReservationTime reservationTime = reservationTimeRepository.save(Fixture.RESERVATION_TIME_1);
        Theme theme = themeRepository.save(Fixture.THEME_1);
        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationDetail detail = new ReservationDetail(date, reservationTime, theme);
        Member member = memberRepository.save(Fixture.MEMBER_2);
        reservationRepository.save(new Reservation(detail, member));

        WaitingWebRequest request = new WaitingWebRequest(date, reservationTime.getId(), theme.getId());

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("waiting/create-waiting",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                        requestFields(
                                WAITING_DESCRIPTOR[1],
                                WAITING_DESCRIPTOR[2],
                                WAITING_DESCRIPTOR[3]),
                        responseFields(
                                WAITING_DESCRIPTOR[1],
                                WAITING_DESCRIPTOR[0],
                                subsectionWithPath("member").description("예약한 사용자 정보입니다."),
                                subsectionWithPath("time").description("예약한 예약 시간 정보입니다."),
                                subsectionWithPath("theme").description("예약한 예약 테마 정보입니다.")),
                        responseFields(beneathPath("member"), MEMBER_DESCRIPTOR),
                        responseFields(beneathPath("time"), RESERVATION_TIME_DESCRIPTOR),
                        responseFields(beneathPath("theme"), THEME_DESCRIPTOR),
                        responseBody(beneathPath("member").withSubsectionId("member")),
                        responseBody(beneathPath("time").withSubsectionId("time")),
                        responseBody(beneathPath("theme").withSubsectionId("theme"))
                ))
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(request)
                .when().post("/waitings")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", response -> equalTo("/waitings/" + response.path("id")))
                .and()
                .body("date", equalTo("2024-04-09"))
                .body("member.id", equalTo(1))
                .body("time.id", equalTo(1))
                .body("theme.id", equalTo(1));
    }

    @Test
    @DisplayName("예약 대기를 제거하고 성공할 경우 200을 반환한다.")
    void deleteReservationWaiting() {
        Member user = memberRepository.save(Fixture.MEMBER_USER);
        String token = tokenProvider.createToken(user.getId().toString());

        // given
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));

        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationDetail detail = new ReservationDetail(date, reservationTime, theme);

        Waiting savedWaiting = waitingRepository.save(new Waiting(detail, user));

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("waiting/delete-waiting",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다."))
                ))
                .cookie("token", token)
                .when().delete("/waitings/" + savedWaiting.getId())
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(waitingRepository.findById(1L)).isEmpty();
        });
    }
}
