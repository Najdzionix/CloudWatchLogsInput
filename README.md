# CloudWatchLogsInput
Logstash input CloudWatch Logs plugin 
This is a Java plugin for [Logstash](https://github.com/elastic/logstash).

## Usage

### Parameters
| Parameter | Input Type | Required | Default |
|-----------|------------|----------|---------|
| log_group_name_prefix | string | Yes | |
| aws_credential_path | string | Yes | |
| tags | Array of strings | No | |
| interval | number | No | 30 |


#### `log_group_name_prefix`
The `log_group_name_prefix` is used to search log group as prefix. 