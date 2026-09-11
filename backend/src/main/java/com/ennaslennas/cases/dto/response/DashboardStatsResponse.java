package com.ennaslennas.cases.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalReceived;
    private long submitted;
    private long underReview;
    private long needMoreInfo;
    private long published;
    private long inProgress;
    private long completed;
    private long totalHelpOffers;
    private long newHelpOffers;
}
