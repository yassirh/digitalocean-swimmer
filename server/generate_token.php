<?php

$code = $_GET['code'];
$clientId = "";
$clientSecret = "";
$redirectionUrl = rawurlencode("https://yassirh.com/digitalocean_swimmer/generate_token.php");
$url = sprintf("https://cloud.digitalocean.com/v1/oauth/token?client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s&redirect_uri=%s", $clientId, $clientSecret, $code, $redirectionUrl);

$fields = array();
$fields_string = "";
$ch = curl_init();
curl_setopt($ch,CURLOPT_URL, $url);
curl_setopt($ch,CURLOPT_POST, count($fields));
curl_setopt($ch,CURLOPT_POSTFIELDS, $fields_string);
curl_setopt($ch,CURLOPT_RETURNTRANSFER, TRUE);

$result = curl_exec($ch);
curl_close($ch);

$json = json_decode($result, TRUE);

header(sprintf("Location:callback://com.yassirh.digitalocean?code=%s&account_name=%s&refresh_token=%s&expires_in=%s",
			$json["access_token"], rawurlencode($json["info"]["name"]), $json["refresh_token"], $json["expires_in"]));
