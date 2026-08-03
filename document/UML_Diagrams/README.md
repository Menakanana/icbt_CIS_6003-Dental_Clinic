# Sunrise Dental Clinic - Modular UML & System Diagram Suite

## Overview
This directory contains the official **PlantUML Diagrams (`.puml`)** and **Markdown Specifications (`.md`)** for the **Sunrise Dental Clinic Management System** (Module: CIS6003 - Advanced Programming).

The documentation strictly separates PlantUML diagram source code from descriptive specifications to maintain clean Git version control and support high-quality PDF report generation.

---

## 📁 Modular Directory Structure

```text
document/UML_Diagrams/
├── uml-conventions.md                           <- Official UML Standards & Guidelines
│
├── use-case/                                    <- Task A Use Case Diagram
│   ├── dental-clinic-use-case.puml              <- Pure PlantUML Notation
│   └── dental-clinic-use-case-specification.md  <- Detailed Markdown Specs
│
├── class/                                       <- Task A Domain Class Diagram
│   ├── dental-clinic-class.puml                 <- Pure PlantUML Class Code
│   └── dental-clinic-class-specification.md     <- Detailed Class Specifications
│
├── sequence/                                    <- Task A Sequence Diagrams
│   ├── dental-clinic-booking-sequence.puml      <- Booking & Overlap Validation Sequence
│   ├── dental-clinic-booking-sequence-specification.md
│   ├── dental-clinic-auth-sequence.puml         <- Staff Authentication Sequence
│   ├── dental-clinic-auth-sequence-specification.md
│   ├── dental-clinic-billing-sequence.puml      <- Invoicing & Financial Snapshot Sequence
│   └── dental-clinic-billing-sequence-specification.md
│
├── er/                                          <- Database ER Diagram
│   ├── dental-clinic-er.puml                    <- Crow's Foot ER Diagram Code
│   └── dental-clinic-er-specification.md        <- Detailed ER Data Dictionary & Cardinality Specs
│
└── activity/                                    <- Task B Activity Flowchart
    ├── dental-clinic-booking-activity.puml      <- Booking Swimlane Flowchart Code
    └── dental-clinic-booking-activity-specification.md
```

---

## 🛠️ Viewing & Rendering Instructions

1. **In VS Code:** Install the **`jebbs.plantuml`** extension. Open any `.puml` file and press **`Alt + D`** to render the visual diagram live!
2. **Online:** Copy the contents of any `.puml` file and paste it into **[www.plantuml.com/plantuml](https://www.plantuml.com/plantuml)** or **[draw.io](https://app.diagrams.net)** (*Arrange $\rightarrow$ Insert $\rightarrow$ Advanced $\rightarrow$ PlantUML*).
