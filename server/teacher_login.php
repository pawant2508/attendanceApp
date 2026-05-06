<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['response' => 'Invalid request method']);
    exit;
}

$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

if (empty($email) || empty($password)) {
    echo json_encode(['response' => 'All fields are required']);
    exit;
}

try {
    $dsn = "pgsql:host=ep-polished-wave-a8p9s9nh-pooler.eastus2.azure.neon.tech;port=5432;dbname=neondb;sslmode=require";
    $pdo = new PDO($dsn, 'neondb_owner', 'npg_9dkOhyiSoE1A');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $stmt = $pdo->prepare("SELECT teacher_id, name, department FROM teachers WHERE email = ? AND password = ?");
    $stmt->execute([$email, $password]);
    $teacher = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($teacher) {
        echo json_encode([
            'response' => 'Login Successful',
            'teacherID' => $teacher['teacher_id'],
            'name' => $teacher['name'],
            'department' => $teacher['department']
        ]);
    } else {
        echo json_encode(['response' => 'Invalid Credentials']);
    }
} catch (PDOException $e) {
    echo json_encode(['response' => 'Database error: ' . $e->getMessage()]);
}
?>