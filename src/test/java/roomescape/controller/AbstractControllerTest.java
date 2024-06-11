package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.cookies.RequestCookiesSnippet;
import org.springframework.test.context.jdbc.Sql;
import roomescape.BaseTest;
import roomescape.request.MemberLoginRequest;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

@Sql({"/initialize_table.sql", "/controller_test_data.sql"})
@ExtendWith(RestDocumentationExtension.class)
public abstract class AbstractControllerTest extends BaseTest {
    protected RequestSpecification spec;
    protected RequestCookiesSnippet requiredCookie = requestCookies(cookieWithName("token").description("POST /login 를 통해 획득한 jwt 값"));


    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    protected String getAuthenticationCookie(String email, String password) {
        MemberLoginRequest request = new MemberLoginRequest(password, email);
        final String cookieValue = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().statusCode(200)
                .extract().header("Set-Cookie");
        return parseTokenValue(cookieValue);
    }

    private String parseTokenValue(String cookieValue) {
        final String[] split = cookieValue.split("=", 2);
        return split[1];
    }

    protected String getAdminCookie() {
        return this.getAuthenticationCookie("pobi@email.com", "2222");
    }

    protected String getMemberCookie() {
        return this.getAuthenticationCookie("sun@email.com", "1234");
    }
}
