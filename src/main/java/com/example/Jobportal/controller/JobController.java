package com.example.Jobportal.controller;
import com.example.Jobportal.model.Job;
import com.example.Jobportal.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class JobController {
    private final JobService jobService;
    public JobController(JobService jobService) {

        this.jobService = jobService;
    }
    @PostMapping("/jobs")
    public Job saveJob(@RequestBody Job job)
    {

        return jobService.saveJob(job);
    }
    @GetMapping("/jobs")
    public List<Job> getAllJobs()
    {

        return jobService.getAllJobs();
    }
    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id)
    {
        Job job;
        job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }
    @DeleteMapping("/jobs/{id}")
    public  ResponseEntity<Map<String,Boolean>> deleteJob(@PathVariable Long id)
    {
        boolean deleted;
        deleted=jobService.deleteJob(id);
        Map<String,Boolean> response= new HashMap<>();
        response.put("deleted",deleted);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/jobs/{id}")
    public ResponseEntity<Job>updateJob(@PathVariable Long id,
                                          @RequestBody Job job){
        job=jobService.updateJob(id,job);
        return ResponseEntity.ok(job);

    }


}
