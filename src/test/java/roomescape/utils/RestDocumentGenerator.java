package roomescape.utils;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class RestDocumentGenerator {
    public static RestDocumentationFilter prettyPrintDocument(String identifier) {
        return document(identifier,
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));
    }

    public static RestDocumentationFilter documentWithTokenDescription(String identifier,
                                                                       String tokenDescription) {
        return document(identifier,
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                requestCookies(cookieWithName("token").description(tokenDescription)));
    }

    public static RestDocumentationFilter deleteDocumentWithTokenAndIdDescription(String identifier,
                                                                                  String tokenDescription,
                                                                                  String idDescription) {
        return document(identifier,
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                requestCookies(cookieWithName("token").description(tokenDescription)),
                pathParameters(parameterWithName("id").description(idDescription)));
    }

    public static FieldDescriptor[] reservationFieldDescriptors() {
        return new FieldDescriptor[]{
                fieldWithPath("id").description("예약 id"),
                fieldWithPath("date").description("예약 날짜"),
                subsectionWithPath("member").description("예약자 정보"),
                subsectionWithPath("time").description("예약 시간 정보"),
                subsectionWithPath("theme").description("예약 테마 정보")
        };
    }

    public static FieldDescriptor[] waitingFieldDescriptors() {
        return new FieldDescriptor[]{
                fieldWithPath("id").description("예약 대기 id"),
                fieldWithPath("date").description("예약 대기 날짜"),
                subsectionWithPath("member").description("예약 대기자 정보"),
                subsectionWithPath("time").description("예약 대기 시간 정보"),
                subsectionWithPath("theme").description("예약 대기 테마 정보")
        };
    }

    public static FieldDescriptor[] reservationTimeFieldDescriptors() {
        return new FieldDescriptor[]{
                fieldWithPath("id").description("시간 id"),
                fieldWithPath("startAt").description("방탈출 시작 시간")
        };
    }

    public static FieldDescriptor[] themeFieldDescriptors() {
        return new FieldDescriptor[]{
                fieldWithPath("id").description("테마 id"),
                fieldWithPath("name").description("테마 이름"),
                fieldWithPath("description").description("테마 설명"),
                fieldWithPath("thumbnail").description("테마 썸네일")
        };
    }

    public static FieldDescriptor[] memberFieldDescriptors() {
        return new FieldDescriptor[]{
                fieldWithPath("id").description("유저 id"),
                fieldWithPath("name").description("유저 이름")
        };
    }
}
