$version=$args[0]
# manager
cd ../../cf/manager-cloud-function
$zipResult=jar -cvMf manager-direct-call-worker-cloud-function-$version.zip src pom.xml
Write-Output $zipResult
cd ../../
$mvResult=mv ./cf/manager-cloud-function/manager-direct-call-worker-cloud-function-$version.zip .
$copyResult=gsutil cp manager-direct-call-worker-cloud-function-$version.zip gs://playground-cf/
Write-Output $copyResult
Remove-Item manager-direct-call-worker-cloud-function-$version.zip

# worker
cd cf/worker-directly-invoked-cloud-function
$zipResult=jar -cvMf etl-worker-cloud-function-$version.zip src pom.xml
Write-Output $zipResult
cd ../../
$mvResult=mv ./cf/worker-directly-invoked-cloud-function/etl-worker-cloud-function-$version.zip .
$copyResult=gsutil cp etl-worker-cloud-function-$version.zip gs://playground-cf/
Write-Output $copyResult
Remove-Item etl-worker-cloud-function-$version.zip

