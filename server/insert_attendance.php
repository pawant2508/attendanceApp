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
$date = $_POST['date'] ?? '';
$year = $_POST['year'] ?? '';
$sect = $_POST['sect'] ?? '';
$term = $_POST['term'] ?? '';
$department = $_POST['department'] ?? '';

if (empty($id) || empty($stu_id) || empty($date) || empty($year) || empty($sect) || empty($term) || empty($department)) {
    echo json_encode(['response' => 'All fields are required']);
    exit;
}

try {
    $dsn = "pgsql:host=ep-polished-wave-a8p9s9nh-pooler.eastus2.azure.neon.tech;port=5432;dbname=neondb;sslmode=require";
    $pdo = new PDO($dsn, 'neondb_owner', 'npg_9dkOhyiSoE1A');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $stmt = $pdo->prepare("INSERT INTO attendance (qr_id, student_id, attendance_date, year, sect, term, department) VALUES (?, ?, ?, ?, ?, ?, ?)");
    $stmt->execute([$id, $stu_id, $date, $year, $sect, $term, $department]);

    echo json_encode(['response' => 'Attendance recorded successfully']);
} catch (PDOException $e) {
    echo json_encode(['response' => 'Database error: ' . $e->getMessage()]);
}
?>