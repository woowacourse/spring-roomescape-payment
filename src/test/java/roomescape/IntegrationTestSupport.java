package roomescape;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import roomescape.controller.member.dto.MemberLoginRequest;
import roomescape.payment.PaymentService;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.TestFixture.ADMIN_EMAIL;
import static roomescape.TestFixture.ADMIN_PASSWORD;
import static roomescape.TestFixture.USER_EMAIL;
import static roomescape.TestFixture.USER_PASSWORD;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriHost = "redddy.com", uriPort = 80)
@ActiveProfiles("test")
public abstract class IntegrationTestSupport {

    protected static String ADMIN_TOKEN;
    protected static String USER_TOKEN;

    protected RequestSpecification specification;

    @SpyBean
    protected PaymentService paymentService;

    @LocalServerPort
    int serverPost;

    @PostConstruct
    private void initialize() {
        RestAssured.port = serverPost;

        ADMIN_TOKEN = RestAssured
                .given().log().all()
                .body(new MemberLoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all().extract().cookie("token");

        USER_TOKEN = RestAssured
                .given().log().all()
                .body(new MemberLoginRequest(USER_EMAIL, USER_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all().extract().cookie("token");
    }

    @BeforeEach
    protected void initTest(RestDocumentationContextProvider restDocumentation) {
        final Filter documentationConfiguration = documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());

        this.specification = new RequestSpecBuilder()
                .addFilter(documentationConfiguration)
                .build();
    }
}
