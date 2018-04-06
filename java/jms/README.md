# To build locally

    mvn clean install

# To build and deploy to OpenShift

    mvn -Dfabric8.mode=openshift package fabric8:build
    mvn fabric8:resource fabric8:deploy
