<?php
    $con = mysqli_connect("localhost", "etroyo01", "x036cfp0mkp21kcm", "etroyo01");
    
    $username = $_POST["username"];
    $password = $_POST["password"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM runnableUser WHERE username = ? AND password = ?");
    mysqli_stmt_bind_param($statement, "ss", $username, $password);
    mysqli_stmt_execute($statement);
    
    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $userID, $firstName, $lastName, $email, $username, $password);
    
    $response = array();
    $response["success"] = false;  
    
    while(mysqli_stmt_fetch($statement)){
        $response["success"] = true;  
        $response["firstName"] = $firstName;
	$response["lastName"] = $lastName;
        $response["email"] = $email;
        $response["username"] = $username;
        $response["password"] = $password;
    }
    
    echo json_encode($response);
?>