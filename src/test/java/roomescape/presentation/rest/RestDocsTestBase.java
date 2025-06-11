package roomescape.presentation.rest;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class RestDocsTestBase {

    private static final FieldDescriptor[] ERROR_RESPONSE_FIELDS = {
            fieldWithPath("type").description("에러 유형"),
            fieldWithPath("title").description("에러 제목"),
            fieldWithPath("status").description("HTTP 상태 코드"),
            fieldWithPath("detail").description("에러 메시지"),
            fieldWithPath("message").description("에러 상세 메시지"),
            fieldWithPath("instance").description("에러 발생 URI")
    };

    @LocalServerPort
    protected int port;

    protected RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .setPort(port)
                .build();
    }

    protected String getAdminToken() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "admin@email.com", "password", "password"))
                .when().post("/login")
                .then().statusCode(200)
                .extract().response().getDetailedCookies().getValue("token");
    }

    protected String getUserToken() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "user1@email.com", "password", "password1"))
                .when().post("/login")
                .then().statusCode(200)
                .extract().response().getDetailedCookies().getValue("token");
    }

    protected FieldDescriptor[] getErrorFieldDescriptors() {
        return ERROR_RESPONSE_FIELDS;
    }
}

