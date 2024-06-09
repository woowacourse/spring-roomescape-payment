package roomescape.controller.api;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.controller.BaseControllerTest;
import roomescape.controller.dto.request.ReservationWaitingRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.ReservationFixture;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

class ReservationWaitingControllerTest extends BaseControllerTest {

    private static final String RESERVATION_DATE = "2024-05-24";

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        ReservationTime time = reservationTimeRepository.save(ReservationTimeFixture.ten());
        Theme theme = themeRepository.save(ThemeFixture.theme());
        Member member = memberRepository.save(MemberFixture.user());
        reservationRepository.save(ReservationFixture.create(RESERVATION_DATE, member, time, theme));
        userLogin();
    }

    @Test
    @DisplayName("예약 대기를 생성한다.")
    void addReservationWaiting() {
        ReservationWaitingRequest request = new ReservationWaitingRequest(LocalDate.parse(RESERVATION_DATE), 1L, 1L);

        RestAssured.given(spec).log().all()
                .contentType("application/json")
                .cookie("token", token)
                .body(request)
                .filter(document("waiting/addReservationWaiting",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 대기할 날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 대기할 시간 아이디"),
                                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("예약 대기할 테마 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("예약자 이름"),
                                fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시작 시간"),
                                fieldWithPath("theme").type(JsonFieldType.STRING).description("예약 테마 이름")
                        )))
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);
    }
}
