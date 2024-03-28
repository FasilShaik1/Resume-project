package maker.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import maker.model.ResumeModel;
import maker.repository.ResumeRepository;
import maker.service.ResumeService;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Override
    public ResumeModel saveResume(ResumeModel resume) {
        return resumeRepository.save(resume);
    }

    @Override
    public ResumeModel getResumeById(int id) {
        return resumeRepository.findById(id).orElse(null);
    }

    @Override
    public List<ResumeModel> getAllResumes() {
        return resumeRepository.findAll();
    }

    @Override
    public ResumeModel updateResume(ResumeModel resume) {
    	ResumeModel existingResume = resumeRepository.findById(resume.getId()).orElse(null);
        if (existingResume != null) {
            existingResume.setName(resume.getName());
            existingResume.setEmail(resume.getEmail());
            existingResume.setPhone(resume.getPhone());
            existingResume.setAddress(resume.getAddress());
            existingResume.setSkills(resume.getSkills());
            existingResume.setEducation(resume.getEducation());
            existingResume.setExperience(resume.getExperience());
            return resumeRepository.save(existingResume);
        }
        return null;
    }

    @Override
    public void deleteResume(int id) {
        resumeRepository.deleteById(id);
    }
}