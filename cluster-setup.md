# Cluster setup for OpenShift Service Catalog tutorial

## Prerequisites

Setting up the service catalog and installing the EnMasse service broker requires cluster-admin
privileges on the OpenShift cluster.


## Installing

To install, make a copy of the ansible/playbooks/openshift/multitenant-service-catalog.yaml playbook. Edit the keycloak_http_url so that it matches the keycloak route to the EnMasse Keycloak instance. (Typically https://keycloak-enmasse.example.com/auth).

Then run the playbook using `ansible-playbook`. EnMasse with the service broker will be installed and registered with the service catalog.

## Uninstalling

To uninstall the cluster, first delete address spaces that are actively in use in the project where EnMasse is deployed (`enmasse` by default):

    oc delete configmap -n enmasse -l type=address-space

This should mark the address space projects as Terminating. Once they are deleted, simply delete the project where EnMasse is deployed (`enmasse` by default).
