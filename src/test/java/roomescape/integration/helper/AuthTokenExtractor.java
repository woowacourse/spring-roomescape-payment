package roomescape.integration.helper;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;

public class AuthTokenExtractor {

    public static String extractAdminToken() {
        return extractToken(Map.of("email", "admin@email.com", "password", "password"), "/admin/login");
    }

    public static String extractMemberToken() {
        return extractToken(Map.of("email", "member1@email.com", "password", "password"), "/login");
    }

    private static String extractToken(Map<String, String> credentials, String loginUrl) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(credentials)
                .when().post(loginUrl)
                .then().statusCode(200)
                .extract();

        String token = response.cookie("token");
        if (token == null) {
            throw new IllegalStateException("토큰이 응답에 없습니다.");
        }
        return token;
    }
}
