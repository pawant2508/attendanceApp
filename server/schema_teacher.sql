-- Add Teachers table
CREATE TABLE IF NOT EXISTS teachers (
    teacher_id TEXT PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    name TEXT NOT NULL,
    department TEXT
);

-- Add Classes/Sessions table
CREATE TABLE IF NOT EXISTS classes (
    class_id SERIAL PRIMARY KEY,
    teacher_id TEXT NOT NULL REFERENCES teachers(teacher_id),
    class_name TEXT NOT NULL,
    subject TEXT,
    section TEXT,
    qr_code TEXT UNIQUE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    is_active BOOLEAN DEFAULT true
);

-- Add Class Attendance junction table
CREATE TABLE IF NOT EXISTS class_attendance (
    attendance_id SERIAL PRIMARY KEY,
    class_id INTEGER NOT NULL REFERENCES classes(class_id),
    student_id TEXT NOT NULL REFERENCES students(student_id),
    attendance_date TIMESTAMPTZ NOT NULL DEFAULT now()
);