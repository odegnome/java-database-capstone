package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    public Doctor findByEmail(String email);

    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%',:name,'%')")
    public List<Doctor> findByNameLike(String name);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.name) LIKE CONCAT('%',LOWER(:name),'%') and LOWER(d.specialty) = LOWER(:specialty)")
    public List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String speciality);

//    @Query("SELECT d FROM Doctor d WHERE LOWER(d.specialty) = LOWER(:specialty)")
    public List<Doctor> findBySpecialtyIgnoreCase(String specialty);

// 2. Custom Query Methods:
//    - **findBySpecialtyIgnoreCase**:
//      - This method retrieves a list of Doctors with the specified specialty, ignoring case sensitivity.
//      - Return type: List<Doctor>
//      - Parameters: String specialty

// 3. @Repository annotation:
//    - The @Repository annotation marks this interface as a Spring Data JPA repository.
//    - Spring Data JPA automatically implements this repository, providing the necessary CRUD functionality and custom queries defined in the interface.

}