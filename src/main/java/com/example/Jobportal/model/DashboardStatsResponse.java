package com.example.Jobportal.model;

import lombok.*;

@Getter @Setter @Builder
public class DashboardStatsResponse {
    private long applications;
    private long underReview;
    private long shortlisted;
    private long rejected;
}