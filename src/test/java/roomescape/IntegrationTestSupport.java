package roomescape;

import io.restassured.RestAssured;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.controller.dto.TokenRequest;
import roomescape.service.reservation.pay.PaymentService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class IntegrationTestSupport {

    protected static final String ADMIN_EMAIL = "admin@admin.com";
    protected static final String ADMIN_PASSWORD = "1234";
    protected static final String ADMIN_NAME = "어드민";
    protected static final String USER_EMAIL = "user1@user.com";
    protected static final String USER2_EMAIL = "user2@user.com";
    protected static final String USER_PASSWORD = "1234";
    public static String ADMIN_TOKEN;
    public static String USER_TOKEN;
    public static String USER2_TOKEN;

    @MockBean
    protected PaymentService paymentService;
    @LocalServerPort
    private int serverPort;

    @PostConstruct
    private void initialize() {
        RestAssured.port = serverPort;

        ADMIN_TOKEN = RestAssured
                .given().log().all()
                .body(new TokenRequest(ADMIN_EMAIL, ADMIN_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all().extract().cookie("token");

        USER_TOKEN = RestAssured
                .given().log().all()
                .body(new TokenRequest(USER_EMAIL, USER_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all().extract().cookie("token");

        USER2_TOKEN = RestAssured
                .given().log().all()
                .body(new TokenRequest(USER2_EMAIL, USER_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().all().extract().cookie("token");
    }
}
