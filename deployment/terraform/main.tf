# Configure GCP project
provider "google" {
  project = var.project
  region  = var.region
  credentials = var.credentials
}

terraform {
  backend "gcs" {
    bucket  = "tf-state-bk"
    prefix  = "terraform/state"
    credentials = "c:/credentials/google/sa.json"
  }
}

# simple ETL scenario

resource "null_resource" "archive-simple-etl-cloud-function" {
  triggers = {
    version = var.cloud_functions_version
  }
  provisioner "local-exec" {
    command = "PowerShell -file ../cloud-functions-deploy.ps1 ${var.cloud_functions_version}"
    interpreter = ["PowerShell", "-Command"]
  }
}

resource "google_pubsub_topic" "simple-etl-trigger-topic" {
  name = var.simple_etl_trigger_topic

  message_storage_policy {
    allowed_persistence_regions = [
      "europe-west3",
    ]
  }
}

resource "google_cloud_scheduler_job" "simple_etl_trigger_scheduler_job" {
  name        = "simple-etl-trigger-scheduler-job"
  description = "triggers simple etl job"
  schedule    = var.simple_etl_schedule

  pubsub_target {
    topic_name = "projects/${var.project}/topics/${var.simple_etl_trigger_topic}"
    data       = base64encode("{\"command\":\"simple_etl\"}")
  }
}

resource "google_cloudfunctions_function" "simple-etl-cloud-function" {
  depends_on = [null_resource.archive-simple-etl-cloud-function]
  name = "simple-etl-cloud-function"
  project = var.project
  region = var.region
  available_memory_mb = "256"
  entry_point = "eu.accesa.playground.SimpleEtlService"
  runtime = "java11"
  max_instances = 1
  timeout = 500
  source_archive_bucket = var.cloud_function_bucket
  source_archive_object = "simple-etl-cloud-function-${var.cloud_functions_version}.zip"
  event_trigger {
    event_type = "providers/cloud.pubsub/eventTypes/topic.publish"
    resource = "projects/${var.project}/topics/${var.simple_etl_trigger_topic}"
  }
  environment_variables = {
    PROJECT_ID = var.project
    WORKLOAD_DURATION = var.simple_etl_workload_duration
  }
}

# manager direct call worker scenario

resource "null_resource" "archive-manager-direct-call-cloud-function" {
  triggers = {
    version = var.cloud_functions_version
  }
  provisioner "local-exec" {
    command = "PowerShell -file ../manager-direct-call-worker-deploy.ps1 ${var.cloud_functions_version}"
    interpreter = ["PowerShell", "-Command"]
  }
}


resource "google_pubsub_topic" "manager-direct-call-trigger-topic" {
  name = var.manager_direct_call_trigger_topic

  message_storage_policy {
    allowed_persistence_regions = [
      "europe-west3",
    ]
  }
}

resource "google_pubsub_topic" "etl-worker-topic" {
  name = var.etl_worker_topic

  message_storage_policy {
    allowed_persistence_regions = [
      "europe-west3",
    ]
  }
}

resource "google_cloud_scheduler_job" "manager_direct_call_trigger_scheduler_job" {
  name        = "manager-direct-call-trigger-scheduler-job"
  description = "triggers manager for to start the ETL workloads"
  schedule    = var.manager_direct_call_etl_schedule

  pubsub_target {
    topic_name = "projects/${var.project}/topics/${var.manager_direct_call_trigger_topic}"
    data       = base64encode("{\"command\":\"etl_direct_call_workers\"}")
  }
}

resource "google_cloudfunctions_function" "manager-direct-call-cloud-function" {
  depends_on = [null_resource.archive-manager-direct-call-cloud-function]
  name = "manager-direct-call-cloud-function"
  project = var.project
  region = var.region
  available_memory_mb = "256"
  entry_point = "eu.accesa.playground.DirectCallEtlManager"
  runtime = "java11"
  max_instances = 1
  timeout = 500
  source_archive_bucket = var.cloud_function_bucket
  source_archive_object = "manager-direct-call-worker-cloud-function-${var.cloud_functions_version}.zip"
  event_trigger {
    event_type = "providers/cloud.pubsub/eventTypes/topic.publish"
    resource = "projects/${var.project}/topics/${var.manager_direct_call_trigger_topic}"
  }
  environment_variables = {
    PROJECT_ID = var.project
    WORKER_BATCH_SIZE = var.worker_batch_size
    ETL_WORKER_TOPIC = var.etl_worker_topic
    MAX_WORKLOADS = var.max_workloads
  }
}

resource "google_cloudfunctions_function" "etl-worker-cloud-function" {
  depends_on = [null_resource.archive-manager-direct-call-cloud-function]
  name = "etl-worker-cloud-function"
  project = var.project
  region = var.region
  available_memory_mb = "256"
  entry_point = "eu.accesa.playground.EtlWorkerService"
  runtime = "java11"
  max_instances = 1
  timeout = 500
  source_archive_bucket = var.cloud_function_bucket
  source_archive_object = "etl-worker-cloud-function-${var.cloud_functions_version}.zip"
  event_trigger {
    event_type = "providers/cloud.pubsub/eventTypes/topic.publish"
    resource = "projects/${var.project}/topics/${var.etl_worker_topic}"
  }
  environment_variables = {
    PROJECT_ID = var.project
    WORKLOAD_DURATION = var.directly_triggered_workload_duration
  }
}