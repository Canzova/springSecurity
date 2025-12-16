package com.codingshuttle.youtube.hospitalManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

/*
    Without getters/setters in DTOs:
        Jackson cannot populate fields (unless configured differently)
        Validation may not work as expected

 */

public class LoginResponseDTO {
    private Long id;
    private String username;
    private String jwt;

}
