package com.hp.springsecurity.security;

import com.hp.springsecurity.type.PermissionType;
import com.hp.springsecurity.type.RoleType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hp.springsecurity.type.PermissionType.*;
import static com.hp.springsecurity.type.RoleType.*;

public class RolePermissionMapping {

    private static final Map<RoleType, Set<PermissionType>> map = Map.of(
            PATIENT, Set.of(PATIENT_READ, APPOINTMENT_READ, APPOINTMENT_WRITE),
            DOCTOR, Set.of(APPOINTMENT_READ, APPOINTMENT_WRITE, APPOINTMENT_DELETE, PATIENT_READ),
            ADMIN, Set.of(PATIENT_READ, PATIENT_WRITE, APPOINTMENT_WRITE, APPOINTMENT_DELETE, USER_MANAGE, REPORT_VIEW)
    );

    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(RoleType role) {
        return map.get(role).stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
