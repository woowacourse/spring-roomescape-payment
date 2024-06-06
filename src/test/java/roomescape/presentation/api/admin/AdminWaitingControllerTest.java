package roomescape.presentation.api.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
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

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

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

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filters(document("waiting/all-waitings",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                        responseFields(fieldWithPath("[]").description("예약 대기 배열입니다."))
                                .andWithPrefix("[].",
                                        WAITING_DESCRIPTOR[0],
                                        WAITING_DESCRIPTOR[1],
                                        subsectionWithPath("member").description("예약한 사용자 정보입니다."),
                                        subsectionWithPath("time").description("예약한 예약 시간 정보입니다."),
                                        subsectionWithPath("theme").description("예약한 예약 테마 정보입니다.")),
                        responseFields(beneathPath("[].member"), MEMBER_DESCRIPTOR),
                        responseFields(beneathPath("[].time"), RESERVATION_TIME_DESCRIPTOR),
                        responseFields(beneathPath("[].theme"), THEME_DESCRIPTOR),
                        responseBody(beneathPath("[].member").withSubsectionId("member")),
                        responseBody(beneathPath("[].time").withSubsectionId("time")),
                        responseBody(beneathPath("[].theme").withSubsectionId("theme"))))
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

        ReservationResponse reservationResponse = RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document(
                        "waiting/approve-reservation",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                        responseFields(
                                WAITING_DESCRIPTOR[0],
                                WAITING_DESCRIPTOR[1],
                                subsectionWithPath("member").description("예약한 사용자 정보입니다."),
                                subsectionWithPath("time").description("예약한 예약 시간 정보입니다."),
                                subsectionWithPath("theme").description("예약한 예약 테마 정보입니다."),
                                fieldWithPath("paymentKey").description("토스에서 제공하는 paymentKey입니다."),
                                fieldWithPath("amount").description("예약 결제 금액입니다.")
                        ),
                        responseFields(beneathPath("member"), MEMBER_DESCRIPTOR),
                        responseFields(beneathPath("time"), RESERVATION_TIME_DESCRIPTOR),
                        responseFields(beneathPath("theme"), THEME_DESCRIPTOR),
                        responseBody(beneathPath("member").withSubsectionId("member")),
                        responseBody(beneathPath("time").withSubsectionId("time")),
                        responseBody(beneathPath("theme").withSubsectionId("theme"))
                ))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().post("/admin/waitings/{id}/approve", savedWaiting.getId())
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

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document(
                        "waiting/reject-reservation",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다."))))
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().delete("/admin/waitings/{id}/reject", savedWaiting.getId())
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value());

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(waitingRepository.findById(savedWaiting.getId())).isEmpty();
        });
    }
}
