package roomescape.presentation.api.admin;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import static roomescape.support.docs.DescriptorUtil.ERROR_MESSAGE_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.RESERVATION_TIME_DESCRIPTOR;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import roomescape.application.dto.request.ReservationTimeRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.detail.ReservationDetail;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.detail.ReservationTimeRepository;
import roomescape.domain.reservation.detail.Theme;
import roomescape.domain.reservation.detail.ThemeRepository;
import roomescape.fixture.Fixture;
import roomescape.presentation.BaseControllerTest;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class AdminReservationTimeControllerTest extends BaseControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Nested
    @DisplayName("예약 시간을 생성하는 경우")
    class AddReservationTime extends BaseControllerTest {
        @Test
        @DisplayName("성공할 경우 201을 반환한다.")
        void success() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(10, 30));

            RestAssured.given(spec).log().all()
                    .accept("application/json")
                    .filters(document("reservation-time/cookies",
                                    requestCookies(cookieWithName("token").description("세션에 저장된 쿠기 값입니다.")))
                            , document("reservation-time/create-time",
                                    requestFields(fieldWithPath("startAt").description("예약할 시간입니다.")),
                                    responseFields(RESERVATION_TIME_DESCRIPTOR)))
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/admin/times")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.CREATED.value())
                    .header("Location", response1 -> equalTo("/times/" + response1.path("id")))
                    .body("startAt", equalTo("10:30"));
        }


        @Test
        @DisplayName("이미 예약 시간이 존재하면 400을 반환한다.")
        void addReservationTimeFailWhenDuplicatedTime() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 30)));

            ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(10, 30));

            RestAssured.given(spec).log().all()
                    .accept("application/json")
                    .filters(document("reservation-time/create-time/exist-exception",
                            requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키입니다.")),
                            requestFields(fieldWithPath("startAt").description("예약할 시간입니다.")),
                            responseFields(ERROR_MESSAGE_DESCRIPTOR)))
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/admin/times")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("해당 시간의 예약 시간이 이미 존재합니다."));
        }
    }


    @Nested
    @DisplayName("예약 시간을 삭제하는 경우")
    class DeleteReservationTimeById {

        @Test
        @DisplayName("성공할 경우 204를 반환한다.")
        void success() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 30)));

            RestAssured.given(spec).log().all()
                    .filters(document("reservation-time/delete-time",
                            requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다."))))
                    .cookie("token", token)
                    .when().delete("/admin/times/{id}", 1)
                    .then().log().all()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        @DisplayName("존재하지 않는 예약 시간을 삭제하면 404를 반환한다.")
        void deleteReservationTimeByIdFailWhenNotFoundId() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            RestAssured.given(spec).log().all()
                    .filters(document("reservation-time/delete-time/exception/not-exist",
                            requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                            responseFields(ERROR_MESSAGE_DESCRIPTOR)))
                    .cookie("token", token)
                    .when().delete("/admin/times/1")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("message", containsString("해당 id의 예약 시간이 존재하지 않습니다."));
        }

        @Test
        @DisplayName("이미 사용 중인 예약 시간을 삭제하면 400을 반환한다.")
        void deleteReservationTimeByIdFailWhenUsedTime() {
            Member admin = memberRepository.save(Fixture.MEMBER_ADMIN);
            String token = tokenProvider.createToken(admin.getId().toString());

            Member member = memberRepository.save(new Member("member@gmail.com", "password", "member", Role.USER));
            ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 30)));
            Theme theme = themeRepository.save(new Theme("테마 이름", "테마 설명", "https://example.com"));
            LocalDate date = LocalDate.of(2024, 4, 9);
            ReservationDetail detail = new ReservationDetail(date, reservationTime, theme);
            reservationRepository.save(new Reservation(detail, member));

            RestAssured.given(spec).log().all()
                    .filters(document("reservation-time/delete-time/exception/already-exist",
                            requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                            responseFields(ERROR_MESSAGE_DESCRIPTOR)))
                    .cookie("token", token)
                    .when().delete("/admin/times/1")
                    .then().log().all()
                    .assertThat()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("message", containsString("해당 예약 시간을 사용하는 예약이 존재합니다."));
        }
    }
}
