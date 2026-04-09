package com.example.Jobportal.service;

import com.example.Jobportal.entity.RecruiterProfileEntity;
import com.example.Jobportal.model.RecruiterProfile;
import com.example.Jobportal.repository.RecruiterProfileRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecruiterProfileServiceImpl implements RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;

    public RecruiterProfileServiceImpl(RecruiterProfileRepository recruiterProfileRepository) {
        this.recruiterProfileRepository = recruiterProfileRepository;
    }

    @Override
    public RecruiterProfile createProfile(RecruiterProfile recruiterProfile) {
        RecruiterProfileEntity entity = new RecruiterProfileEntity();

        BeanUtils.copyProperties(recruiterProfile, entity);

        RecruiterProfileEntity saved = recruiterProfileRepository.save(entity);

        BeanUtils.copyProperties(saved, recruiterProfile);

        return recruiterProfile;
    }

    @Override
    public List<RecruiterProfile> getAllProfiles() {
        List<RecruiterProfileEntity> entities = recruiterProfileRepository.findAll();

        return entities.stream()
                .map(entity -> new RecruiterProfile(
                        entity.getId(),
                        entity.getUserId(),
                        entity.getCompanyName(),
                        entity.getJobTitle(),
                        entity.getSpecialization(),
                        entity.getCompanyWebsite(),
                        entity.getLinkedInUrl()
                )).toList();
    }

    @Override
    public RecruiterProfile getProfileById(Long id) {
        RecruiterProfileEntity entity = recruiterProfileRepository.findById(id).orElseThrow();

        RecruiterProfile profile = new RecruiterProfile();
        BeanUtils.copyProperties(entity, profile);

        return profile;
    }

    @Override
    public boolean deleteProfile(Long id) {
        RecruiterProfileEntity entity = recruiterProfileRepository.findById(id).orElseThrow();
        recruiterProfileRepository.delete(entity);
        return true;
    }

    @Override
    public RecruiterProfile updateProfile(Long id, RecruiterProfile recruiterProfile) {
        RecruiterProfileEntity entity = recruiterProfileRepository.findById(id).orElseThrow();

        entity.setCompanyName(recruiterProfile.getCompanyName());
        entity.setJobTitle(recruiterProfile.getJobTitle());
        entity.setSpecialization(recruiterProfile.getSpecialization());
        entity.setCompanyWebsite(recruiterProfile.getCompanyWebsite());
        entity.setLinkedInUrl(recruiterProfile.getLinkedInUrl());

        recruiterProfileRepository.save(entity);

        return recruiterProfile;
    }
}