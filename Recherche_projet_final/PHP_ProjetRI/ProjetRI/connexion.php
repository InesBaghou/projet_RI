<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
</head>
<body>
<?php
/*
// Connexion via neptune
$PARAM_hote='localhost';
$PARAM_port='13306';
$PARAM_nom_bd='LAHRACH';
$PARAM_utilisateur='LAHRACH';
$PARAM_mot_passe='XvSVIdwdSg0xOA';


try
{
	$bdd = new PDO('mysql:host='.$PARAM_hote.';port='.$PARAM_port.';dbname='.$PARAM_nom_bd, $PARAM_utilisateur, $PARAM_mot_passe, array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
	$bdd->exec("set names utf8");
}
catch (Exception $e)
{
        die('Erreur : ' . $e->getMessage());
};

*/
// Connexion en local

try
{
	$bdd = new PDO('mysql:host=localhost;dbname=ri', 'root', '', array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
	$bdd->exec("set names utf8");
}
catch (Exception $e)
{
        die('Erreur : ' . $e->getMessage());
};



?>
