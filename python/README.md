# AMQP OpenShift python demo clients

This repo contains the source for docker images and OpenShift templates for creating AMQP consumers
and producers.

To build the image:

    docker build -t clients .

To create a producer:

    oc process -f producer.yaml NAME=myproducer MESSAGING_URL=amqps://localhost:5672/myaddress RATE=10 | oc create -f -

To create a consumer:

    oc process -f consumer.yaml NAME=myconsumer MESSAGING_URL=amqps://localhost:5672/myaddress | oc create -f -
