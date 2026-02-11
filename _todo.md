check services are healthy
add implicit image versions

podman machine start

podman compose --file compose.yaml down
podman compose down --remove-orphans
podman compose --file compose.yaml build
podman compose --file compose.yaml up

podman compose --file compose.yaml up resource-db song-db localstack kafka

podman compose logs -f resource-processor
podman compose logs -f resource-service
podman compose logs -f song-service
podman compose logs -f eureka-server
podman compose logs -f kafka


podman ps --format "table {{.Names}}\t{{.Status}}"


      test: [ "CMD-SHELL", "curl -s http://localhost:4566/_localstack/health | grep '\"s3\": \"running\"' && awslocal s3 ls | grep resource || exit 1" ]

      test: ["CMD", "curl", "-f", "http://localhost:4566/_localstack/health"]


