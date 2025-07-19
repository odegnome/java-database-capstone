package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DoctorService {
    private DoctorRepository doctorRepository;
    private AppointmentRepository appointmentRepository;
    private TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusHours(23);
        var result = doctorRepository.findById(doctorId);
        if (result.isEmpty()) {
            throw new RuntimeException("Invalid doctor ID.");
        }
        Doctor doctor = result.get();
        List<String> doctorTimes = doctor.getAvailableTimes();
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);
        if (appointments.isEmpty()) {
            return doctorTimes;
        }

        return doctorTimes
                .stream()
                .filter(time -> appointments.stream().anyMatch(apt -> apt.getAppointmentTime().toString().equals(time)))
                .toList();
    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
        } catch (Exception e) {
            return 0;
        }

        return 1;
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctor.getId());
            if (existingDoctor.isEmpty()) {
                return -1;
            }
            doctorRepository.save(doctor);
        } catch (RuntimeException e) {
            return 0;
        }

        return 1;
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctor = doctorRepository.findById(id);
            if (doctor.isEmpty()) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
        } catch (RuntimeException e) {
            return 0;
        }

        return 1;
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        var message = new HashMap<String, String>();

        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        if (doctor == null) {
            message.put("error", "Invalid _email_ or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        if (!doctor.getPassword().equals(login.getPassword())) {
            message.put("error", "Invalid email or _password_");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        message.put("token", tokenService.generateToken(login.getEmail()));

        return ResponseEntity.ok().body(message);
    }

    public Map<String, Object> findDoctorByName(String name) {
        var message = new HashMap<String, Object>();

        var doctors = doctorRepository.findByNameLike(name);
        message.put("doctors", doctors);

        return message;
    }
// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 11. **filterDoctorsByNameSpecilityandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.

// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.


// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).

// 14. **filterDoctorByNameAndSpecility Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.


// 15. **filterDoctorByTimeAndSpecility Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).

// 16. **filterDoctorBySpecility Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.

// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.


}
