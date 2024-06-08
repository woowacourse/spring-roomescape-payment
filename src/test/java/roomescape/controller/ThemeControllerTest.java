package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.request.ThemeRequest;
import roomescape.model.Theme;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

class ThemeControllerTest extends AbstractControllerTest {

    @DisplayName("전체 테마를 조회한다.")
    @Test
    void should_get_themes() {
        RestDocumentationFilter description = document("themes-success-get",
                responseFields(
                        fieldWithPath("[].id").description("테마 Id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].description").description("테마 설명").type(JsonFieldType.STRING),
                        fieldWithPath("[].thumbnail").description("테마 썸네일 URL").type(JsonFieldType.STRING)
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .when().get("/themes")
                .then().log().all()
                .statusCode(200).extract()
                .jsonPath().getList(".", Theme.class);
    }

    @DisplayName("테마를 추가한다.")
    @Test
    void should_add_theme() {
        ThemeRequest request = new ThemeRequest("에버", "공포", "공포.jpg");
        RestDocumentationFilter description = document("themes-success-post",
                requestFields(
                        fieldWithPath("name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("description").description("테마 설명").type(JsonFieldType.STRING),
                        fieldWithPath("thumbnail").description("테마 썸네일 URL").type(JsonFieldType.STRING)
                ),
                responseFields(
                        fieldWithPath("id").description("테마 Id").type(JsonFieldType.NUMBER),
                        fieldWithPath("name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("description").description("테마 설명").type(JsonFieldType.STRING),
                        fieldWithPath("thumbnail").description("테마 썸네일 URL").type(JsonFieldType.STRING)
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/themes/12");
    }

    @DisplayName("테마를 삭제한다")
    @Test
    void should_remove_theme() {
        RestDocumentationFilter description = document("themes-success-delete",
                pathParameters(
                        parameterWithName("id").description("삭제할 테마 id")
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .when().delete("/themes/{id}", 11)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("인기 테마를 조회한다.")
    @Test
    @Sql("/theme_data.sql")
    void should_find_popular_theme() {
        RestDocumentationFilter description = document("themes-top10-success-get",
                responseFields(
                        fieldWithPath("[].id").description("테마 Id").type(JsonFieldType.NUMBER),
                        fieldWithPath("[].name").description("테마 이름").type(JsonFieldType.STRING),
                        fieldWithPath("[].description").description("테마 설명").type(JsonFieldType.STRING),
                        fieldWithPath("[].thumbnail").description("테마 썸네일 URL").type(JsonFieldType.STRING)
                )
        );
        RestAssured.given(spec).log().all()
                .filter(description)
                .when().get("/themes/top10")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath().getList(".", Theme.class);
    }
}
