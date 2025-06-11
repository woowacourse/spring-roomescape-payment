package roomescape.presentation.rest;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

class TimeSlotControllerTest {

    @Nested
    @DisplayName("예약 시간을 추가한다.")
    class CreateTimeSlot extends RestDocsTestBase {

        @Test
        @DisplayName("id를 포함한 예약 시간과 CREATED를 응답한다")
        void createTimeSlot() {
            Map<String, String> requestBody = Map.of(
                    "startAt", "13:00"
            );

            var token = getAdminToken();

            RestAssured.given(spec).filter(createTimeSlot_Document()).log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", token)
                    .body(requestBody)
                    .when().post("/admin/times")
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", Matchers.equalTo(4))
                    .body("startAt", Matchers.equalTo("13:00:00"));
        }

        RestDocumentationFilter createTimeSlot_Document() {
            FieldDescriptor[] requestFields = {
                    fieldWithPath("startAt").description("방탈출 시작 시각 (HH:mm:ss)")
            };

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            FieldDescriptor[] responseFields = {
                    fieldWithPath("id").description("예약 시간 ID"),
                    fieldWithPath("startAt").description("방탈출 시작 시각 (HH:mm:ss)")
            };

            return document(
                    "time-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestCookies(requestCookies),
                    requestFields(requestFields),
                    responseFields(responseFields)
            );
        }
    }

    @Nested
    @DisplayName("모든 예약 시간을 조회한다.")
    class ReadAllTimeSlots extends RestDocsTestBase {

        @Test
        @DisplayName("존재하는 모든 예약 시간과 OK를 응답한다")
        void findAllReservationTime() {
            RestAssured.given(spec).filter(readAllTimeSlots_Document()).log().all()
                    .when().get("/times")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", Matchers.is(3));
        }

        RestDocumentationFilter readAllTimeSlots_Document() {
            FieldDescriptor[] responseFields = {
                    fieldWithPath("[]").description("조회된 시간 목록"),
                    fieldWithPath("[].id").description("예약 시간 ID"),
                    fieldWithPath("[].startAt").description("방탈출 시작 시각 (HH:mm:ss)")
            };

            return document(
                    "time-find-all",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(responseFields)
            );
        }
    }

    @Nested
    @DisplayName("예약 시간을 삭제한다.")
    class DeleteTimeSlot extends RestDocsTestBase {

        @Test
        @DisplayName("주어진 아이디에 해당하는 예약 시간이 없다면 NOT FOUND를 응답한다.")
        void deleteTimeSlot_WhenReservationTimeDoesNotExisted() {
            String token = getAdminToken();

            Long removeId = 1000L;

            RestAssured.given(spec).filter(deleteTimeSlot_WhenReservationTimeDoesNotExisted_Document()).log().all()
                    .cookie("token", token)
                    .when().delete("/admin/times/{id}", removeId)
                    .then().log().all()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("주어진 아이디에 해당하는 예약 시간이 사용 중이라면 CONFLICT를 응답한다.")
        void deleteTimeSlot_WhenReservationTimeInUsed() {
            String token = getAdminToken();

            Long removeId = 1L;

            RestAssured.given(spec).filter(deleteTimeSlot_WhenReservationTimeInUsed_Document()).log().all()
                    .cookie("token", token)
                    .when().delete("/admin/times/{id}", removeId)
                    .then().log().all()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

        @Test
        @DisplayName("예약 시간을 정상적으로 삭제한다.")
        void deleteTimeSlot() {
            var token = getAdminToken();

            Long removeId = 3L;

            RestAssured.given(spec).filter(deleteTimeSlot_Document()).log().all()
                    .cookie("token", token)
                    .when().delete("/admin/times/{id}", removeId)
                    .then().log().all()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        RestDocumentationFilter deleteTimeSlot_WhenReservationTimeDoesNotExisted_Document() {
            FieldDescriptor[] responseFields = getErrorFieldDescriptors();

            ParameterDescriptor[] pathParameters = {
                    parameterWithName("id").description("삭제할 시간 ID")
            };

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            return document(
                    "time-remove-not-found",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestCookies(requestCookies),
                    pathParameters(pathParameters),
                    responseFields(responseFields)
            );
        }

        RestDocumentationFilter deleteTimeSlot_WhenReservationTimeInUsed_Document() {
            FieldDescriptor[] responseFields = getErrorFieldDescriptors();

            ParameterDescriptor[] pathParameters = {
                    parameterWithName("id").description("삭제할 시간 ID")
            };

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            return document(
                    "time-remove-conflict",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestCookies(requestCookies),
                    pathParameters(pathParameters),
                    responseFields(responseFields)
            );
        }

        RestDocumentationFilter deleteTimeSlot_Document() {

            ParameterDescriptor[] pathParameters = {
                    parameterWithName("id").description("삭제할 시간 ID")
            };

            CookieDescriptor[] requestCookies = {
                    cookieWithName("token").description("사용자 인증 토큰")
            };

            return document(
                    "time-remove",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestCookies(requestCookies),
                    pathParameters(pathParameters)
            );
        }
    }


}
