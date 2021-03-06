package com.mogaco.project.member.dto;

import com.mogaco.project.member.domain.MemberSupplier;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 회원 등록 명세서.
 */
@Getter
@NoArgsConstructor
public class MemberRegisterDto implements MemberSupplier {
    /**
     * 사용자 이름.
     */
    @NotBlank
    private String name;

    /**
     * 사용자 이메일.
     */
    @NotBlank
    @Size(min = 3)
    private String email;

    /**
     * 사용자 비밀번호.
     */
    @NotBlank
    @Size(min = 4, max = 32)
    private String password;

    @Builder
    public MemberRegisterDto(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
