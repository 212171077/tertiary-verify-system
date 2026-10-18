package com.tertiary.verify.data_model.model.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WelcomeDataResponse {
    private boolean success;
    private String dataPrivacyUrl;
    private String errorMessage;
}
