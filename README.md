<?php

$name = $_GET["image_name"];
$response = array();
if(isset($_FILES['pic']['name'])){
	define('UPLOAD_PATH','user-images/');
	$ext = pathinfo(($_FILES['pic']['name']), PATHINFO_EXTENSION);
	try{
		move_uploaded_file($_FILES['pic']['tmp_name'],UPLOAD_PATH . $name.".".$ext);
		$response['error'] = false;
		$response['message'] = 'Image uploaded successfully';
	}catch(Exception $e){
		$response['error'] = true;
		$response['message'] = 'Not uploaded successfully';
	}
}else{
	$response['error'] = true;
	$response['message'] = 'insufficient parameter';
}

echo json_encode($response);

?>
