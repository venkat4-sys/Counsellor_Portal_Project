
# Counsellor Application API Endpoints

This document provides a quick reference for the API endpoints in the Counsellor Application.

## Authentication Endpoints

### Register User
- **Endpoint**: `http://localhost:8082/auth/register`
- **Method**: POST
- **Payload**:
  ```json
  {
    "name": "Jane Smith",
    "email": "janesmith@test.org",
    "password": "janesmith",
    "phnum": "9123456789"
  }
  ```

### Login User
- **Endpoint**: `http://localhost:8082/auth/login`
- **Method**: POST
- **Payload**:
  ```json
  {
    "email": "raghu@gmail.com",
    "password": "raghu_147"
  }
  ```

## Enquiry Management Endpoints

### Add Enquiry
- **Endpoint**: `http://localhost:8082/auth/add-enquiry`
- **Method**: POST
- **Payload**:
  ```json
  {
    "student_name": "Ravi Kumar",
    "phno": "9876543210",
    "classmode_id": 1,
    "course_id": 3,
    "status_id": 2
  }
  ```
### Update Enquiry
- **Endpoint**: `http://localhost:8082/auth/update-enquiry-status`
- **Method**: POST
- **Payload**:
  ```json
    {
    "id":2,  
    "student_name": "siva Kumar",
    "phno": "9876543236",
    "classmode_id": 1,
    "course_id": 2,
    "status_id": 2
  }
  ```
### Get Dropdowns Data
- **Endpoint**: `http://localhost:8082/auth/get-dropdowns-data`
- **Method**: GET
- **Description**: Retrieves data for dropdown menus (e.g., class modes, courses, statuses).

### Get Dashboard Data
- **Endpoint**: `http://localhost:8082/auth/get-dashboard-data`
- **Method**: GET
- **Description**: Retrieves dashboard statistics and data.

### Get Enquiries
- **Endpoint**: `http://localhost:8082/auth/get-enquiries`
- **Method**: POST
- **Payload**:
  ```json
  {
    "classModeId": 1,
    "courseId": 2,
    "statusId": 2
  }
  ```
- **Description**: Retrieves enquiries filtered by the provided criteria.