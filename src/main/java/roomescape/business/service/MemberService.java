package roomescape.business.service;

import static roomescape.exception.ErrorCode.EMAIL_DUPLICATED;
import static roomescape.exception.ErrorCode.USER_NOT_EXIST;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.Id;
import roomescape.exception.business.InvalidCreateArgumentException;
import roomescape.exception.business.NotFoundException;
import roomescape.infrastructure.MemberRepository;
import roomescape.presentation.dto.request.RegisterRequest;
import roomescape.presentation.dto.response.MemberResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse register(RegisterRequest request) {
        if (memberRepository.existsByEmail_Value(request.email())) {
            throw new InvalidCreateArgumentException(EMAIL_DUPLICATED);
        }
        Member member = Member.create(request.name(), request.email(), request.password());
        memberRepository.save(member);
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(final String userIdValue) {
        Member member = memberRepository.findById(Id.create(userIdValue))
                .orElseThrow(() -> new NotFoundException(USER_NOT_EXIST));
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getByEmail(final String email) {
        Member member = memberRepository.findByEmail_Value(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_EXIST));
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAll() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }
}
