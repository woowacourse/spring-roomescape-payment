package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import roomescape.core.dto.waiting.MemberWaitingRequest;
import roomescape.utils.AccessTokenGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.TestFixture;

@AcceptanceTest
class WaitingControllerTest {
    private static final String TOMORROW = TestFixture.getTomorrowDate();

    private String accessToken;

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestFixture testFixture;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
                .build();

        databaseCleaner.executeTruncate();
        testFixture.initTestData();

        accessToken = AccessTokenGenerator.adminTokenGenerate();
    }

    @Test
    @DisplayName("예약 대기를 생성할 수 있다.")
    void createWaiting() {
        MemberWaitingRequest request = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .accept("application/json")
                .filter(document("waitings/post/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("예약 대기를 생성할 멤버의 토큰")),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 대기 시간 id"),
                                fieldWithPath("themeId").description("예약 대기 테마 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 id"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("member.*").description("예약 대기자 정보"),
                                fieldWithPath("time.*").description("예약 대기 시간"),
                                fieldWithPath("theme.*").description("예약 대기 테마")
                        )))
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("전체 예약 대기 목록을 조회할 수 있다.")
    void findAllWaitings() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(document("/waitings/get/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("예약 id"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].member.*").description("예약자 정보"),
                                fieldWithPath("[].time.*").description("예약 시간 정보"),
                                fieldWithPath("[].theme.*").description("예약 테마 정보")
                        )))
                .when().get("/waitings")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    @DisplayName("예약 대기를 취소할 수 있다.")
    void deleteWaiting() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(document("waitings/delete/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("예약 대기를 삭제할 멤버의 토큰")),
                        pathParameters(parameterWithName("id").description("삭제할 대기 시간의 id"))))
                .when().delete("/waitings/{id}", 1)
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("이미 예약 대기한 내역이 존재하면 예약 대기를 생성할 수 없다.")
    void createDuplicateWaitingBySameMember() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("내 예약 대기가 아닌 다른 회원의 예약 대기를 삭제하면 예외가 발생한다.")
    void deleteWaitingByOtherMember() {
        MemberWaitingRequest waitingRequest = new MemberWaitingRequest(TOMORROW, 1L, 1L);

        RestAssured.given().log().all()
                .cookies("token", accessToken)
                .contentType(ContentType.JSON)
                .body(waitingRequest)
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookies("token", AccessTokenGenerator.memberTokenGenerate())
                .when().delete("/waitings/1")
                .then().log().all()
                .statusCode(400);
    }
}
