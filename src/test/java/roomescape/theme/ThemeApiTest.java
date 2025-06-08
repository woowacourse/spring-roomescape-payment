package roomescape.theme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import roomescape.theme.dto.ThemeResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(RestDocumentationExtension.class)
public class ThemeApiTest {

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation).operationPreprocessors()
                        .withRequestDefaults(modifyHeaders().remove("Foo"), prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @DisplayName("인기테마 조회를 성공할 경우 200을 반환한다.")
    @Test
    @Sql({"/test-time-data.sql", "/test-member-data.sql", "/test-popular-theme-data.sql"})
    void testFindPopularThemes() {
        ThemeResponse[] responses = RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .filter(document(
                        "get-popular-themes",
                        responseFields(
                                fieldWithPath("[].id").description("테마 ID"),
                                fieldWithPath("[].name").description("테마 이름"),
                                fieldWithPath("[].description").description("테마 설명"),
                                fieldWithPath("[].thumbnail").description("테마 썸네일")
                        )
                ))
                .when().get("/popular-themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", Matchers.is(3))
                .extract()
                .as(ThemeResponse[].class);
        assertAll(
                () -> assertThat(responses[0].id()).isEqualTo(2L),
                () -> assertThat(responses[1].id()).isEqualTo(1L),
                () -> assertThat(responses[2].id()).isEqualTo(3L)
        );
    }

    @DisplayName("테마 목록 조회를 성공할 경우 200을 반환한다.")
    @Test
    @Sql("/test-theme-data.sql")
    void testFindALl() {
        RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .filter(document(
                        "get-themes",
                        responseFields(
                                fieldWithPath("[].id").description("테마 ID"),
                                fieldWithPath("[].name").description("테마 이름"),
                                fieldWithPath("[].description").description("테마 설명"),
                                fieldWithPath("[].thumbnail").description("테마 썸네일")
                        )
                ))
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", Matchers.is(3));
    }

    @DisplayName("테마 생성에 성공할 경우 201을 반환한다.")
    @Test
    void testCreate() {
        String requestBody = """
                {
                    "name": "새로운 테마",
                    "description": "새로운 테마 설명",
                    "thumbnail": "https://example.com/thumbnail.jpg"
                }
                """;

        RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .filter(document(
                        "create-theme",
                        requestFields(
                                fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 썸네일")
                        ),
                        responseFields(
                                fieldWithPath("id").description("테마 ID"),
                                fieldWithPath("name").description("테마 이름"),
                                fieldWithPath("description").description("테마 설명"),
                                fieldWithPath("thumbnail").description("테마 썸네일")
                        )
                ))
                .when().post("/themes")
                .then().log().all()
                .statusCode(201)
                .body("name", Matchers.is("새로운 테마"))
                .body("description", Matchers.is("새로운 테마 설명"))
                .body("thumbnail", Matchers.is("https://example.com/thumbnail.jpg"));
    }

    @DisplayName("테마 삭제에 성공할 경우 204를 반환한다.")
    @Test
    @Sql("/test-theme-data.sql")
    void testDelete() {
        RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .filter(document("delete-theme"))
                .when().delete("/themes/1")
                .then().log().all()
                .statusCode(204);
    }
}
