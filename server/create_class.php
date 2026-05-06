<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['response' => 'Invalid request method']);
    exit;
}

$teacher_id = $_POST['teacher_id'] ?? '';
$class_name = $_POST['class_name'] ?? '';
$subject = $_POST['subject'] ?? '';
$section = $_POST['section'] ?? '';

if (empty($teacher_id) || empty($class_name) || empty($subject) || empty($section)) {
    echo json_encode(['response' => 'All fields are required']);
    exit;
}

try {
    $dsn = "pgsql:host=ep-polished-wave-a8p9s9nh-pooler.eastus2.azure.neon.tech;port=5432;dbname=neondb;sslmode=require";
    $pdo = new PDO($dsn, 'neondb_owner', 'npg_9dkOhyiSoE1A');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Generate unique QR code
    $qr_code = 'QR_' . uniqid() . '_' . time();

    $stmt = $pdo->prepare("INSERT INTO classes (teacher_id, class_name, subject, section, qr_code) VALUES (?, ?, ?, ?, ?) RETURNING class_id");
    $stmt->execute([$teacher_id, $class_name, $subject, $section, $qr_code]);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    echo json_encode([
        'response' => 'Class created successfully',
        'class_id' => $result['class_id'],
        'qr_code' => $qr_code
    ]);
} catch (PDOException $e) {
    echo json_encode(['response' => 'Database error: ' . $e->getMessage()]);
}
?>