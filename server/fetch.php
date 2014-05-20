<?php

$view = $_GET['view'];
$contents = openJson($view);

$json = json_decode($contents);

function openJson($name)
{
	return file_get_contents("json/$name.json");
}

function replacer($rootFileName,$object) {

	
	if ($object != null) {
		if ($object->include != null) {
			$widgetName = $object->include;
			$fileToOpen = "$rootFileName.$widgetName";			
			$widgetContents = openJson($fileToOpen);
			$widgetObject = json_decode($widgetContents);
			$widgetObject = replacer($fileToOpen,$widgetObject);
			return $widgetObject;
		}
		

		if($object->children!=null)
		{
		foreach($object->children as $key => $value) {
			$object->children[$key] = replacer($rootFileName,$value);
		}
	}
	return $object;
	}

}

$json->RESPONSE->layout = replacer($view,$json->RESPONSE->layout);

header("Content-Type: application/json");

echo json_encode($json);

?>