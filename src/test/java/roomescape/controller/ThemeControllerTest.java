package roomescape.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static roomescape.fixture.ThemeFixture.SPACE_THEME;
import static roomescape.fixture.ThemeFixture.SPOOKY_THEME;
import static roomescape.fixture.ThemeFixture.TEST_THEME;

import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import roomescape.application.reservation.dto.request.ThemeRequest;
import roomescape.application.reservation.dto.response.ThemeResponse;

class ThemeControllerTest extends ControllerTest {

    @Test
    @DisplayName("관리자가 테마를 생성한다.")
    void createThemeTest() {
        ThemeRequest request = TEST_THEME.request();
        BDDMockito.given(themeService.create(any(ThemeRequest.class)))
                .willReturn(TEST_THEME.responseWithId(1L));

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("name").description("테마명"),
                fieldWithPath("description").description("테마 설명"),
                fieldWithPath("thumbnail").description("이미지 URL"),
                fieldWithPath("price").description("가격")
        };

        RestDocumentationResultHandler handler = document(
                "theme-create",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "admin-token")
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .apply(handler)
                .statusCode(201);
    }

    @Test
    @DisplayName("테마 목록을 조회한다.")
    void findAllThemesTest() {
        List<ThemeResponse> responses = List.of(
                SPOOKY_THEME.responseWithId(1L),
                SPACE_THEME.responseWithId(2L)
        );
        BDDMockito.given(themeService.findAll())
                .willReturn(responses);

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].id").description("ID"),
                fieldWithPath("[].name").description("테마명"),
                fieldWithPath("[].description").description("테마 설명"),
                fieldWithPath("[].thumbnail").description("이미지 URL"),
                fieldWithPath("[].price").description("가격")
        };

        RestDocumentationResultHandler handler = document(
                "theme-find-all",
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .accept(ContentType.JSON)
                .when().get("/themes")
                .then().log().all()
                .apply(handler)
                .statusCode(200);
    }

    @Test
    @DisplayName("관리자가 테마를 삭제한다.")
    void deleteThemeTest() {
        BDDMockito.doNothing()
                .when(themeService)
                .deleteById(1L);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        ParameterDescriptor[] pathParameterDescriptors = {
                parameterWithName("id").description("테마 ID")
        };

        RestDocumentationResultHandler handler = document(
                "theme-delete",
                requestCookies(cookieDescriptors),
                pathParameters(pathParameterDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", "admin-token")
                .pathParam("id", 1L)
                .when().delete("/themes/{id}")
                .then().log().all()
                .apply(handler)
                .statusCode(204);
    }
}
