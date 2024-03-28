package maker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import maker.model.ResumeModel;




@Repository
public interface ResumeRepository extends JpaRepository<ResumeModel, Integer> {

	Optional<ResumeModel> findById(int id);
    
}