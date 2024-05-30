TOKEN=token-01
CLUSTER_STATE=new
NAME_1=${HOSTNAME}
HOST_1=$(ip --brief address show eth0 | awk '{print $3}' | cut -d'/' -f1)
CLUSTER=${NAME_1}=http://${HOST_1}:2380

# For machine 1
THIS_NAME=${NAME_1}
THIS_IP=${HOST_1}
etcd --data-dir=data.etcd --name ${THIS_NAME} \
	--initial-advertise-peer-urls http://${THIS_IP}:2380 --listen-peer-urls http://${THIS_IP}:2380 \
	--advertise-client-urls http://${THIS_IP}:2379 --listen-client-urls http://${THIS_IP}:2379 \
	--initial-cluster ${CLUSTER} \
	--initial-cluster-state ${CLUSTER_STATE} --initial-cluster-token ${TOKEN}
