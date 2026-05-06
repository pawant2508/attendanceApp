# My QR Code Attendance - Server Setup

## Overview
This server provides the backend API for the Android QR Code Attendance app, using PostgreSQL (Neon) as the database.

## Files
- `check_login.php`: Handles user login authentication
- `insert_attendance.php`: Records attendance when QR code is scanned
- `schema.sql`: Database schema creation script

## Setup Instructions

1. **Database Setup**:
   - Connect to your Neon PostgreSQL instance
   - Run the `schema.sql` script to create tables
   - Insert sample student data if needed

2. **Server Deployment**:
   - Upload these PHP files to your web server (e.g., Apache/Nginx with PHP)
   - Ensure PDO PostgreSQL extension is enabled
   - Update the Android app's BASE_URL to point to your server

3. **Security Notes**:
   - Passwords are stored in plain text (not recommended for production)
   - Consider adding password hashing (e.g., bcrypt)
   - Implement proper input validation and sanitization
   - Use HTTPS in production

## API Endpoints

### POST /check_login.php
**Request Body**:
- email: string
- password: string

**Response** (Success):
```json
{
  "response": "Login Successful",
  "studentID": "12345",
  "name": "John Doe",
  "year": "2023",
  "department": "Computer Science",
  "curryear": "3",
  "sect": "A",
  "term": "Fall"
}
```

**Response** (Failure):
```json
{
  "response": "Invalid Credentials"
}
```

### POST /insert_attendance.php
**Request Body**:
- id: string (QR code value)
- stu_id: string
- date: string
- year: string
- sect: string
- term: string
- department: string

**Response** (Success):
```json
{
  "response": "Attendance recorded successfully"
}
```

**Response** (Error):
```json
{
  "response": "Database error: [error message]"
}
```