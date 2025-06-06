package roomescape.presentation.rest;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WaitingControllerTest {

    @Nested
    @DisplayName("예약 대기를 추가한다.")
    class CreateWaiting extends RestDocsTestBase {

        @Test
        @DisplayName("id를 포함한 예약 대기 내용과 CREATED를 응답한다")
        void createWaiting() {
            Map<String, String> requestBody = Map.of(
                    "date", "3000-03-17",
                    "timeId", "1",
                    "themeId", "1"
            );

            String token = getUserToken();

            RestAssured.given(spec).filter(createWaiting_Document()).log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", token)
                    .body(requestBody)
                    .when().post("/waitings")
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("date", Matchers.equalTo("3000-03-17"));

        }

        RestDocumentationFilter createWaiting_Document() {

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            FieldDescriptor[] requestFields = {
                    fieldWithPath("date").description("예약하려는 날짜"),
                    fieldWithPath("timeId").description("예약하려는 시간의 ID"),
                    fieldWithPath("themeId").description("예약하려는 테마의 ID")
            };

            FieldDescriptor[] responseFields = {
                    fieldWithPath("id").description("예약 ID"),
                    fieldWithPath("user").description("예약 사용자 정보"),
                    fieldWithPath("user.id").description("사용자 ID"),
                    fieldWithPath("user.name").description("사용자 이름"),
                    fieldWithPath("date").description("예약 날짜"),
                    fieldWithPath("time").description("시간 정보"),
                    fieldWithPath("time.id").description("예약 시간 ID"),
                    fieldWithPath("time.startAt").description("방탈출 예약 시간"),
                    fieldWithPath("theme").description("테마 정보"),
                    fieldWithPath("theme.id").description("테마 정보"),
                    fieldWithPath("theme.name").description("테마 정보"),
                    fieldWithPath("theme.description").description("테마 정보"),
                    fieldWithPath("theme.thumbnail").description("테마 정보")
            };

            return document(
                    "waiting-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    requestCookies(requestCookies),
                    responseFields(responseFields)
            );
        }
    }

    @Nested
    @DisplayName("예약 대기를 삭제한다.")
    class DeleteWaiting extends RestDocsTestBase {

        @Test
        @DisplayName("주어진 아이디에 해당하는 예약 대기가 있다면 삭제하고 NO CONTENT를 응답한다.")
        void deleteWaiting() {
            Long removeId = 4L;

            String token = getUserToken();

            RestAssured.given(spec).filter(deleteWaiting_Document()).log().all()
                    .cookie("token", token)
                    .when().delete("/waitings/{id}", removeId)
                    .then().log().all()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        RestDocumentationFilter deleteWaiting_Document() {

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            ParameterDescriptor[] pathParameters = {
                    parameterWithName("id").description("삭제하려는 예약 대기 ID")
            };

            return document(
                    "waiting-remove",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestCookies(requestCookies),
                    pathParameters(pathParameters)
            );
        }
    }
}
