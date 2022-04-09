$version=$args[0]
cd ../../cf/simple-etl-cloud-function
$zipResult=jar -cvMf simple-etl-cloud-function-$version.zip src pom.xml
Write-Output $zipResult
cd ../../
$mvResult=mv ./cf/simple-etl-cloud-function/simple-etl-cloud-function-$version.zip .
$copyResult=gsutil cp simple-etl-cloud-function-$version.zip gs://playground-cf/
Write-Output $copyResult
Remove-Item simple-etl-cloud-function-$version.zip

