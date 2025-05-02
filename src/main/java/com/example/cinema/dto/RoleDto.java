package com.example.cinema.dto;

import com.example.cinema.entity.RoleName;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private Long id;
    private RoleName name;
}
