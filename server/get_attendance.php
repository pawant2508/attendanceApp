<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');
header('Access-Control-Allow-Headers: Content-Type');

$class_id = $_GET['class_id'] ?? '';

if (empty($class_id)) {
    echo json_encode(['response' => 'Class ID required']);
    exit;
}

try {
    $dsn = "pgsql:host=ep-polished-wave-a8p9s9nh-pooler.eastus2.azure.neon.tech;port=5432;dbname=neondb;sslmode=require";
    $pdo = new PDO($dsn, 'neondb_owner', 'npg_9dkOhyiSoE1A');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $stmt = $pdo->prepare("
        SELECT s.student_id, s.name, s.department, COUNT(ca.attendance_id) as attendance_count
        FROM students s
        LEFT JOIN class_attendance ca ON s.student_id = ca.student_id AND ca.class_id = ?
        GROUP BY s.student_id, s.name, s.department
        ORDER BY s.name
    ");
    $stmt->execute([$class_id]);
    $attendance = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'response' => 'Attendance retrieved successfully',
        'attendance' => $attendance
    ]);
} catch (PDOException $e) {
    echo json_encode(['response' => 'Database error: ' . $e->getMessage()]);
}
?>