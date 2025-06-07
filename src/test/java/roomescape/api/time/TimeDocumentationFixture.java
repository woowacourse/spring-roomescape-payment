package roomescape.api.time;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static roomescape.api.fixture.DocumentationFixture.createDocumentWithDefaultPath;

import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class TimeDocumentationFixture {

    // descriptors
    public static final FieldDescriptor START_AT_FIELD_DESCRIPTOR = fieldWithPath("startAt").description("시작 시간");
    public static final FieldDescriptor TIME_ID_FIELD_DESCRIPTOR = fieldWithPath("id").description("예약 시간 ID");
    public static final FieldDescriptor TIME_ARRAY_DESCRIPTOR = fieldWithPath("[]").description("전체 시간 배열");
    public static final FieldDescriptor TIME_AVAILABILITY_ARRAY_DESCRIPTOR = fieldWithPath("[]")
            .description("예약 가능 시간 배열");
    public static final FieldDescriptor ALREADY_BOOKED_FIELD_DESCRIPTOR = fieldWithPath("alreadyBooked")
            .description("예약 여부");
    public static final ParameterDescriptor DATE_QUERY_PARAMETER_DESCRIPTOR = parameterWithName("date")
            .description("확인 하고 싶은 날짜");
    public static final ParameterDescriptor THEME_QUERY_PARAMETER_DESCRIPTOR = parameterWithName(
            "themeId").description("확인 하고 싶은 테마");
    public static final ParameterDescriptor TIME_ID_PARAMETER_DESCRIPTOR = parameterWithName("id")
            .description("예약 시간 ID");

    public static final List<FieldDescriptor> TIME_RESPONSE_DESCRIPTORS = List.of(
            TIME_ID_FIELD_DESCRIPTOR,
            START_AT_FIELD_DESCRIPTOR
    );
    public static final List<FieldDescriptor> TIME_AVAILABILITY_DESCRIPTORS = List.of(
            fieldWithPath("timeId").description("예약 시간 ID"),
            START_AT_FIELD_DESCRIPTOR,
            ALREADY_BOOKED_FIELD_DESCRIPTOR
    );

    // documents
    public static final RestDocumentationFilter CREATE_TIME_DOCUMENT = createDocumentWithDefaultPath(
            requestFields(START_AT_FIELD_DESCRIPTOR),
            responseFields(TIME_RESPONSE_DESCRIPTORS)
    );
    public static final RestDocumentationFilter FIND_ALL_TIMES_DOCUMENT = createDocumentWithDefaultPath(
            responseFields(TIME_ARRAY_DESCRIPTOR).andWithPrefix("[].", TIME_RESPONSE_DESCRIPTORS)
    );
    public static final RestDocumentationFilter FIND_ALL_TIME_AVAILABILITY_DOCUMENT = createDocumentWithDefaultPath(
            queryParameters(
                    DATE_QUERY_PARAMETER_DESCRIPTOR,
                    THEME_QUERY_PARAMETER_DESCRIPTOR
            ),
            responseFields(TIME_AVAILABILITY_ARRAY_DESCRIPTOR).andWithPrefix("[].", TIME_AVAILABILITY_DESCRIPTORS)
    );
    public static final RestDocumentationFilter DELETE_TIME_BY_ID_DOCUMENT = createDocumentWithDefaultPath(
            pathParameters(TIME_ID_PARAMETER_DESCRIPTOR)
    );
}
