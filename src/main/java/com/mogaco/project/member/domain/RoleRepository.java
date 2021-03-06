package com.mogaco.project.member.domain;

import java.util.List;

/**
 * 권한 정보 저장소.
 */
public interface RoleRepository {
    List<Role> findAllByMemberId(Long memberId);

    Role save(Role role);
}
