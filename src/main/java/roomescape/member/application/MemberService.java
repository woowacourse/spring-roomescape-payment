package roomescape.member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.domain.AuthRole;
import roomescape.exception.auth.AuthorizationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.ui.dto.MemberResponse;
import roomescape.member.ui.dto.MemberResponse.IdName;
import roomescape.member.ui.dto.SignUpRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse.IdName create(final SignUpRequest request) {
        log.info("[MemberService] 회원 가입 요청 - email: {}, name: {}", request.email(), request.name());

        final Member member = Member.of(
                request.name(),
                request.email(),
                request.password(),
                AuthRole.MEMBER
        );

        final Member saved = memberRepository.save(member);

        log.info("[MemberService] 회원 가입 완료 - id: {}, name: {}", saved.getId(), saved.getName());

        return new MemberResponse.IdName(saved.getId(), saved.getName());
    }

    @Transactional
    public void delete(final Long memberId) {
        log.info("[MemberService] 회원 삭제 요청 - id: {}", memberId);

        final Member found = memberRepository.getById(memberId);

        if (found.getRole() == AuthRole.ADMIN) {
            log.warn("[MemberService] 관리자 계정 삭제 시도 차단 - id: {}", memberId);
            throw new AuthorizationException("관리자 계정은 삭제할 수 없습니다.");
        }

        memberRepository.deleteById(memberId);

        log.info("[MemberService] 회원 삭제 완료 - id: {}", memberId);
    }

    @Transactional(readOnly = true)
    public List<IdName> findAllNames() {
        List<IdName> names = memberRepository.findAll().stream()
                .map(IdName::from)
                .toList();

        log.info("[MemberService] 전체 회원 이름 조회 - count: {}", names.size());

        return names;
    }
}
