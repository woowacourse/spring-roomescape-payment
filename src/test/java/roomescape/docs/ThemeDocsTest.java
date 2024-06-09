package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.application.reservation.dto.request.ThemeRequest;
import roomescape.application.reservation.dto.response.ThemeResponse;

class ThemeDocsTest extends RestDocsTest {
    @Test
    @DisplayName("관리자가 테마를 생성한다.")
    void postThemeSuccess() {
        ThemeResponse response = new ThemeResponse(1L, "테마명", "테마 설명", 10_000L, "url");

        doReturn(response)
                .when(themeService)
                .create(any());

        ThemeRequest request = new ThemeRequest("테마명", "테마 설명", "url", 10_000L);

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .apply(document("/themes/post/success"
                        , requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("테마명"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("테마에 대한 설명"),
                                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 url"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("테마 가격")
                        )
                        , responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("테마명"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("테마에 대한 설명"),
                                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 url"),
                                fieldWithPath("price").type(JsonFieldType.NUMBER).description("테마 가격")
                        )
                ));
    }

    @Test
    @DisplayName("관리자가 테마를 생성을 실패한다.")
    void postThemeFail() {
        doThrow(new IllegalArgumentException("연관된 예약이 존재하여 삭제할 수 없습니다."))
                .when(themeService)
                .create(any());

        ThemeRequest request = new ThemeRequest(null, "테마 설명", "url", 10_000L);

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .apply(document("/themes/post/fail"));
    }

    @Test
    @DisplayName("테마 목록을 조회한다.")
    void getAllSuccess() {
        List<ThemeResponse> responses = List.of(
                new ThemeResponse(1L, "테마명", "테마 설명", 10_000L, "url"),
                new ThemeResponse(2L, "테마명2", "테마 설명2", 20_000L, "url2")
        );

        doReturn(responses)
                .when(themeService)
                .findAll();

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().get("/themes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/themes/get/all/success"
                        , responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마명"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마에 대한 설명"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 url"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("테마 가격")
                        )
                ));
    }

    @Test
    @DisplayName("관리자가 테마를 삭제한다.")
    void deleteSuccess() {
        ThemeResponse response = new ThemeResponse(1L, "테마명", "테마 설명", 10_000L, "url");

        doNothing()
                .when(themeService)
                .deleteById(any(Long.class));

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .when().delete("/themes/" + response.id())
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .apply(document("/themes/delete/success"));
    }

    @Test
    @DisplayName("관리자가 테마를 삭제를 실패한다.")
    void deleteFail() {
        ThemeResponse response = new ThemeResponse(1L, "테마명", "테마 설명", 10_000L, "url");

        doThrow(new IllegalArgumentException("관련된 예약이 존재합니다."))
                .when(themeService)
                .deleteById(any(Long.class));

        restDocs
                .cookie(COOKIE_NAME, getAdminToken(1L, "admin"))
                .when().delete("/themes/" + response.id())
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .apply(document("/themes/delete/fail"));
    }

    @Test
    @DisplayName("인기 테마를 조회한다.")
    void getPopular() {
        List<ThemeResponse> responses = List.of(
                new ThemeResponse(1L, "테마명", "테마 설명", 10_000L, "url"),
                new ThemeResponse(2L, "테마명2", "테마 설명2", 20_000L, "url2"),
                new ThemeResponse(3L, "테마명3", "테마 설명3", 30_000L, "url3")
        );

        doReturn(responses)
                .when(themeService)
                .findPopularThemes();

        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/themes/get/popular/success"
                        , responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마명"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마에 대한 설명"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 url"),
                                fieldWithPath("[].price").type(JsonFieldType.NUMBER).description("테마 가격")
                        )));
    }

}
