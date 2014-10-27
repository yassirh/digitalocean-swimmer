<?php

$refreshToken = $_GET['refresh_token'];
$clientId = "";
$clientSecret = "";
$url = sprintf("https://cloud.digitalocean.com/v1/oauth/token?grant_type=refresh_token&client_id=%s&client_secret=%s&refresh_token=%s", $clientId, $clientSecret, $refreshToken);

$fields = array();
$fields_string = "";
$ch = curl_init();
curl_setopt($ch,CURLOPT_URL, $url);
curl_setopt($ch,CURLOPT_POST, count($fields));
curl_setopt($ch,CURLOPT_POSTFIELDS, $fields_string);
curl_setopt($ch,CURLOPT_RETURNTRANSFER, TRUE);

$result = curl_exec($ch);
curl_close($ch);

echo $result;
