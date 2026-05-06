<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['response' => 'Invalid request method']);
    exit;
}

$id = $_POST['id'] ?? '';
$stu_id = $_POST['stu_id'] ?? '';
$year = $_POST['year'] ?? '';
$sect = $_POST['sect'] ?? '';
$term = $_POST['term'] ?? '';
$department = $_POST['department'] ?? '';

if (empty($id) || empty($stu_id) || empty($year) || empty($sect) || empty($term) || empty($department)) {
    echo json_encode(['response' => 'All fields are required']);
    exit;
}

try {
    $dsn = "pgsql:host=ep-polished-wave-a8p9s9nh-pooler.eastus2.azure.neon.tech;port=5432;dbname=neondb;sslmode=require";
    $pdo = new PDO($dsn, 'neondb_owner', 'npg_9dkOhyiSoE1A');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Find the corresponding class by QR code and ensure it has not expired
    $stmt = $pdo->prepare("SELECT class_id, expires_at FROM classes WHERE qr_code = ? AND is_active = true");
    $stmt->execute([$id]);
    $class = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$class) {
        echo json_encode(['response' => 'Invalid or inactive QR code']);
        exit;
    }

    $expiresAt = new DateTime($class['expires_at']);
    $now = new DateTime();
    if ($now > $expiresAt) {
        echo json_encode(['response' => 'QR code has expired']);
        exit;
    }

    // Prevent duplicate attendance for the same student and class
    $stmt = $pdo->prepare("SELECT 1 FROM class_attendance WHERE class_id = ? AND student_id = ?");
    $stmt->execute([$class['class_id'], $stu_id]);
    if ($stmt->fetch()) {
        echo json_encode(['response' => 'Attendance already recorded for this student']);
        exit;
    }

    $stmt = $pdo->prepare("INSERT INTO class_attendance (class_id, student_id, attendance_date) VALUES (?, ?, now())");
    $stmt->execute([$class['class_id'], $stu_id]);

    echo json_encode(['response' => 'Attendance recorded successfully']);
} catch (PDOException $e) {
    echo json_encode(['response' => 'Database error: ' . $e->getMessage()]);
}
?>