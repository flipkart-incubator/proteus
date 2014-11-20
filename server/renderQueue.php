<?php
header("Content-Type: application/json");
$action = $_GET['action'];

include_once "db.php";
connect();
$table = "render_queue";


if (!isset($_GET['action']) || $action == "fetch") {

	// called by the app to fetch any pending render requests

	$out = array();
	$body = new stdClass;

	$selectedRows=selectFromDB($table,"*","status='WAITING' limit 1");
	if(count($selectedRows)>0)
	{
		$body->layout = json_decode($selectedRows[0]['layout']);
		$body->id = $selectedRows[0]['id'];
	}
	
	$out['RESPONSE'] = $body;
	echo json_encode($out);
} else if($action == "upload") {

	//called by the app to submit a rendered bitmap

	$id = $_GET['id'];
	if($id>0)
	{
		$uploads_dir = 'bitmaps';
		$finalPath="";
		foreach($_FILES["bitmap"] as $key => $error) {
			if ($error == UPLOAD_ERR_OK) {
				$tmp_name = $_FILES["bitmap"][$key];
				$name = $_FILES["bitmap"]["name"];
				$finalPath = "$uploads_dir/$id.png";
				move_uploaded_file($tmp_name, $finalPath);
			}	
		}
		$result = updateDB($table,"status='COMPLETE', output='$finalPath'",$where="id=$id");
		print_r($result);

	}

} 
?>