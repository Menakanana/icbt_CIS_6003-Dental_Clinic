# Dental Clinic Management System (Sunrise Dental Clinic)

## Overview
This repository contains the database design, architecture documentation, and system specifications for the **Sunrise Dental Clinic Management System** developed for module **CIS6003 - Advanced Programming** (Cardiff Metropolitan University / ICBT Campus).

The system adopts a **Modern Code-First 3-Tier Web API Architecture** built on **Spring Boot, Spring Data JPA / Hibernate (ORM)** where all business rules, dynamic slot generation, patient overlap validation, financial calculations, and BCrypt security reside in clean, testable Java Application Services.

---

## 📁 Repository & Document Structure

All project documentation is organized cleanly inside the [`document/`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document) directory:

- 📄 [`document/APPLICATION_ARCHITECTURE_GUIDE.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/APPLICATION_ARCHITECTURE_GUIDE.md) – **Master System & Application Guide**: Architecture trade-off justification, Design Patterns, Java REST Controllers, Spring Data JPA Derived Repositories, Services, DTO Validation, Security (RBAC), Global Exception Handler, Reports Engine, Swagger UI, CORS, and JUnit 5 / MockMvc tests.
- 📄 [`document/DATABASE_DESIGN_DOCUMENT.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/DATABASE_DESIGN_DOCUMENT.md) – **Database Data Dictionary**: Table specifications, data types, primary keys, foreign keys, unique constraints, and ER relationship diagrams.
- 📄 [`document/Assignment_CIS6003_Advanced_Programming.md`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/document/Assignment_CIS6003_Advanced_Programming.md) – Official assignment brief.

---

## 🗄️ Database Script

- 💾 [`DentalClinic_DB_Script.sql`](file:///d:/BSC/CIS_6003-Advanced_Programming/Assignment/icbt_CIS_6003-Dental_Clinic/DentalClinic_DB_Script.sql) – Clean DDL relational script containing table definitions, foreign keys, unique constraints, and seed data.