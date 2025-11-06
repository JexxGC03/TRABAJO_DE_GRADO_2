package com.ucdc.backend.application.services.user;

import com.ucdc.backend.application.dto.user.GetMyProfileQuery;
import com.ucdc.backend.application.dto.user.GetMyProfileResult;
import com.ucdc.backend.application.usecase.user.GetMyProfileUseCase;
import com.ucdc.backend.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyProfileService implements GetMyProfileUseCase {

    private final UserRepository userRepo;

    @Override
    public GetMyProfileResult handle(GetMyProfileQuery query) {
        var u = userRepo.findById(query.userId())
                .orElseThrow(() -> new com.ucdc.backend.domain.exceptions.logic.NotFoundException("User", query.userId()));
        return new GetMyProfileResult(
                u.fullName(),
                u.email() != null ? u.email().value() : null,
                u.phone() != null ? u.phone().value() : null
        );
    }
}
