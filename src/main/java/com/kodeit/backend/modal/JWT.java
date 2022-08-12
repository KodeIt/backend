package com.kodeit.backend.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWT {

    private String accessToken;
    private String refreshToken;

}