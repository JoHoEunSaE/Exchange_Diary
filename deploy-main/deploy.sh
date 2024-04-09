#!/bin/bash

cd /home/ubuntu

if [ ! -d "/home/ubuntu/server-config" ]; then
  git clone git@github.com:JoHoEunSaE/BackendConfig.git server-config
fi

cd server-config
git pull origin main

mkdir -p /home/ubuntu/deploy/zip
cd /home/ubuntu/deploy/zip/

docker compose down --rmi all
docker compose pull
docker compose up -d
