<?php
include_once "db.php";

connect();
$table = "render_queue";

$layout = mysql_escape_string(json_encode(json_decode($_POST['layout'])));
$timeout = 20000000; //micro seconds
$sleepTime = 100000; //micro seconds

if ($layout != null) {
	$id = insertIntoDB($table, "null,'$layout','WAITING',''");
	if ($id > 0) {
		$selectedRows = array();
		$currentTimeout = 0;
		do {
			$currentTimeout+=$sleepTime;
			usleep($sleepTime);// wait 100 ms for app to pick up
			$selectedRows=selectFromDB($table,"*","id=$id AND status='COMPLETE'");
			if($currentTimeout>$timeout)
			{
				break;
			}
		}
		while (count($selectedRows)==0);

		if(count($selectedRows)>0)
		{
			header('Content-Type: image/png');
			$src = $selectedRows[0]['output'];
			readfile($src); // output image to browser
			unlink($src); // delete image file
			deleteFromDB($table,"id=$id"); // delete db row
		}
		else
		{
			echo "No bitmap generated after timeout. Maybe client app is not running.";
			deleteFromDB($table,"id=$id"); // delete db row

		}

	}
	else
	{
		echo "Insert to DB failed.";
	}

} else {
	echo "Layout is not specified. Expected POST with layout=json";
} ?>