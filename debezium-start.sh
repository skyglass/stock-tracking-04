#!/bin/bash

echo "Creating debezium connectors"

curl --location --request POST 'localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "order-payment-connector",
  "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "stock-postgres",
      "database.port": "5433",
      "database.user": "postgres",
      "database.password": "postgres",
      "database.dbname" : "postgres",
      "database.server.name": "PostgreSQL-15",
      "table.include.list": "order.payment_outbox",
      "topic.prefix": "debezium",
      "tombstones.on.delete" : "false",
      "slot.name" : "order_payment_outbox_slot",
      "plugin.name": "pgoutput",
      "auto.create.topics.enable": false,
      "auto.register.schemas": false,
      "skipped.operations": "u,d,t"
      }
 }'

curl --location --request POST 'localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--data-raw '{
  "name": "payment-order-connector",
  "config": {
      "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
      "tasks.max": "1",
      "database.hostname": "stock-postgres",
      "database.port": "5433",
      "database.user": "postgres",
      "database.password": "postgres",
      "database.dbname" : "postgres",
      "database.server.name": "PostgreSQL-15",
      "table.include.list": "payment.order_outbox",
      "topic.prefix": "debezium",
      "tombstones.on.delete" : "false",
      "slot.name" : "payment_order_outbox_slot",
      "plugin.name": "pgoutput",
      "auto.create.topics.enable": false,
      "auto.register.schemas": false,
      "skipped.operations": "u,d,t"
      }
 }'

 curl --location --request POST 'localhost:8083/connectors' \
 --header 'Content-Type: application/json' \
 --data-raw '{
   "name": "payment-deposit-order-connector",
   "config": {
       "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
       "tasks.max": "1",
       "database.hostname": "stock-postgres",
       "database.port": "5433",
       "database.user": "postgres",
       "database.password": "postgres",
       "database.dbname" : "postgres",
       "database.server.name": "PostgreSQL-15",
       "table.include.list": "order.payment_deposit_outbox",
       "topic.prefix": "debezium",
       "tombstones.on.delete" : "false",
       "slot.name" : "order_payment_deposit_outbox_slot",
       "plugin.name": "pgoutput",
       "auto.create.topics.enable": false,
       "auto.register.schemas": false,
       "skipped.operations": "u,d,t"
       }
  }'

echo "Start-up completed"