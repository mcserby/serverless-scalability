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

resource "null_resource" "archive-cloud-functions" {
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
