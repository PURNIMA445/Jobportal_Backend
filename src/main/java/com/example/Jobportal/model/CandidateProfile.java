package com.example.Jobportal.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateProfile {
    private long id;
    private String currentJobTitle;
    private String currentCompany;
    public CandidateProfile(){}
    public CandidateProfile(long id, String currentJobTitle, String currentCompany)
    {
        this.id=id;
        this.currentJobTitle=currentJobTitle;
        this.currentCompany=currentCompany;
    }
}
