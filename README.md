# Leave Management System

A full-stack **Java Spring Boot** application for automating and managing employee leave requests.  
The system uses **Flowable BPMN** to orchestrate multi-level approval workflows based on leave type and organizational hierarchy.  

This project can serve as a **production-ready HR leave management system** or as a **learning project** for Spring Boot, BPMN, and workflow-driven applications.  

---

## 📑 Table of Contents

-[✨ Features](#-features)
- [⚙️ Technology Stack](#-technology-stack)
- [📂 Project Structure](#-project-structure)
- [🗄 Database Schema](#-database-schema)
- [🔄 Workflow Logic](#-workflow-logic)
- [🚀 Installation & Setup](#-installation--setup)
- [🔑 Access & Default Login Credentials](#-access--default-login-credentials)
- [🧪 Testing the Workflow](#-testing-the-workflow)
- [⚙️ Configuration](#-configuration)
- [📡 API Endpoints](#-api-endpoints)
- [📝 Usage Examples](#-usage-examples)
- [👨‍💻 Development Workflow](#-development-workflow)
- [🚀 Potential Enhancements](#-potential-enhancements)

---

## ✨ Features

- **Role-Based Access Control (RBAC):** Different dashboards for Employees, Managers, HODs, and HR.  
- **Workflow-Driven Approvals:** BPMN 2.0 workflows (via Flowable) enforce business rules.  
- **Multi-Level Approval:**  
  - Vacation → Manager approval.  
  - Sick → Manager + HR verification.  
  - Emergency → Direct to HR.  
- **Automated Notifications:** Emails for approvals, rejections, escalations, and reminders.  
- **Payroll Integration Simulation:** Retry-enabled service task simulates payroll updates.  
- **Self-Service Portal:** Employees view leave balance, submit requests, track history.  
- **Responsive UI:** Bootstrap + Thymeleaf views.  

---


## ⚙️ Technology Stack

- **Backend:** Java 17, Spring Boot 3.1+, Spring Security, Spring Data JPA  
- **Workflow Engine:** Flowable BPMN  
- **Database:** H2 (default), MySQL/PostgreSQL  
- **Frontend:** Thymeleaf, Bootstrap 5.3  
- **Security:** Spring Security, BCrypt password encoding  
- **Build Tool:** Maven

- 
## 📂 Project Structure
````
src/
├── main/
│ ├── java/com/example/leavemanagement/
│ │ ├── config/ # App & Data Initialization
│ │ ├── controller/ # Web controllers
│ │ ├── entity/ # JPA entities
│ │ ├── repository/ # DAO layer
│ │ ├── security/ # Spring Security config
│ │ ├── service/ # Business logic + Flowable tasks
│ │ └── LeavemanagementApplication.java
│ └── resources/
│ ├── processes/ # BPMN workflow definitions
│ ├── static/ # CSS, JS, images
│ ├── templates/ # Thymeleaf templates
│ └── application.properties
````
## 🗄 Database Schema

### Users Table

| Field        | Type    | Description             |
|--------------|---------|-------------------------|
| id           | Long    | Primary key             |
| username     | String  | Unique login ID         |
| password     | String  | BCrypt-hashed password  |
| first_name   | String  | First name              |
| last_name    | String  | Last name               |
| email        | String  | Email address           |
| is_hr        | Boolean | HR role flag            |
| is_hod       | Boolean | HOD role flag           |
| leave_balance| Integer | Remaining leave days    |
| manager_id   | Long    | Self-referential manager ID |

### LeaveRequests Table

| Field               | Type    | Description                            |
|-------------------- |---------|----------------------------------------|
| id                  | Long    | Primary key                             |
| employee_id         | Long    | FK to Users (employee)                  |
| type                | Enum    | VACATION, SICK, EMERGENCY              |
| start_date          | Date    | Start of leave                          |
| end_date            | Date    | End of leave                            |
| reason              | String  | Reason for leave                        |
| status              | Enum    | PENDING, APPROVED, REJECTED, ESCALATED |
| process_instance_id | String  | Flowable workflow process ID            |
| approved_by_manager | Boolean | Manager decision                        |
| approved_by_hod     | Boolean | HOD decision                            |
| approved_by_hr      | Boolean | HR decision                             |
| rejection_reason    | String  | Reason for rejection                    |

## 🔄 Workflow Logic

- **Vacation Leave:** Employee → Manager approval → (Optional) HOD escalation → Payroll → Notification  
- **Sick Leave:** Employee → Manager approval → HR verification → Payroll → Notification  
- **Emergency Leave:** Employee → HR verification → Payroll → Notification  
- **Escalations:** HOD approval required if manager escalates. Includes 24h timer reminders.  

---

## 🚀 Installation & Setup

### Prerequisites

- Java 17+  
- Maven 3.8+  
- MySQL/PostgreSQL (optional, for production use)  

### Steps

```bash
# Clone repo
git clone <your-repo-url>
cd leavemanagement

# Build
./mvnw clean install

# Run
./mvnw spring-boot:run
````
## 🔑 Access & Default Login Credentials

**App Access:**  
- App → [http://localhost:8080](http://localhost:8080)  
- H2 Console → [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  

### Default Login Credentials

| Role                  | Username  | Password     | Access               |
|-----------------------|-----------|-------------|--------------------|
| Admin                 | admin     | admin123    | Full access         |
| HR                    | hr        | hr123       | HR Dashboard        |
| Head of Department    | hod       | hod123      | HOD Dashboard       |
| Manager               | manager   | manager123  | Manager Dashboard   |
| Employee              | employee  | employee123 | Employee Dashboard  |

---

## 🧪 Testing the Workflow

1. Login as `employee / employee123` → submit leave request  
2. Login as `manager / manager123` → approve or escalate  
3. Login as `hod / hod123` → approve escalations  
4. Login as `hr / hr123` → verify Sick/Emergency requests  
## ⚙️ Configuration

Edit `src/main/resources/application.properties` to configure your database and email settings.

### MySQL Example

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/leavemanagement
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
````
### Email (SMTP) Configuration

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
````
## 📡 API Endpoints

| URL                     | Method    | Role     | Description                  |
|-------------------------|-----------|---------|-----------------------------|
| /                       | GET       | All     | Redirect to dashboard        |
| /login                  | GET       | Public  | Login page                   |
| /dashboard              | GET       | All     | Role-based dashboard         |
| /employee/dashboard     | GET       | Employee| Employee dashboard           |
| /employee/request       | GET/POST  | Employee| Submit leave request         |
| /manager/dashboard      | GET       | Manager | Manager dashboard            |
| /manager/approve/{id}   | POST      | Manager | Approve request              |
| /hod/dashboard          | GET       | HOD     | HOD dashboard                |
| /hr/dashboard           | GET       | HR      | HR dashboard                 |
| /hr/payroll             | GET       | HR      | Payroll simulation view      |

---

## 📝 Usage Examples

### Submit a Leave Request

```http
POST /employee/request
Content-Type: application/json

{
  "type": "VACATION",
  "startDate": "2025-09-10",
  "endDate": "2025-09-15",
  "reason": "Family trip"
}
````
### Response

```json
{
  "id": 12,
  "status": "PENDING",
  "message": "Leave request submitted successfully"
}
````
## 👨‍💻 Development Workflow

- Use H2 DB for quick testing  
- Switch to MySQL/PostgreSQL for persistent storage  
- Edit BPMN in Flowable Modeler  
- Write tests with JUnit 5 + Spring Boot Test  
- Extend workflows with timers, escalations, or new leave types  

---

## 🚀 Potential Enhancements

- Database migration (Flyway, Liquibase)  
- REST API for mobile apps  
- Extended leave types (Maternity, Paternity, Unpaid)  
- Department hierarchy with org chart  
- Reporting (PDF, CSV exports)  
- OAuth2 / SSO login  
- Containerized deployment (Docker, Kubernetes)  


## 👥 Contributors

- [Bouiguigne Achraf]


