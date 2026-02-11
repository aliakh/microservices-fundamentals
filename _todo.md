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
podman compose logs -f kafka
