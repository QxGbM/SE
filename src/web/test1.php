<!doctype html>
<html>
<body>
<?php
$actname = $_POST["acct"];
$pw = $_POST["pw"];

$servername = "localhost:3306";
$username = "root";
$password = "20!8Case20!8";
$dbname = "SP";

$id = rand(0,100000);
// Create connection

$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 


$sql = "insert into Account (id, name, win, loss, gold, dust, password) values ("."'".$id."'".","."'".$actname."'".",'0','0','0','0',"."'".$pw."'".")";


if ($conn->query($sql) === TRUE) {
    echo "<a href = 'index.html'> Thank you</a>";
} else {
    echo "<a href = 'index.html' >registration failed</a> " . $conn->error;
}

$conn->close();

?>
</body>
</html>