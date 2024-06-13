package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.common.util.ApiDocumentUtils.getDocumentRequest;
import static roomescape.common.util.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.common.config.ControllerTest;
import roomescape.common.util.CookieUtils;
import roomescape.reservation.controller.dto.request.ThemeSaveRequest;
import roomescape.reservation.domain.Description;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.ThemeName;

class ThemeControllerTest extends ControllerTest {

    private static final String ROOT_IDENTIFIER = "theme";

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("인기 테마 목록 조회를 성공하면 200 응답을 받는다.")
    @Test
    void findTopTenThemesOfLastWeek() {
        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/themes/popular")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("테마 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findAll() {
        Theme horror = new Theme(new ThemeName("공포"), new Description("무서워요"),
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");
        Theme sf = new Theme(new ThemeName("SF"), new Description("미래"),
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");
        themeJdbcUtil.saveTheme(horror);
        themeJdbcUtil.saveTheme(sf);

        RestAssured.given(spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document(ROOT_IDENTIFIER + "/find-all",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestCookies(cookieWithName("token").description("로그인 유저 토큰")),
                        responseFields(
                                fieldWithPath("resources[].id").type(JsonFieldType.NUMBER).description("식별자"),
                                fieldWithPath("resources[].name").type(JsonFieldType.STRING).description("테마 명"),
                                fieldWithPath("resources[].description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("resources[].thumbnail").type(JsonFieldType.STRING).description("테마 이미지 url")
                        )
                ))
                .when()
                .get("/themes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("resources.$", hasSize(2));
    }

    @DisplayName("테마를 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void save() throws JsonProcessingException {
        ThemeSaveRequest themeSaveRequest = new ThemeSaveRequest("공포", "진짜 무서움", "https://i.pinimg.com/236x.jpg");

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(themeSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/themes")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/themes/1");
    }

    @DisplayName("테마를 성공적으로 제거하면 204 응답을 받는다.")
    @Test
    void delete() {
        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .delete("/themes/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
