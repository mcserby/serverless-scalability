variable "project" {}
variable "region"  {}
variable "credentials" {}
variable "cloud_functions_version" {}
variable "cloud_function_bucket" {}

#simple_etl resources
variable "simple_etl_trigger_topic" {}
variable "simple_etl_schedule" {}
variable "simple_etl_workload_duration" {}

#manager direct call workers etl resources
variable "manager_direct_call_trigger_topic" {}
variable "etl_worker_topic" {}
variable "manager_direct_call_etl_schedule" {}
variable "worker_batch_size" {}
variable "max_workloads" {}
variable "directly_triggered_workload_duration" {}