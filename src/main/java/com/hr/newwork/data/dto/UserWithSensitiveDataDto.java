package com.hr.newwork.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserWithSensitiveDataDto extends UserDto {
    private SensitiveDataDto sensitiveData;
}