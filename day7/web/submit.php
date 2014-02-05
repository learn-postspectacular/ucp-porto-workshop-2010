<?php

$db = mysql_connect('localhost', 'ooio_user', 'outono');
@mysql_select_db("ooio") or die( "Unable to select database");

$numPoints=$_POST['num'];
$sql="";


for($i=0; $i<$numPoints; $i++) {
	$sql="INSERT INTO `points` (`x`, `y`, `brushValue`) VALUES (";
	$sql.=$_POST['x'.$i].", ";
	$sql.=$_POST['y'.$i].", ";
	$sql.=$_POST['v'.$i].");";
	$result = mysql_query($sql,$db);
}

echo $sql."\n";

echo mysql_errno($db) . ": ". mysql_error($db);
mysql_close($db);
?>
