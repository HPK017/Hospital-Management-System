package com.hp.springsecurity.dto;

import com.hp.springsecurity.type.BloodGroupType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BloodGroupResponseEntity {

    private BloodGroupType bloodGroupType;
    private Long count;

}
