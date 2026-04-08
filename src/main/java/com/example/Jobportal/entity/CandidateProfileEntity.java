package com.example.Jobportal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="candidateProfile")
public class CandidateProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String currentJobTitle;
    private String currentCompany;
    public  CandidateProfileEntity(){}
    public CandidateProfileEntity(String currentJobTitle,String currentCompany)
    {
        this.currentJobTitle=currentJobTitle;
        this.currentCompany=currentCompany;
    }
}
