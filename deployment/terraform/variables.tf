variable "project" {}
variable "region"  {}
variable "credentials" {}
variable "cloud_functions_version" {}
variable "cloud_function_bucket" {}

#simple_etl resources // scenario 1
variable "simple_etl_trigger_topic" {}
variable "simple_etl_schedule" {}
variable "simple_etl_workload_duration" {}

#manager direct call workers etl resources  // scenario 2
variable "manager_direct_call_trigger_topic" {}
variable "etl_worker_topic" {}
variable "manager_direct_call_etl_schedule" {}
variable "worker_batch_size" {}
variable "max_workloads" {}
variable "directly_triggered_workload_duration" {}

#manager with workload queue resources  // scenario 3
variable "manager_workload_queue_trigger_topic" {}
variable "manager_batch_size" {}
variable "worker_trigger_topic" {}
variable "etl_pulling_worker_topic" {}
variable "manager_workload_queue_trigger_etl_schedule" {}
variable "worker_trigger_etl_schedule" {}
variable "pulling_worker_batch_size" {}

