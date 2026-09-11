package com.ennaslennas.cases.dto.request;

import com.ennaslennas.cases.domain.UrgencyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminCaseRequest {
    private String publicTitle;
    private String publicDescription;
    private String publicImageUrl;
    private UrgencyLevel urgency;
    private BigDecimal amountNeeded;
    private String internalNotes;
}
