package roomescape.reservation.presentation;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import roomescape.DatabaseCleaner;
import roomescape.TestConfig;
import roomescape.member.presentation.fixture.MemberFixture;
import roomescape.reservation.presentation.dto.WaitingRequest;
import roomescape.reservation.presentation.fixture.ReservationFixture;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
public class WaitingControllerTest {

    private final DatabaseCleaner databaseCleaner;
    private final ReservationFixture reservationFixture = new ReservationFixture();
    private final MemberFixture memberFixture = new MemberFixture();

    private RequestSpecification spec;

    @LocalServerPort
    int port;

    @Autowired
    WaitingControllerTest(final DatabaseCleaner databaseCleaner) {
        this.databaseCleaner = databaseCleaner;
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        databaseCleaner.clear();
        databaseCleaner.setUserInfo();

        this.spec = new RequestSpecBuilder()
                .setPort(port)
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("예약 대기 추가 테스트")
    void createWaitingTest() {
        // given
        final Map<String, String> cookies = memberFixture.loginAdmin();
        reservationFixture.createReservationTime(LocalTime.of(10, 30), cookies);

        reservationFixture.createTheme(
                "레벨2 탈출",
                "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg",
                cookies
        );

        final WaitingRequest waitingRequest = reservationFixture.createWaitingRequest(LocalDate.of(2025, 8, 5), 1L,
                1L);

        // when - then
        RestAssured.given(this.spec).log().all()
                .filter(document("{class-name}/{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("date")
                                        .description("예약 날짜 (형식: yyyy-MM-dd)"),
                                fieldWithPath("themeId")
                                        .description("테마 식별자"),
                                fieldWithPath("timeId")
                                        .description("예약 시간 식별자")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("식별자"),
                                fieldWithPath("date")
                                        .description("예약 날짜 (형식: yyyy-MM-dd)"),
                                fieldWithPath("member.id")
                                        .description("멤버 식별자"),
                                fieldWithPath("member.name")
                                        .description("사용자 이름"),
                                fieldWithPath("theme.id")
                                        .description("테마 식별자"),
                                fieldWithPath("theme.name")
                                        .description("테마 이름"),
                                fieldWithPath("theme.description")
                                        .description("테마 시나리오 설명"),
                                fieldWithPath("theme.thumbnail")
                                        .description("썸네일 이미지 주소"),
                                fieldWithPath("time.id")
                                        .description("예약 시간 식별자"),
                                fieldWithPath("time.startAt")
                                        .description("예약 시간 (형식: HH:mm:ss)")
                        )))
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .body(waitingRequest)
                .when().post("/reservations/waiting")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("예약 대기 삭제 테스트")
    void deleteWaitingTest() {
        // given
        final Map<String, String> cookies = memberFixture.loginAdmin();
        reservationFixture.createReservationTime(LocalTime.of(10, 30), cookies);

        reservationFixture.createTheme(
                "레벨2 탈출",
                "우테코 레벨2를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg",
                cookies
        );

        reservationFixture.createWaiting(LocalDate.of(2025, 8, 5), 1L, 1L, cookies);

        // when
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .when().delete("/reservations/waiting/1")
                .then().log().all()
                .statusCode(204);

        // then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(cookies)
                .when().get("/reservations/waiting")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }
}
