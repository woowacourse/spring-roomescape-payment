package roomescape;

import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import roomescape.controller.dto.LoginRequest;
import roomescape.infrastructure.PaymentClient;

@Sql("/init.sql")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestSupport {

    protected static final String ADMIN_EMAIL = "admin@admin.com";
    protected static final String ADMIN_PASSWORD = "1234";
    protected static final String ADMIN_NAME = "어드민";
    protected static final String USER_EMAIL = "user1@user.com";
    protected static final String USER_PASSWORD = "1234";
    protected static String ADMIN_TOKEN;
    protected static String USER_TOKEN;

    @LocalServerPort
    private int serverPort;

    @MockBean
    protected PaymentClient paymentClient;

    @PostConstruct
    private void initialize() {
        RestAssured.port = serverPort;

        ADMIN_TOKEN = RestAssured
                .given().log().all()
                .body(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all().extract().cookie("token");

        USER_TOKEN = RestAssured
                .given().log().all()
                .body(new LoginRequest(USER_EMAIL, USER_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all().extract().cookie("token");
    }
}
