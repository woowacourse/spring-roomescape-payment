package roomescape.fixture;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.test.context.jdbc.Sql;
import roomescape.service.auth.dto.LoginRequest;

@Sql({"/truncate.sql", "/member.sql"})
public class TokenFixture {

    public static String getAdminToken() {
        return RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("lini123", "lini@email.com"))
            .when().post("/login")
            .then().log().all().extract().cookie("token");
    }

    public static String getGuestLilyToken() {
        return RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("lily123", "lily@email.com"))
            .when().post("/login")
            .then().log().all().extract().cookie("token");
    }

    public static String getGuestTomiToken() {
        return RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("tomi123", "tomi@email.com"))
            .when().post("/login")
            .then().log().all().extract().cookie("token");
    }
}
