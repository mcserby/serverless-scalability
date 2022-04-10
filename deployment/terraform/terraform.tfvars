project = "accesa-playground"
region  = "europe-west3"
credentials = "c:/credentials/google/sa.json"

cloud_function_bucket = "playground-cf"
cloud_functions_version = "0.1.0-S2"

# simple ETL
simple_etl_trigger_topic = "simple-etl-trigger-topic"
simple_etl_schedule = "*/5 * * * *"
simple_etl_workload_duration = 10

# manager direct call ETL
manager_direct_call_trigger_topic = "manager-direct-call-trigger-topic"
etl_worker_topic =  "etl-worker-topic"
manager_direct_call_etl_schedule = "0 * * * *"
worker_batch_size = 5
direct_trigger_max_workloads = 50
directly_triggered_workload_duration = 5

