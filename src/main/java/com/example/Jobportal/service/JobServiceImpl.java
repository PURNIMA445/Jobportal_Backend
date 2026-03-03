package com.example.Jobportal.service;
import com.example.Jobportal.entity.JobEntity;
import com.example.Jobportal.model.Job;
import com.example.Jobportal.repository.JobRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class JobServiceImpl implements  JobService{
    private final JobRepository jobRepository;
    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public Job saveJob(Job job) {

        JobEntity jobEntity = new JobEntity();

        // DTO → Entity
        BeanUtils.copyProperties(job, jobEntity);

        // SAVE and GET UPDATED ENTITY
        JobEntity savedEntity = jobRepository.save(jobEntity);

        // Entity → DTO
        BeanUtils.copyProperties(savedEntity, job);

        return job;
    }

    @Override
    public List<Job> getAllJobs() {
        List<JobEntity> jobEntities=jobRepository.findAll();
        return jobEntities.stream().map(jobEntity -> new Job(
                jobEntity.getId(),
                jobEntity.getTitle(),
                jobEntity.getDescription(),
                jobEntity.getLocation(),
                jobEntity.getSalary(),
                jobEntity.getExperience(),
                jobEntity.getJobType(),
                jobEntity.getCompanyName(),
                jobEntity.getPostedDate()
        )).toList();
    }

    @Override
    public Job getJobById(Long id) {
        JobEntity jobEntity=jobRepository.findById(id).orElseThrow();
        Job job=new Job();
        BeanUtils.copyProperties(jobEntity,job);
        return job;
    }

    @Override
    public boolean deleteJob(Long id) {
        JobEntity job=jobRepository.findById(id).orElseThrow();
        jobRepository.delete(job);
        return true;
    }

    @Override
    public Job updateJob(Long id, Job job) {
        JobEntity jobEntity=jobRepository.findById(id).orElseThrow();
        jobEntity.setLocation(job.getLocation());
        jobEntity.setJobType(job.getJobType());
        jobEntity.setExperience(job.getExperience());
        jobEntity.setDescription(job.getDescription());
        jobEntity.setCompanyName(job.getCompanyName());
        jobEntity.setPostedDate(job.getPostedDate());
        jobEntity.setTitle(job.getTitle());
        jobEntity.setSalary(job.getSalary());
        jobRepository.save(jobEntity);
        return job;
    }


}
