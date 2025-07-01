# Database Design

## MySQL database

### Patients
```mysql
CREATE TABLE patients (
    id INT AUTO INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address VARCHAR(100) NOT NULL,
)
```

### Doctors
```mysql
CREATE TABLE doctors (
    id INT PRIMARY KEY,
    name VARHCHAR(50) NOT NULL,
    age VARCHAR(50) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    department VARCHAR(50) NOT NULL,
)
```

### Appointments
```mysql
CREATE TABLE appointments (
    id INT AUTO INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    apt_time DATETIME NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patients(id),
    FOREIGN KEY (doctor_ID) REFERENCES doctors(id),
    CONSTRAINT UNIQUE_APT UNIQUE(patient_id, doctor_id, apt_time)
)
```

### Admin
```mysql
CREATE TABLE admins (
    id INT AUTO INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(15) NOT NULL,
)
```

## MongoDB

### Prescriptions
```json
{
    "patient": {
        "name": "",
        "id": 0
    },
    "doctor": {
        "name": "",
        "doctor_id": 0
    },
    "apt_time": 0,
    "diagnosis": [],
    "medications": [],
    "comments": "",
}
```

### Feedback
```json
{
    "rating": 0,
    "name": "",
    "patient_id": 0,
    "doctor_id": 0, -- optional
    "comments": "",
}
```
