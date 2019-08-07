
mvn clean package

docker build -t aggarwalmayank/compute-image:latest .

docker push aggarwalmayank/compute-image:latest

kubectl --namespace default create secret generic comp-sa-creds --from-file=key.json=key.json

kubectl create -f deployment.yaml

kubectl expose deployment compute-deployment --type LoadBalancer --port 80 --target-port 8080


Compute Endpoints

curl --header "project-id: my-kubernetes-248208" --header "zone: europe-west2-a" http://35.239.78.165/app/instances

Datastore Endpoints

curl http://35.239.78.165/app/task/list

curl http://35.239.78.165/app/task/add

curl http://35.239.78.165/app/datastore/properties


Cloud Storage Endpoints

curl http://35.239.78.165/app/storage/properties

curl http://35.239.78.165/app/storage/bucket/my-buck-26111986