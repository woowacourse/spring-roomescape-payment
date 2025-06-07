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
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;


class ThemeControllerTest {

    @Nested
    @DisplayName("테마를 추가한다.")
    class AddTheme extends RestDocsTestBase {

        @Test
        @DisplayName("id를 포함한 방 테마와 CREATED를 응답한다")
        void addTheme() {
            Map<String, String> reservationBody = Map.of(
                    "name", "공포 테마",
                    "description", "공포 테마 입니다",
                    "thumbnail", "url"
            );

            String token = getAdminToken();

            RestAssured.given(spec).filter(addTheme_document()).log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", token)
                    .body(reservationBody)
                    .when().post("/admin/themes")
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", Matchers.equalTo(4))
                    .body("name", Matchers.equalTo("공포 테마"));
        }

        RestDocumentationFilter addTheme_document() {

            FieldDescriptor[] requestFields = {
                    fieldWithPath("name").description("테마 이름"),
                    fieldWithPath("description").description("테마 설명"),
                    fieldWithPath("thumbnail").description("대표 이미지 URL 경로")
            };

            FieldDescriptor[] responseFields = {
                    fieldWithPath("id").description("테마 ID"),
                    fieldWithPath("name").description("테마 이름"),
                    fieldWithPath("description").description("테마 설명"),
                    fieldWithPath("thumbnail").description("대표 이미지 URL 경로")
            };

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            return document(
                    "theme-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    requestCookies(requestCookies),
                    responseFields(responseFields)
            );
        }
    }

    @Nested
    @DisplayName("모든 테마를 조회한다.")
    class FindAllTheme extends RestDocsTestBase {

        @Test
        @DisplayName("존재하는 모든 방 테마와 OK를 응답한다")
        void findAllTheme() {
            RestAssured.given(spec).filter(findAllTheme_document()).log().all()
                    .when().get("/themes")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", Matchers.is(3));
        }

        RestDocumentationFilter findAllTheme_document() {

            FieldDescriptor[] responseFields = {
                    fieldWithPath("[]").description("조회된 테마 목록"),
                    fieldWithPath("[].id").description("테마 ID"),
                    fieldWithPath("[].name").description("테마 이름"),
                    fieldWithPath("[].description").description("테마 설명"),
                    fieldWithPath("[].thumbnail").description("대표 이미지 URL 경로")
            };

            return document(
                    "theme-find-all",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(responseFields)
            );
        }
    }

    @Nested
    @DisplayName("테마를 삭제한다.")
    class RemoveTheme extends RestDocsTestBase {

        @Test
        @DisplayName("주어진 아이디에 해당하는 방 테마가 없다면 NOT FOUND를 응답한다.")
        void removeTheme_WhenThemeDoesNotExisted() {
            String token = getAdminToken();
            Long removeId = 1000L;

            RestAssured.given(spec).filter(removeTheme_WhenThemeDoesNotExisted_Document()).log().all()
                    .cookie("token", token)
                    .when().delete("/admin/themes/{id}", removeId)
                    .then().log().all()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("주어진 아이디에 해당하는 방테마가 사용중이라면 CONFLICT를 응답한다.")
        void removeTheme_WhenThemeIsUsed() {
            String token = getAdminToken();
            Long removeId = 1L;

            RestAssured.given(spec).filter(removeTheme_WhenThemeIsUsed_Document()).log().all()
                    .cookie("token", token)
                    .when().delete("/admin/themes/{id}", removeId)
                    .then().log().all()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

        @Test
        @DisplayName("테마를 정상적으로 삭제한다.")
        void removeTheme() {
            var token = getAdminToken();
            Long removeId = 3L;

            RestAssured.given(spec).filter(removeTheme_Document()).log().all()
                    .cookie("token", token)
                    .when().delete("/admin/themes/{id}", removeId)
                    .then().log().all()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        RestDocumentationFilter removeTheme_WhenThemeDoesNotExisted_Document() {

            ParameterDescriptor[] pathParameters = {
                    parameterWithName("id").description("삭제할 테마 ID")
            };

            FieldDescriptor[] responseFields = getErrorFieldDescriptors();

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            return document(
                    "theme-remove-not-found",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(pathParameters),
                    requestCookies(requestCookies),
                    responseFields(responseFields)
            );
        }

        RestDocumentationFilter removeTheme_WhenThemeIsUsed_Document() {
            ParameterDescriptor[] pathParameters = {
                    parameterWithName("id").description("삭제할 테마 ID")
            };

            FieldDescriptor[] responseFields = getErrorFieldDescriptors();

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            return document(
                    "theme-remove-conflict",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(pathParameters),
                    requestCookies(requestCookies),
                    responseFields(responseFields)
            );
        }

        RestDocumentationFilter removeTheme_Document() {
            ParameterDescriptor[] pathParameters = {
                    parameterWithName("id").description("삭제할 테마 ID")
            };

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            return document(
                    "theme-remove",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestCookies(requestCookies),
                    pathParameters(pathParameters)
            );
        }
    }
}
