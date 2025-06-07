package roomescape.api.theme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import roomescape.api.fixture.DocumentationFixture;
import roomescape.theme.dto.CreateThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/test-theme-data.sql")
@ExtendWith(RestDocumentationExtension.class)
public class ThemeApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ThemeRepository themeRepository;

    private RequestSpecification documentationSpecification;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.documentationSpecification = DocumentationFixture
                .createDefaultDocumentationSpecification(restDocumentation);
    }

    @DisplayName("POST /themes : 테마 추가 API 테스트")
    @Test
    void create() {
        // given
        CreateThemeRequest request = new CreateThemeRequest("레벨2 탈출", "우테코를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");
        ThemeResponse expectedResponse = new ThemeResponse(4L, "레벨2 탈출", "우테코를 탈출하는 내용입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");
        // when
        ThemeResponse actualResponse = RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().post("/themes")
                .then().log().all()
                .statusCode(201)
                .extract().as(ThemeResponse.class);
        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("GET /themes : 테마 목록 조회 API 테스트")
    @Test
    void findAll() {
        // given
        ThemeResponse expectedResponse = new ThemeResponse(1L, "테마1", "테마1입니다.",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");
        // when
        ThemeResponse[] actualResponse = RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .extract().as(ThemeResponse[].class);
        // then
        assertAll(
                () -> assertThat(actualResponse[0]).isEqualTo(expectedResponse),
                () -> assertThat(actualResponse).hasSize(3)
        );
    }

    @DisplayName("GET /popular-themes : 인기 테마 목록 조회 API")
    @Test
    @Sql({"/test-time-data.sql", "/test-member-data.sql", "/test-popular-theme-data.sql"})
    void findPopularThemes() {
        // given
        // when
        ThemeResponse[] actualResponse = RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().get("/popular-themes")
                .then().log().all()
                .statusCode(200)
                .extract().as(ThemeResponse[].class);
        // then
        assertAll(
                () -> assertThat(actualResponse[0].id()).isEqualTo(2L),
                () -> assertThat(actualResponse[1].id()).isEqualTo(1L),
                () -> assertThat(actualResponse[2].id()).isEqualTo(3L),
                () -> assertThat(actualResponse).hasSize(3)
        );
    }

    @DisplayName("DELETE /themes/{id} : 테마 삭제 API 테스트")
    @Test
    void delete() {
        // given
        long themeIdPathParameter = 1L;
        // when
        RestAssured.given(documentationSpecification).log().all()
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().delete("/themes/{id}", themeIdPathParameter)
                .then().log().all()
                .statusCode(204);
        // then
        assertThat(themeRepository.count()).isEqualTo(2);
    }
}
