-- Create the students table
CREATE TABLE IF NOT EXISTS students (
    student_id TEXT PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    name TEXT NOT NULL,
    year TEXT,
    department TEXT,
    curryear TEXT,
    sect TEXT,
    term TEXT
);

-- Create the attendance table
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id SERIAL PRIMARY KEY,
    qr_id TEXT NOT NULL,
    student_id TEXT NOT NULL REFERENCES students(student_id),
    attendance_date TEXT,  -- Using TEXT to match app's string format
    year TEXT,
    sect TEXT,
    term TEXT,
    department TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Optional: Create an index on email for faster login queries
CREATE INDEX IF NOT EXISTS idx_students_email ON students(email);