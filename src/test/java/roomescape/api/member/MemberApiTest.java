package roomescape.api.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
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
import roomescape.api.fixture.DocumentationFixture;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.SignupRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/test-member-data.sql")
@ExtendWith(RestDocumentationExtension.class)
public class MemberApiTest {

    @LocalServerPort
    private int port;

    private RequestSpecification documentationSpecification;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.documentationSpecification = DocumentationFixture.createDefaultDocumentationSpecification(
                restDocumentation);
    }

    @DisplayName("POST /members : 회원 가입 API 테스트")
    @Test
    void signUp() {
        // given
        SignupRequest request = new SignupRequest("노랑", "norang@gmail.com", "1234");
        MemberResponse expectedResponse = new MemberResponse(4L, "norang@gmail.com", "노랑");
        // when
        MemberResponse actualResponse = RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .filter(MemberDocumentationFixture.SIGN_UP_DOCUMENT)
                .when().post("/members")
                .then().log().all()
                .statusCode(201)
                .extract().as(MemberResponse.class);
        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @DisplayName("GET /members : 회원 목록 조회 API 테스트")
    @Test
    void findAll() {
        // given
        MemberResponse expectedResponse = new MemberResponse(1L, "aaa@gmail.com", "사용자1");
        // when
        MemberResponse[] actualResponse = RestAssured.given(documentationSpecification).log().all()
                .filter(MemberDocumentationFixture.GET_MEMBERS_DOCUMENT)
                .when().get("/members")
                .then().log().all()
                .statusCode(200)
                .extract().as(MemberResponse[].class);
        // then
        assertAll(
                () -> assertThat(actualResponse).hasSize(3),
                () -> {
                    Assertions.assertNotNull(actualResponse);
                    assertThat(actualResponse[0]).isEqualTo(expectedResponse);
                }
        );
    }
}
