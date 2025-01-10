docker pull wurstmeister/zookeeper:latest
docker run -d \
  -p 2181:2181 \
  --name zookeeper \
  wurstmeister/zookeeper:latest

docker pull wurstmeister/kafka:latest

docker run -d -p 9092:9092 \
  -e KAFKA_BROKER_ID="001" \
  -e HOSTNAME_COMMAND="docker info | grep ^Name: | cut -d' ' -f 2" \
  -e KAFKA_ZOOKEEPER_CONNECT="zookeeper:2181" \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP="INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT" \
  -e KAFKA_ADVERTISED_LISTENERS="INSIDE://:9092,OUTSIDE://_{HOSTNAME_COMMAND}:9094" \
  -e KAFKA_LISTENERS="INSIDE://:9092,OUTSIDE://:9094" \
  -e KAFKA_INTER_BROKER_LISTENER_NAME="INSIDE" \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --link zookeeper:zookeeper \
  --name kafka \
  wurstmeister/kafka:latest