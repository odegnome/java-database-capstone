package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availableTimes WHERE a.doctor.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    public List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availableTimes LEFT JOIN FETCH a.patient p WHERE a.doctor.id = :doctorId AND LOWER(p.name) LIKE CONCAT('%',LOWER(:patientName),'%') AND a.appointmentTime BETWEEN :start AND :end")
    public List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
//    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    public void deleteAllByDoctorId(Long doctorId);

    public List<Appointment> findByPatientId(Long patientId);

    public List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE CONCAT('%',LOWER(:doctorName),'%') AND a.patient.id = :patientId")
    public List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

    @Query("SELECT a FROM Appointment a WHERE LOWER(a.doctor.name) LIKE CONCAT('%',LOWER(:doctorName),'%') AND a.patient.id = :patientId AND a.status = :status")
    public List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);

    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    public void updateStatus(int status, Long id);
}
