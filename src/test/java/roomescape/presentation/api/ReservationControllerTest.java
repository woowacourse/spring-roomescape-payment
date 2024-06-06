package roomescape.presentation.api;

import static org.hamcrest.Matchers.containsString;
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
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import static roomescape.application.dto.response.ReservationStatus.RESERVED;
import static roomescape.application.dto.response.ReservationStatus.WAITING;
import static roomescape.support.docs.DescriptorUtil.ERROR_MESSAGE_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.MEMBER_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.RESERVATION_REQUEST_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.RESERVATION_TIME_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.THEME_DESCRIPTOR;

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
import roomescape.presentation.dto.request.ReservationWebRequest;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class ReservationControllerTest extends BaseControllerTest {

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

    public FieldDescriptor[] RESERVATION_MINE_DESCRIPTOR = new FieldDescriptor[]{
            fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 및 대기 아이디입니다."),
            fieldWithPath("date").type(JsonFieldType.STRING).description("예약 및 대기 날짜입니다."),
            fieldWithPath("time").type(JsonFieldType.STRING).description("예약 및 대기 시간입니다."),
            fieldWithPath("theme").type(JsonFieldType.STRING).description("예약 및 대기 테마입니다."),
            fieldWithPath("status").type(JsonFieldType.STRING).description("예약 또는 대기 상태를 나타냅니다."),
            fieldWithPath("rank").type(JsonFieldType.NUMBER).description("예약 여부 또는 대기순번을 나타냅니다. 예약은 0입니다."),
            fieldWithPath("paymentKey").description("토스테서 제공하는 payment key입니다."),
            fieldWithPath("amount").description("예약시 결제된 금액입니다.")
    };


    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("나의 예약들을 예약 대기 순번과 함께 조회하고, 성공하면 200을 반환한다.")
    void getMyReservations() {
        // given
        LocalDate date = LocalDate.of(2024, 4, 6);

        Member user1 = memberRepository.save(Fixture.MEMBER_1);
        Member user2 = memberRepository.save(Fixture.MEMBER_2);

        Theme theme1 = themeRepository.save(Fixture.THEME_1);

        ReservationTime time1 = reservationTimeRepository.save(Fixture.RESERVATION_TIME_1);
        ReservationTime time2 = reservationTimeRepository.save(Fixture.RESERVATION_TIME_2);

        Reservation reservation = reservationRepository
                .save(new Reservation(new ReservationDetail(date, time1, theme1), user1));
        reservationRepository.save(new Reservation(new ReservationDetail(date, time1, theme1), user2));
        Waiting waiting = waitingRepository.save(new Waiting(new ReservationDetail(date, time2, theme1), user1));

        String token = tokenProvider.createToken(user1.getId().toString());

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("reservation/reservations-mine",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                        responseFields(fieldWithPath("[]").description("나의 예약 및 대기 배열입니다."))
                                .andWithPrefix("[].", RESERVATION_MINE_DESCRIPTOR)
                ))
                .cookie("token", token)
                .when().get("/reservations/mine")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .and()
                .body("size()", equalTo(2))
                .body("id", hasItems(reservation.getId().intValue(), waiting.getId().intValue()))
                .body("rank", hasItems(0, 1))
                .body("status", hasItems(RESERVED.name(), WAITING.name()));
    }

    @Nested
    @DisplayName("예약을 생성하는 경우")
    class AddReservation {

        @Test
        @DisplayName("성공할 경우 201을 반환한다.")
        void addReservation() {
            Member user = memberRepository.save(Fixture.MEMBER_USER);
            String token = tokenProvider.createToken(user.getId().toString());

            // given
            BDDMockito.doReturn(new PaymentResponse("DONE", "123"))
                    .when(paymentClient).confirmPayment(any());

            reservationTimeRepository.save(Fixture.RESERVATION_TIME_1);
            themeRepository.save(Fixture.THEME_1);

            ReservationWebRequest request = new ReservationWebRequest(
                    LocalDate.of(2024, 4, 9),
                    1L,
                    1L,
                    "test-paymentKey",
                    "test-orderId",
                    1L,
                    "test-paymentType"
            );

            RestAssured.given(spec).log().all()
                    .accept("application/json")
                    .filter(document("reservation/create-reservation",
                            requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                            requestFields(RESERVATION_REQUEST_DESCRIPTOR),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디입니다."),
                                    fieldWithPath("date").type(LocalDate.class).description("예약 날짜입니다."),
                                    subsectionWithPath("member").description("예약한 사용자 정보입니다."),
                                    subsectionWithPath("time").description("예약한 예약 시간 정보입니다."),
                                    subsectionWithPath("theme").description("예약한 예약 테마 정보입니다."),
                                    fieldWithPath("paymentKey").type(JsonFieldType.STRING)
                                            .description("payment key입니다."),
                                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("방탈출 결제 금액입니다.")
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
                    .body(request)
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.CREATED.value())
                    .header("Location", response -> equalTo("/reservations/" + response.path("id")))
                    .and()
                    .body("date", equalTo("2024-04-09"))
                    .body("member.id", equalTo(1))
                    .body("time.id", equalTo(1))
                    .body("theme.id", equalTo(1));
        }

        @Test
        @DisplayName("지나간 날짜/시간이면 400을 반환한다.")
        void failWhenDateTimePassed() {
            Member user = memberRepository.save(Fixture.MEMBER_USER);
            String token = tokenProvider.createToken(user.getId().toString());

            // given
            reservationTimeRepository.save(new ReservationTime(LocalTime.of(11, 0)));
            themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));

            ReservationWebRequest request = new ReservationWebRequest(
                    LocalDate.of(2024, 4, 7),
                    1L,
                    1L,
                    "test-paymentKey",
                    "test-orderId",
                    1L,
                    "test-paymentType"
            );

            RestAssured.given(spec).log().all()
                    .accept("application/json")
                    .filter(document("reservation/create-reservation",
                            requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                            requestFields(RESERVATION_REQUEST_DESCRIPTOR),
                            responseFields(ERROR_MESSAGE_DESCRIPTOR)))
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/reservations")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("지나간 날짜/시간에 대한 예약은 불가능합니다."));
        }
    }
}
