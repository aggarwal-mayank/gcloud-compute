
mvn clean package

docker build -t aggarwalmayank/compute-image:latest .

docker push aggarwalmayank/compute-image

curl --header "project-id: my-kubernetes-248208" --header "zone: europe-west2-a" http://35.246.13.61/app/instances

kubectl --namespace default create secret generic comp-sa-creds --from-file=key.json=key.json

kubectl create -f deployment.yaml

kubectl expose deployment compute-deployment --type LoadBalancer --port 80 --target-port 8080