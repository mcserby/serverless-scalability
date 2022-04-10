project = "accesa-playground"
region  = "europe-west3"
credentials = "c:/credentials/google/sa.json"

cloud_function_bucket = "playground-cf"
cloud_functions_version = "0.0.9"

# simple ETL
simple_etl_trigger_topic = "simple-etl-trigger-topic"
simple_etl_schedule = "*/5 * * * *"
simple_etl_workload_duration = 30

# manager direct call ETL
manager_direct_call_trigger_topic = "manager-direct-call-trigger-topic"
etl_worker_topic =  "etl-worker-topic"
manager_direct_call_etl_schedule = "0 * * * *"
worker_batch_size = 5
max_workloads = 100
directly_triggered_workload_duration = 10

# manager with working pub sub QUEUE and PULL
manager_workload_queue_trigger_topic = "manager_workload_queue_trigger_topic"
manager_batch_size = 5
worker_trigger_topic = "worker_trigger_topic"
etl_pulling_worker_topic = "etl_pulling_worker_topic"
manager_workload_queue_trigger_etl_schedule = "0 * * * *"
worker_trigger_etl_schedule = "*/10 * * * *"
pulling_worker_batch_size = 2

