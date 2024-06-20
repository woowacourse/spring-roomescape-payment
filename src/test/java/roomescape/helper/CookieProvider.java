package roomescape.helper;

import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import roomescape.service.login.dto.LoginRequest;

@Component
public class CookieProvider {
    public Cookies createUserCookies() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LoginRequest("user@gmail.com", "1234567890"))
                .when().post("/login")
                .then().log().all()
                .extract().detailedCookies();
    }

    public Cookies createUserCookies(String email) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LoginRequest(email, "1234567890"))
                .when().post("/login")
                .then().log().all()
                .extract().detailedCookies();
    }

    public Cookies createAdminCookies() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LoginRequest("admin@gmail.com", "1234567890"))
                .when().post("/login")
                .then().log().all()
                .extract().detailedCookies();
    }
}
