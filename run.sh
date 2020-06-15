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
    sleep 1m
    KIBANA_STATUS=$(curl -o /dev/null -s -w "%{http_code}\n" http://localhost:5601)
    while [[ $KIBANA_STATUS != 302 ]]; do
      KIBANA_STATUS=$(curl -o /dev/null -s -w "%{http_code}\n" http://localhost:5601);
    done
    docker run --rm -ti -v $(pwd)/monitoring/kibana/import/kibana_1.json:/tmp/kibana.json elasticdump/elasticsearch-dump \
      --input=/tmp/kibana.json \
      --output=http://host.docker.internal:9200/.kibana_1

    docker run --rm -ti -v $(pwd)/monitoring/kibana/import/kibana_task_manager_1.json:/tmp/kibana.json elasticdump/elasticsearch-dump \
      --input=/tmp/kibana.json \
      --output=http://host.docker.internal:9200/.kibana_task_manager_1

    touch .sre_kibana_index_imported_ok
    echo "OK"
  fi
}

function load_initial_hashtags() {
  if [ ! -f ".sre_api_initial_hashtags" ]; then
    echo "Loading Initial hashtags..."
    sleep 30s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23openbanking"
    sleep 1s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23remediation"
    sleep 1s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23devops"
    sleep 1s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23sre"
    sleep 1s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23microservices"
    sleep 1s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23observability"
    sleep 1s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23oauth"
    sleep 1s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23metrics"
    sleep 1s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23logmonitoring"
    sleep 1s
    curl -X POST "http://localhost:8080/tweet/collector" -d "hashtag=%23opentracing"
    touch .sre_api_initial_hashtags
    echo "OK"
  fi
}

function create() {
  load_twitter_credentials
  load_infrastructure
  load_kibana_config_index_on_elastic
  load_initial_hashtags
}

function destroy() {
  rm -f .sre_api_initial_hashtags
  rm -f .sre_kibana_index_imported_ok
  docker-compose down
}


function main(){
  echo "SRE - Case Twitter"
  echo -e "##################\n"
  echo "Opções de execução: "
  echo -e "\t 1. Criar ambiente"
  echo -e "\t 2. Apagar ambiente"
  echo -e "\t 0. Sair"
  read -p "Informe a opção desejada [ENTER: Opção 1]: " op
  case $op in
    2)
      destroy;;
    0)
      exit;;
    *)
      create;;
  esac
}


main
