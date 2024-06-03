package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.request.MemberLoginRequest;

@Sql({"/initialize_table.sql", "/controller_test_data.sql"})
public abstract class AbstractControllerTest {
    protected String getAuthenticationCookie(String email, String password) {
        MemberLoginRequest request = new MemberLoginRequest(password, email);
        return RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().statusCode(200)
                .extract().header("Set-Cookie");
    }

    protected String getAdminCookie() {
        MemberLoginRequest request = new MemberLoginRequest("2222", "pobi@email.com");
        return RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().statusCode(200)
                .extract().header("Set-Cookie");
    }

    protected String getMemberCookie() {
        MemberLoginRequest request = new MemberLoginRequest("1234", "sun@email.com");
        return RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().statusCode(200)
                .extract().header("Set-Cookie");
    }
}
