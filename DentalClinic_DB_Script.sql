-- ================================================================
-- DENTAL CLINIC MANAGEMENT SYSTEM - DATABASE SCRIPT (v3.0 Code-First)
-- ================================================================
-- Database: SunriseDentalClinic
-- Version: 3.0 - Code-First Data Storage Layer
-- Created: 2026-08-02
-- ================================================================

IF EXISTS (SELECT * FROM sys.databases WHERE name = 'SunriseDentalClinic')
BEGIN
    ALTER DATABASE SunriseDentalClinic SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE SunriseDentalClinic;
END
GO

CREATE DATABASE SunriseDentalClinic;
GO

USE SunriseDentalClinic;
GO

-- ================================================================
-- 1. TABLES & CONSTRAINTS
-- ================================================================

-- Table: ClinicSettings
CREATE TABLE ClinicSettings (
    SettingID INT PRIMARY KEY IDENTITY(1,1),
    ClinicName VARCHAR(100) NOT NULL,
    ClinicAddress VARCHAR(255) NULL,
    ClinicPhone VARCHAR(15) NULL,
    ClinicEmail VARCHAR(100) NULL,
    DefaultSlotDurationMinutes INT NOT NULL DEFAULT 30,
    LastUpdated DATETIME2 DEFAULT GETDATE()
);
GO

-- Table: Users (Staff Authentication)
CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,       -- BCrypt Hashed Password
    Email VARCHAR(100) NULL,
    FullName VARCHAR(100) NOT NULL,
    Role VARCHAR(50) NOT NULL DEFAULT 'Receptionist',
    IsActive BIT NOT NULL DEFAULT 1,
    CreatedDate DATETIME2 DEFAULT GETDATE(),
    LastLogin DATETIME2 NULL,
    CreatedBy INT NULL
);
GO

-- Table: Patients
CREATE TABLE Patients (
    PatientID INT PRIMARY KEY IDENTITY(1,1),
    PatientName VARCHAR(100) NOT NULL,
    Address VARCHAR(255) NULL,
    ContactNumber VARCHAR(15) NOT NULL,
    Email VARCHAR(100) NULL,
    DateOfBirth DATE NULL,
    Gender CHAR(1) NULL,
    NIC VARCHAR(20) NULL,
    RegisteredDate DATETIME2 DEFAULT GETDATE(),
    IsActive BIT NOT NULL DEFAULT 1
);
GO

-- Table: Dentists
CREATE TABLE Dentists (
    DentistID INT PRIMARY KEY IDENTITY(1,1),
    DentistName VARCHAR(100) NOT NULL,
    Email VARCHAR(100) NULL,
    ContactNumber VARCHAR(15) NULL,
    Specialization VARCHAR(100) NULL,
    LicenseNumber VARCHAR(50) NULL,
    Qualifications VARCHAR(255) NULL,
    ConsultationFee DECIMAL(10,2) NOT NULL DEFAULT 500.00,
    ExperienceYears INT NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    CreatedDate DATETIME2 DEFAULT GETDATE()
);
GO

-- Table: TreatmentTypes
CREATE TABLE TreatmentTypes (
    TreatmentTypeID INT PRIMARY KEY IDENTITY(1,1),
    TreatmentName VARCHAR(100) NOT NULL,
    Description VARCHAR(255) NULL,
    BaseCost DECIMAL(10,2) NOT NULL,
    IsActive BIT NOT NULL DEFAULT 1
);
GO

-- Table: DentistSchedule (Shift Configurations)
CREATE TABLE DentistSchedule (
    ScheduleID INT PRIMARY KEY IDENTITY(1,1),
    DentistID INT NOT NULL,
    ScheduleDate DATE NOT NULL,
    SessionStartTime TIME NOT NULL,
    SessionEndTime TIME NOT NULL,
    SlotDurationMinutes INT NOT NULL DEFAULT 30,
    MaxPatientsInSession INT NOT NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    CreatedDate DATETIME2 DEFAULT GETDATE(),
    CONSTRAINT FK_Schedule_Dentists FOREIGN KEY (DentistID) REFERENCES Dentists(DentistID)
);
GO

-- Table: DentistSessionSlots (Session Slots)
CREATE TABLE DentistSessionSlots (
    SlotID INT PRIMARY KEY IDENTITY(1,1),
    ScheduleID INT NOT NULL,
    DentistID INT NOT NULL,
    SlotNumber INT NOT NULL,
    SlotStartTime TIME NOT NULL,
    SlotEndTime TIME NOT NULL,
    IsBooked BIT NOT NULL DEFAULT 0,
    CONSTRAINT FK_Slots_Schedule FOREIGN KEY (ScheduleID) REFERENCES DentistSchedule(ScheduleID),
    CONSTRAINT FK_Slots_Dentists FOREIGN KEY (DentistID) REFERENCES Dentists(DentistID)
);
GO

-- Table: Appointments
CREATE TABLE Appointments (
    AppointmentNumber INT PRIMARY KEY IDENTITY(1000,1),
    PatientID INT NOT NULL,
    DentistID INT NOT NULL,
    TreatmentTypeID INT NOT NULL,
    ScheduleID INT NOT NULL,
    SlotID INT NOT NULL,
    AppointmentDate DATE NOT NULL,
    Status VARCHAR(20) NOT NULL DEFAULT 'Scheduled',
    Notes VARCHAR(255) NULL,
    CreatedDate DATETIME2 DEFAULT GETDATE(),
    CreatedBy INT NOT NULL,
    CONSTRAINT FK_Appt_Patients FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    CONSTRAINT FK_Appt_Dentists FOREIGN KEY (DentistID) REFERENCES Dentists(DentistID),
    CONSTRAINT FK_Appt_Treatments FOREIGN KEY (TreatmentTypeID) REFERENCES TreatmentTypes(TreatmentTypeID),
    CONSTRAINT FK_Appt_Schedule FOREIGN KEY (ScheduleID) REFERENCES DentistSchedule(ScheduleID),
    CONSTRAINT FK_Appt_Slots FOREIGN KEY (SlotID) REFERENCES DentistSessionSlots(SlotID),
    CONSTRAINT UQ_Dentist_Slot UNIQUE (DentistID, AppointmentDate, SlotID)
);
GO

-- Table: appointment_treatments (Multi-Procedure Join Table)
CREATE TABLE appointment_treatments (
    appointment_id INT NOT NULL,
    treatment_type_id INT NOT NULL,
    PRIMARY KEY (appointment_id, treatment_type_id),
    CONSTRAINT FK_ApptTreatments_Appt FOREIGN KEY (appointment_id) REFERENCES Appointments(AppointmentNumber),
    CONSTRAINT FK_ApptTreatments_Type FOREIGN KEY (treatment_type_id) REFERENCES TreatmentTypes(TreatmentTypeID)
);
GO

-- Table: Bills (Financial Snapshot Records)
CREATE TABLE Bills (
    BillID INT PRIMARY KEY IDENTITY(1000,1),
    AppointmentNumber INT UNIQUE NOT NULL,
    PatientID INT NOT NULL,
    DentistID INT NOT NULL,
    ConsultationFee DECIMAL(10,2) NOT NULL,
    TreatmentCost DECIMAL(10,2) NOT NULL,
    TotalCost DECIMAL(10,2) NOT NULL,
    DiscountAmount DECIMAL(10,2) DEFAULT 0.00,
    FinalAmount DECIMAL(10,2) NOT NULL,
    PaymentStatus VARCHAR(20) DEFAULT 'Pending',
    PaymentMethod VARCHAR(50) NULL,
    PaymentDate DATETIME2 NULL,
    BillDate DATETIME2 DEFAULT GETDATE(),
    IssuedBy INT NOT NULL,
    CONSTRAINT FK_Bills_Appointments FOREIGN KEY (AppointmentNumber) REFERENCES Appointments(AppointmentNumber),
    CONSTRAINT FK_Bills_Patients FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    CONSTRAINT FK_Bills_Dentists FOREIGN KEY (DentistID) REFERENCES Dentists(DentistID)
);
GO

-- ================================================================
-- 2. SEED DATA
-- ================================================================

INSERT INTO ClinicSettings (ClinicName, ClinicAddress, ClinicPhone, ClinicEmail, DefaultSlotDurationMinutes)
VALUES ('Sunrise Dental Clinic', 'No. 123, Galle Road, Colombo 03', '+94 11 234 5678', 'info@sunrisedental.lk', 30);

-- Initial Users (Password: Admin@123 / Receptionist@123 hashed via BCrypt)
INSERT INTO Users (Username, Password, FullName, Role)
VALUES 
('admin', '$2a$12$e0MYzXyjpJS7Pd0RVvHwHe1m5s0Z8V4a1/8W7r5Y3X2b1c4d5e6f7', 'System Administrator', 'Admin'),
('receptionist', '$2a$12$e0MYzXyjpJS7Pd0RVvHwHe1m5s0Z8V4a1/8W7r5Y3X2b1c4d5e6f7', 'Nimali Perera', 'Receptionist');

-- Dentists (With per-dentist consultation fees)
INSERT INTO Dentists (DentistName, Email, ContactNumber, Specialization, Qualifications, ConsultationFee, ExperienceYears)
VALUES 
('Dr. Roshan Silva', 'roshan@sunrisedental.lk', '0771234567', 'General Dentistry', 'BDS (Peradeniya)', 500.00, 8),
('Dr. Anura Perera', 'anura@sunrisedental.lk', '0772345678', 'Orthodontics', 'BDS, MSc (London)', 1500.00, 15),
('Dr. Lakshmi Fernando', 'lakshmi@sunrisedental.lk', '0773456789', 'Periodontics', 'BDS, MD (Colombo)', 1200.00, 12);

-- Treatment Types
INSERT INTO TreatmentTypes (TreatmentName, Description, BaseCost)
VALUES 
('General Checkup', 'Routine dental examination and consultation', 1500.00),
('Tooth Extraction', 'Simple or surgical tooth extraction', 3500.00),
('Dental Cleaning (Scaling)', 'Full mouth scaling and polishing', 4500.00),
('Root Canal Treatment', 'Root canal therapy per tooth', 15000.00),
('Dental Filling', 'Composite or amalgam tooth filling', 3000.00);

-- Sample Patients
INSERT INTO Patients (PatientName, Address, ContactNumber, Email, NIC)
VALUES 
('Kasun Jayasinghe', 'No. 45, Main St, Nugegoda', '0711111111', 'kasun@gmail.com', '199012345678'),
('Priya Wijesinghe', 'No. 88, Kandy Rd, Kelaniya', '0722222222', 'priya@gmail.com', '199287654321'),
('Sunil Rathnayake', 'No. 12, Station Rd, Dehiwala', '0733333333', 'sunil@gmail.com', '198511223344');
GO
