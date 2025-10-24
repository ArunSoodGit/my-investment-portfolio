#!/bin/bash

# Katalog bazowy projektu
BASE_DIR="/mnt/d/smart-reservation"
SERVICES=("api-server" "reservation-service" "notification-service" "analytics-service")
K8S_DIR="$BASE_DIR/k8s/base"
NAMESPACE="smart-reservation"

cd "$BASE_DIR" || { echo "Nie można wejść do katalogu $BASE_DIR"; exit 1; }

# 1️⃣ Budowanie obrazów Docker/nerdctl
for svc in "${SERVICES[@]}"; do
    echo "Budowanie obrazu $svc..."
    cd "$BASE_DIR/$svc" || { echo "Folder $svc nie istnieje!"; exit 1; }
    nerdctl build -t "$svc:latest" .
done

# 2️⃣ Załaduj obrazy do Rancher Desktop (containerd)
for svc in "${SERVICES[@]}"; do
    echo "Ładowanie obrazu $svc do Rancher Desktop..."
    nerdctl save "$svc:latest" | nerdctl --namespace k8s.io load
done

# 3️⃣ Tworzenie namespace jeśli nie istnieje
kubectl get namespace "$NAMESPACE" &>/dev/null || kubectl create namespace "$NAMESPACE"

# 4️⃣ Zastosowanie konfiguracji Kubernetes (Deployment + Service)
echo "Zastosowanie konfiguracji Kubernetes (Deployment + Service)..."
for svc in "${SERVICES[@]}"; do
    DEPLOYMENT_FILE="$K8S_DIR/$svc/deployment.yaml"
    SERVICE_FILE="$K8S_DIR/$svc/service.yaml"

    if [ -f "$DEPLOYMENT_FILE" ]; then
        kubectl apply -f "$DEPLOYMENT_FILE" -n "$NAMESPACE"
    else
        echo "Nie znaleziono pliku $DEPLOYMENT_FILE"
    fi

    if [ -f "$SERVICE_FILE" ]; then
        kubectl apply -f "$SERVICE_FILE" -n "$NAMESPACE"
    else
        echo "Nie znaleziono pliku $SERVICE_FILE"
    fi
done

# 5️⃣ Restart podów (aby wziąć nowe obrazy)
echo "Restartowanie podów w namespace $NAMESPACE..."
kubectl delete pods -n "$NAMESPACE" --all

# 6️⃣ Sprawdzenie statusu podów i usług
echo "Status podów:"
kubectl get pods -n "$NAMESPACE"

echo "Status usług:"
kubectl get svc -n "$NAMESPACE"

# 7️⃣ Wyświetlenie dostępnych URL-i dla NodePort
echo "Dostępne URL-e serwisów:"
for svc in "${SERVICES[@]}"; do
    NODE_PORT=$(kubectl get svc "$svc" -n "$NAMESPACE" -o jsonpath='{.spec.ports[0].nodePort}' 2>/dev/null)
    if [ -n "$NODE_PORT" ]; then
        echo "$svc -> http://localhost:$NODE_PORT"
    fi
done
