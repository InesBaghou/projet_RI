<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
	<link rel="stylesheet" href="style.css">
	<title>SEARCH</title>
</head>
<body>

<?php

include ("connexion.php");

?>
<div class="LOGO">
<p><img src='Logo.png' alt='Logo'></p>
</div>

	<br/>
	<form action="accueil.php" method="POST">
		<label>Saisissez un numéro patient pour continuer.</label>
		<input class="google" type ="text" name="num"></br>

		<p><input type="submit" name="BTN_OK" value="OK" class="btn"></p>


	<?php

	if (isset($_POST['BTN_OK']))
	{
		$requete= "select distinct LibelleCIM10, D.CodeCIM10  from ths_cim10 as T, tab_diagnostic as D, tab_hospitalisation as H where D.NumHospitalisation = H.NumHospitalisation and H.NumPatient ='".$_POST['num']."'  and T.CodeCIM10=D.CodeCIM10";

		$resultat=$bdd->query($requete);

		echo " <p>Cliquer sur le diagnostic associé au patient si vous souhaitez consulter la littérature à propos de celui-ci.</p>";


		while ($ligne = $resultat->fetch()){
			extract ($ligne);
			echo " <label><input type='radio' name ='DIAG' value=".$CodeCIM10." /> ",$CodeCIM10,'  -  ', $LibelleCIM10," </label> <br /> \n ";

	}
echo"	<p><input type='submit' name='DIAG' value='OK' class='btn'></p>";

	}


	if (isset($_POST['DIAG']))
	{
		echo " <label name='btnEN'><input type='radio' name ='lang' value='1' class='btn'/> ENGLISH</label> ";
		echo " <label name='btnFR'><input type='radio' name ='lang' value='2'class='btn' />FRANCAIS</label>  \n ";

		echo " <label name='ReqType'><input type='radio' name ='req' value='1' class='btn'/> Requête simple</label> ";
		echo " <label name='ReqType'><input type='radio' name ='req' value='2'class='btn' />Requête étendue</label>  \n ";

	}


	include ("footer.html");


	?>


	</form>
</body>
</html>
