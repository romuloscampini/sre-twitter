#!/bin/bash

function load_twitter_credentials() {
  TWITTER_VAR_LIST=("__TWITTER_CONSUMER_KEY__" "__TWITTER_CONSUMER_SECRET__" "__TWITTER_ACCESS_TOKEN__" "__TWITTER_ACCESS_TOKEN_SECRET__")
  if [ ! -f "./api/container.env" ]; then
    echo "Twitter API Credentials..."
    cp -Rp ./api/example.env ./api/container.env
    for VAR_NAME in "${TWITTER_VAR_LIST[@]}"; do
      VAR_NAME_TO_ASK=$(echo "$VAR_NAME" | awk -F '__' '{print $2}')
      read -p "$VAR_NAME_TO_ASK: " NEW_VALUE
      sed -i 0 "s/${VAR_NAME}/${NEW_VALUE}/g" ./api/container.env
    done
  fi
}

function load_infrastructure() {
  echo "Checking infrastructure..."
  CONTAINER_LIST=$(docker-compose ps -q | wc -l)
  if [[ "$CONTAINER_LIST" -lt 1 ]]; then
    docker-compose up -d
  fi
  echo "OK"
}

function load_kibana_config_index_on_elastic() {
  if [ ! -f ".sre_kibana_index_imported_ok" ]; then
    echo "Loading Kibana configurations..."
    sleep 2m
    docker run --rm -ti -v $(pwd)/monitoring/kibana/import/kibana_1.json:/tmp/kibana.json elasticdump/elasticsearch-dump \
      --input=/tmp/kibana.json \
      --output=http://host.docker.internal:9200/.kibana_1 &> /dev/null

    docker run --rm -ti -v $(pwd)/monitoring/kibana/import/kibana_task_manager_1.json:/tmp/kibana.json elasticdump/elasticsearch-dump \
      --input=/tmp/kibana.json \
      --output=http://host.docker.internal:9200/.kibana_task_manager_1 &> /dev/null

    touch .sre_kibana_index_imported_ok
    echo "OK"
  fi
}

function load_initial_hashtags() {
  if [ ! -f ".sre_api_initial_hashtags" ]; then
    echo "Loading Initial hashtags..."
    sleep 30s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23openbanking'
    sleep 1s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23remediation'
    sleep 1s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23devops'
    sleep 1s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23sre'
    sleep 1s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23microservices'
    sleep 1s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23observability'
    sleep 1s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23oauth'
    sleep 1s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23metrics'
    sleep 1s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23logmonitoring'
    sleep 1s
    curl -XPOST 'http://localhost:8080/tweet/collector?hashtag=%23opentracing'

    touch .sre_api_initial_hashtags
    echo "OK"
  fi
}

function main() {
  load_twitter_credentials
  load_infrastructure
  load_kibana_config_index_on_elastic
  load_initial_hashtags
}

main
