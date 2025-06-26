package org.example.expert.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class SignupResponse {

    private String email;
    private String nickname;

}
