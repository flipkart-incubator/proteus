<?

function connect()
{
	$host="localhost";
	$user="root";
	$pass="root"; // dont worry about this. The pass wont be displayed unless php source is viewed
	$db="layout_engine";
	$link=mysql_connect($host,$user,$pass) or die("Could not connect to DB: ".mysql_error());
	mysql_select_db($db);
	return $link;
}

function disconnect()
{mysql_close();}

function fetchArray($select,$table,$whereString)
{
	$link=connect();
	
	$query="select $select from $table where $whereString";
	$result=mysql_query($query) or mysqlError($query);
	while($arr=mysql_fetch_array($result,MYSQL_ASSOC))
		{
		$cont[]=$arr;
		}
		if($cont)
		return $cont;
		else
		return array(0,0);
		
}

function updateDB($table,$set,$where=" ")
{
	connect();
	if(!empty($where))
	$where="where $where";
	
	$query="update $table set $set $where";
	$result=mysql_query($query) or mysqlError($query);
	return $result;
}

function selectFromDB($table,$select,$whereString="")
{
	$link=connect();
	
	if($whereString)
	$where="where $whereString";
	$query="select $select from $table $where";
	$result=mysql_query($query) or mysqlError($query);
	while($arr=mysql_fetch_array($result,MYSQL_ASSOC))
		{
		$cont[]=$arr;
		}
		return $cont;
		
}


function insertIntoDB($table,$valuesString)
{
	$link=connect();
	
	$query="insert into $table values ($valuesString)";
	$result=mysql_query($query) or mysqlError($query);
	return mysql_insert_id();
}
function deleteFromDB($table,$where)
{
	$query="delete from $table where $where limit 1";
	$result=mysql_query($query) or mysqlError($query);
	return $result;
}
function mysqlError($query)
{
	die("Query error during <b><u> $query </u></b> into  table:'$table' because::<br><br><i> ".mysql_error()."</i>");
}



?>
