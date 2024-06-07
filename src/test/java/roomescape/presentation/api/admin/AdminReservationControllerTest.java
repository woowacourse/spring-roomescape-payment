package roomescape.presentation.api.admin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import static roomescape.support.docs.DescriptorUtil.MEMBER_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.RESERVATION_TIME_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.THEME_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.WAITING_DESCRIPTOR;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
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
import roomescape.infra.payment.PaymentClient;
import roomescape.infra.payment.PaymentResponse;
import roomescape.presentation.BaseControllerTest;
import roomescape.presentation.dto.request.AdminReservationWebRequest;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class AdminReservationControllerTest extends BaseControllerTest {

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

    @SpyBean
    private PaymentClient paymentClient;

    private RequestSpecification spec;

    public FieldDescriptor[] RESERVATION_REQUEST_DESCRIPTOR = new FieldDescriptor[]{
            fieldWithPath("date").type(LocalDate.class).description("예약할 날짜입니다."),
            fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약할 예약 시간 아이디입니다."),
            fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("예약할 테마 아이디입니다."),
            fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("토스에서 제공되는 payment key입니다."),
            fieldWithPath("orderId").type(JsonFieldType.STRING).description("결제 아이디입니다."),
            fieldWithPath("amount").type(JsonFieldType.NUMBER).description("예약할 방탈출의 결제 금액입니다."),
            fieldWithPath("paymentType").type(JsonFieldType.STRING).description("결제 타입을 나타냅니다."),
            fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("예약하는 사용자를 나타냅니다.")};

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("조건에 맞는 예약들(예약 대기 제외)을 조회하고 성공할 경우 200을 반환한다.")
    void getReservationsByConditions() {
        Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
        String token = tokenProvider.createToken(admin.getId().toString());

        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));
        Member member = memberRepository.save(new Member("ex@gmail.com", "password", "유저", Role.USER));
        Member member1 = memberRepository.save(new Member("ex2@gmail.com", "password", "유저2", Role.USER));
        reservationRepository.save(new Reservation(new ReservationDetail(date, reservationTime, theme), member));
        waitingRepository.save(new Waiting(new ReservationDetail(date, reservationTime, theme), member1));

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("reservation/search-reservations",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                        queryParameters(
                                parameterWithName("memberId").description("검색할 사용자 아이디입니다.").optional(),
                                parameterWithName("themeId").description("검색할 테마 아이디입니다.").optional(),
                                parameterWithName("dateFrom").description("검색하고 싶은 예약의 시작 날짜입니다.").optional(),
                                parameterWithName("dateTo").description("검색하고 싶은 예약의 끝 날짜입니다.").optional()
                        ),
                        responseFields(fieldWithPath("[]").description("예약 대기 배열입니다."))
                                .andWithPrefix("[].",
                                        WAITING_DESCRIPTOR[0],
                                        WAITING_DESCRIPTOR[1],
                                        subsectionWithPath("member").description("예약한 사용자 정보입니다."),
                                        subsectionWithPath("time").description("예약한 예약 시간 정보입니다."),
                                        subsectionWithPath("theme").description("예약한 예약 테마 정보입니다."),
                                        fieldWithPath("paymentKey").description("토스에서 제공하는 payment key입니다."),
                                        fieldWithPath("amount").description("예약 결제 금액입니다.")),
                        responseFields(beneathPath("[].member").withSubsectionId("member"), MEMBER_DESCRIPTOR),
                        responseFields(beneathPath("[].time").withSubsectionId("time"), RESERVATION_TIME_DESCRIPTOR),
                        responseFields(beneathPath("[].theme").withSubsectionId("theme"), THEME_DESCRIPTOR),
                        responseBody(beneathPath("[].member").withSubsectionId("member")),
                        responseBody(beneathPath("[].time").withSubsectionId("time")),
                        responseBody(beneathPath("[].theme").withSubsectionId("theme"))))
                .cookie("token", token)
                .param("memberId", 2)
                .param("themeId", 1)
                .param("dateFrom", "2024-04-01")
                .param("dateTo", "2024-05-01")
                .when().get("/admin/reservations")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body("size()", equalTo(1))
                .body("id", hasItems(1))
                .body("date", hasItems("2024-04-09"))
                .body("member.id", hasItems(2))
                .body("time.id", hasItems(1))
                .body("theme.id", hasItems(1));
    }

    @Nested
    @DisplayName("예약을 생성하는 경우")
    class AddReservation {

        @Test
        @DisplayName("성공할 경우 201을 반환한다.")
        void addAdminReservation() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            // given
            BDDMockito.doReturn(new PaymentResponse("DONE", "123"))
                    .when(paymentClient).confirmPayment(any());

            ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
            Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));

            AdminReservationWebRequest request = new AdminReservationWebRequest(
                    LocalDate.of(2024, 6, 22),
                    reservationTime.getId(),
                    theme.getId(),
                    "test-paymentKey",
                    "test-orderId",
                    1L,
                    "test-paymentType",
                    admin.getId()
            );

            RestAssured.given(spec).log().all()
                    .accept("application/json")
                    .filter(document("reservation/create-reservation/admin",
                            requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                            requestFields(RESERVATION_REQUEST_DESCRIPTOR),
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
                            responseBody(beneathPath("theme").withSubsectionId("theme"))))
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("date", equalTo("2024-06-22"))
                    .body("member.name", equalTo("어드민"))
                    .body("time.startAt", equalTo("11:00"))
                    .body("theme.name", equalTo("테마 이름"));
        }

        @Test
        @DisplayName("어드민 권한이 아닐 경우 403을 반환한다.")
        void addAdminReservationFailWhenNotAdmin() {
            Member user = memberRepository.save(Fixture.MEMBER_USER);
            String token = tokenProvider.createToken(user.getId().toString());

            AdminReservationWebRequest request = new AdminReservationWebRequest(
                    LocalDate.of(2024, 6, 22),
                    1L,
                    1L,
                    "test-paymentKey",
                    "test-orderId",
                    100L,
                    "toss-payment",
                    1L
            );

            RestAssured.given(spec).log().all()
                    .accept("application/json")
                    .filter(document("reservation/create-reservation/admin/exception/unauthorized",
                            requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                            requestFields(RESERVATION_REQUEST_DESCRIPTOR),
                            responseFields(
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지입니다."))
                    ))
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Test
    @DisplayName("예약을 삭제하고 성공할 경우 204를 반환한다.")
    void deleteReservationById() {
        Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
        String token = tokenProvider.createToken(admin.getId().toString());

        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
        Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));
        Member member = memberRepository.save(new Member("ex@gmail.com", "password", "유저", Role.USER));
        ReservationDetail detail = new ReservationDetail(date, reservationTime, theme);
        Reservation savedReservation = reservationRepository.save(new Reservation(detail, member));

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("reservation/delete-reservation/admin",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다."))))
                .cookie("token", token)
                .when().delete("/admin/reservations/" + savedReservation.getId())
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
