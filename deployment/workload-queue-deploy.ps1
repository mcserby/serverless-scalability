$version=$args[0]
# manager
cd ../../cf/manager-cloud-function
$zipResult=jar -cvMf manager-workload-queue-cloud-function-$version.zip src pom.xml
Write-Output $zipResult
cd ../../
$mvResult=mv ./cf/manager-cloud-function/manager-workload-queue-cloud-function-$version.zip .
$copyResult=gsutil cp manager-workload-queue-cloud-function-$version.zip gs://playground-cf/
Write-Output $copyResult
Remove-Item manager-workload-queue-cloud-function-$version.zip

# worker
cd cf/worker-pulling-workloads-cloud-function
$zipResult=jar -cvMf worker-pulling-workloads-cloud-function-$version.zip src pom.xml
Write-Output $zipResult
cd ../../
$mvResult=mv ./cf/worker-pulling-workloads-cloud-function/worker-pulling-workloads-cloud-function-$version.zip .
$copyResult=gsutil cp worker-pulling-workloads-cloud-function-$version.zip gs://playground-cf/
Write-Output $copyResult
Remove-Item worker-pulling-workloads-cloud-function-$version.zip

