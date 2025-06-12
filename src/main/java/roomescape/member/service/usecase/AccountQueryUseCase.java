package roomescape.member.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.NotFoundException;
import roomescape.member.controller.dto.LoginRequest;
import roomescape.member.domain.Account;
import roomescape.member.domain.MemberEmail;
import roomescape.member.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountQueryUseCase {

    private final AccountRepository accountRepository;

    public Account getAccount(LoginRequest loginRequest) {
        final String email = loginRequest.email();
        return accountRepository.findAccountByMemberEmail(MemberEmail.from(email))
                .orElseThrow(() -> new NotFoundException("email: 으로 등록된 계정이 존재하지 않습니다.", email));
    }
}
