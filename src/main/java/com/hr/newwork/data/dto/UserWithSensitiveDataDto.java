package com.hr.newwork.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
public class UserWithSensitiveDataDto extends UserDto {
    private SensitiveDataDto sensitiveData;
}