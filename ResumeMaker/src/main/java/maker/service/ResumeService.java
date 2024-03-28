package maker.service;

import java.util.List;

import maker.model.ResumeModel;

public interface ResumeService {
	ResumeModel saveResume(ResumeModel resume);
    ResumeModel getResumeById(int id);
    List<ResumeModel> getAllResumes();
    ResumeModel updateResume(ResumeModel resume);
    void deleteResume(int id);
}