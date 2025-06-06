package roomescape.controller.docs.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.dto.response.MemberResponse;
import roomescape.dto.response.MyPageReservationResponse;

@Tag(name = "회원 관리", description = "회원 가입, 조회 및 마이페이지 API")
public interface MemberControllerDocs {

    @Operation(
            summary = "회원 가입",
            description = """
        새로운 회원을 등록합니다.
        1. 이메일 중복을 검증합니다.
        2. 비밀번호를 암호화하여 저장합니다.
        3. 기본 역할은 USER로 설정됩니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberRegisterResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                        "id": 1,
                        "email": "user@example.com",
                        "name": "홍길동"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 존재하는 이메일",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "이메일 중복",
                                    value = "{\"errorMessage\": \"이미 존재하는 이메일입니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<MemberRegisterResponse> registerMember(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원 가입 요청 데이터",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberRegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "회원 가입 요청 예시",
                                    summary = "일반적인 회원 가입 요청",
                                    value = """
                    {
                        "email": "user@example.com",
                        "password": "password123",
                        "name": "홍길동"
                    }
                    """
                            )
                    )
            )
            @RequestBody MemberRegisterRequest request
    );

    @Operation(
            summary = "전체 회원 조회",
            description = "시스템에 등록된 모든 회원의 기본 정보(ID, 이름)를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "id": 1,
                            "name": "홍길동"
                        },
                        {
                            "id": 2,
                            "name": "김철수"
                        }
                    ]
                    """
                            )
                    )
            )
    })
    ResponseEntity<List<MemberResponse>> getAllMembers();

    @Operation(
            summary = "내 예약 목록 조회 (마이페이지)",
            description = """
        특정 회원의 모든 예약을 조회합니다.
        - 예약 정보와 함께 대기 우선순위를 계산하여 반환합니다.
        - 결제 정보가 있는 경우 함께 포함됩니다.
        - 우선순위는 해당 예약 항목에서 현재 예약보다 먼저 등록된 예약의 수입니다.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MyPageReservationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                    [
                        {
                            "reservationId": 1,
                            "theme": "공포의 저택",
                            "date": "2024-12-25",
                            "time": "14:00:00",
                            "status": "승인됨",
                            "priority": 0,
                            "payment": {
                                "paymentKey": "test_payment_key_123",
                                "amount": 30000
                            }
                        },
                        {
                            "reservationId": 2,
                            "theme": "미스터리 하우스",
                            "date": "2024-12-26",
                            "time": "16:00:00",
                            "status": "대기중",
                            "priority": 2,
                            "payment": null
                        }
                    ]
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 회원 ID",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "회원 조회 실패",
                                    value = "{\"errorMessage\": \"존재하지 않는 사용자입니다.\"}"
                            )
                    )
            )
    })
    ResponseEntity<List<MyPageReservationResponse>> getMyReservations(
            @Parameter(hidden = true) Long memberId
    );
}
