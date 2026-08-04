# UML Modeling Conventions & Standards Guide

## Overview
This document establishes the official **UML Modeling Conventions** and **PlantUML Standards** for the **Sunrise Dental Clinic Management System** (Module: CIS6003 - Advanced Programming). All system diagrams and specifications must strictly adhere to these guidelines to maintain consistency, maintainability in Git version control, and academic rigor.

---

## 1. Core Principles

1. **Separation of Source & Specification:**
   - **PlantUML Files (`.puml`)**: Contain ONLY standard PlantUML code (diagram structure, actors, classes, relationships, stereotypes, and UML elements). No lengthy business text or narrative documentation.
   - **Markdown Files (`.md`)**: Contain all descriptive documentation (purpose, element details, business rules, design rationale, assumptions, constraints).

2. **Naming & Filename Conventions:**
   - Folder names: lowercase with hyphens (e.g., `use-case/`, `class/`, `sequence/`, `activity/`).
   - PlantUML files: `<diagram-name>.puml` (e.g., `dental-clinic-use-case.puml`).
   - Specification files: `<diagram-name>-specification.md` (e.g., `dental-clinic-use-case-specification.md`).
   - PlantUML element IDs: CamelCase or PascalCase (e.g., `BookingService`, `PatientID`).

---

## 2. Diagram Notation Standards

### 2.1 Use Case Diagrams
- **Actors**: Human actors represent roles (`actor "Receptionist" as Recept`).
- **Actor Generalization**: Inheritance denoted by `--|>` (e.g., `Recept --|> Staff`).
- **System Boundary**: Grouped inside a `rectangle "Sunrise Dental Clinic Management System" { ... }`.
- **Stereotypes**: Use `<<include>>` for mandatory sub-processes and `<<extend>>` for conditional extensions.

### 2.2 Class Diagrams
- **Visibility Modifiers**: Private `-`, Public `+`, Protected `#`, Package `~`.
- **Relationships**:
  - Generalization (Inheritance): `--|>`
  - Realization (Interface implementation): `..|>`
  - Composition (Strong lifecycle ownership): `*--`
  - Aggregation (Weak ownership): `o--`
  - Association: `--`
  - Dependency: `..>`
- **Multiplicities**: Explicitly specified on all association ends (e.g., `"1" -- "0..*"`).

### 2.3 Sequence Diagrams
- **Lifelines**: `actor`, `participant`, `boundary`, `control`, `entity`, `database`.
- **Messages**: Synchronous call `->>`, Return message `-->>`.
- **Combined Fragments**:
  - `alt ... else ... end` for conditional branching.
  - `opt ... end` for optional execution.
  - `loop ... end` for repetitive flows.

### 2.4 Activity Diagrams
- **Nodes**: Initial node `(*)`, Action states `:Action Name;`, Decision diamonds `if (...) then (...) else (...) endif`, Final node `(*)`.
- **Swimlanes**: Separated by `|Swimlane Name|` to assign responsibilities to specific layers (Presentation, Service, Database).
