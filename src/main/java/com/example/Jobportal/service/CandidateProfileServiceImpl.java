package com.example.Jobportal.service;
import com.example.Jobportal.entity.CandidateProfileEntity;
import com.example.Jobportal.model.CandidateProfile;
import com.example.Jobportal.repository.CandidateProfileRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateProfileServiceImpl implements CandidateProfileService{
    private final CandidateProfileRepository candidateProfileRepository;
public CandidateProfileServiceImpl(CandidateProfileRepository candidateProfileRepository)
{
    this.candidateProfileRepository=candidateProfileRepository;
}
    @Override
    public CandidateProfile saveCandidateProfile(CandidateProfile candidateProfile) {
        CandidateProfileEntity candidateProfileEntity=new CandidateProfileEntity();
        BeanUtils.copyProperties(candidateProfile,candidateProfileEntity);
        CandidateProfileEntity savedEntity=candidateProfileRepository.save(candidateProfileEntity);
        BeanUtils.copyProperties(savedEntity,candidateProfile);
        return candidateProfile;
    }

    @Override
    public List<CandidateProfile> getAllProfiles() {
        List<CandidateProfileEntity> profileEntities=candidateProfileRepository.findAll();
        return profileEntities.stream()
                .map(candidateProfileEntity -> new CandidateProfile(
                candidateProfileEntity.getId(),
                candidateProfileEntity.getCurrentJobTitle(),
                candidateProfileEntity.getCurrentCompany()
        )).toList();
    }

    @Override
    public CandidateProfile getProfileById(Long id) {
        CandidateProfileEntity candidateProfileEntity=candidateProfileRepository.findById(id).orElseThrow();
        CandidateProfile candidateProfile=new CandidateProfile();
        BeanUtils.copyProperties(candidateProfileEntity,candidateProfile);
        return candidateProfile;
    }

    @Override
    public boolean deleteProfile(Long id) {
        CandidateProfileEntity candidateProfile=candidateProfileRepository.findById(id).orElseThrow();
        candidateProfileRepository.delete(candidateProfile);
        return true;
    }

    @Override
    public CandidateProfile updateProfile(Long id, CandidateProfile candidateProfile) {
        CandidateProfileEntity candidateProfileEntity=candidateProfileRepository.findById(id).orElseThrow();
        candidateProfileEntity.setCurrentCompany(candidateProfile.getCurrentCompany());
        candidateProfileEntity.setCurrentJobTitle(candidateProfile.getCurrentJobTitle());
        candidateProfileRepository.save(candidateProfileEntity);
        return candidateProfile;
    }
}
