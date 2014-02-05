<?php

$db = mysql_connect('localhost', 'ooio_user', 'outono');
@mysql_select_db("ooio") or die( "Unable to select database");

$since=$_REQUEST['since'];
$result = mysql_query("SELECT * FROM points WHERE UNIX_TIMESTAMP(time) > ".$since, $db);

while($row = mysql_fetch_array($result, MYSQL_ASSOC)) {
	echo $row['x'].','.$row['y'].','.$row['brushValue']."\n";
}
mysql_close($db);
?>
