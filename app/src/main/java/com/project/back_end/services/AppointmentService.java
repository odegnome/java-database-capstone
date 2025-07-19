package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private AppointmentRepository appointmentRepository;
    private com.project.back_end.services.Service service;
    private TokenService tokenService;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, com.project.back_end.services.Service service, TokenService tokenService, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
        } catch (Exception e) {
            return 0;
        }

        return 1;
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        var message = new HashMap<String, String>();
        try {
            var result = appointmentRepository.findById(appointment.getId());
            if (result.isEmpty()) {
                message.put("error", "Update request for invalid appointment ID");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
            }

            var beforeAppointment = result.get();
            if (!beforeAppointment.getPatient().getId().equals(appointment.getPatient().getId())) {
                message.put("error", "Patient not same as previous appointment");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
            } else if (!beforeAppointment.getDoctor().getId().equals(appointment.getDoctor().getId())) {
                message.put("error", "Doctor not same as previous appointment");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
            }
            int validatedAppointment = service.validateAppointment(appointment, "patient");
            switch (validatedAppointment) {
                case 1:
                    break;
                case 0:
                    message.put("error", "Appointment time not available or invalid");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
                case -1:
                    message.put("error", "Invalid Doctor requested for Appointment. Doctor not found.");
                    return ResponseEntity.badRequest().body(message);
            }
            appointmentRepository.save(appointment);
        } catch (IllegalArgumentException e) {
            message.put("error", e.toString());
            return ResponseEntity.badRequest().body(message);
        } catch (Exception e) {
            message.put("error", e.toString());
            return ResponseEntity.internalServerError().body(message);
        }

        message.put("message", "Appointment updated successfully");
        return ResponseEntity.ok().body(message);
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(Long id, String token) {
        var message = new HashMap<String, String>();
        if (!tokenService.validateToken(token, "patient")) {
            message.put("error", "Invalid authentication token");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
        }
        try {
            var aptmt = appointmentRepository.findById(id);
            if (aptmt.isEmpty()) {
                message.put("error", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
            }
            var appointment = aptmt.get();
            String tokenEmail = tokenService.extractEmail(token);
            if (!tokenEmail.equals(appointment.getPatient().getEmail())) {
                message.put("error", "Patient mismatch with sender");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
            }
            appointmentRepository.delete(appointment);
        } catch (Exception e) {
            message.put("error", e.toString());
            return ResponseEntity.internalServerError().body(message);
        }
        return ResponseEntity.ok().body(message);
    }

    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        if (!tokenService.validateToken(token, "doctor")) {
            throw new RuntimeException("Invalid authentication token");
        }
        String tokenEmail = tokenService.extractEmail(token);
        Doctor doctor = doctorRepository.findByEmail(tokenEmail);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusHours(23);
        var appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), startOfDay, endOfDay);
        var map = new HashMap<String, Object>();
        if (pname == null || pname.isBlank()) {
            map.put("message", appointments);
            return map;
        }
        var filteredAppointments = appointments
                .stream()
                .filter(appointment -> appointment.getPatient().getName().equals(pname))
                .toList();
        map.put("message", filteredAppointments);
        return map;
    }
// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.


}
