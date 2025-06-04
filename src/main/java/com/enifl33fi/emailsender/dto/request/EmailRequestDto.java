package com.enifl33fi.emailsender.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDto {
    private String email;
    private String text;
    private String subject;
}
