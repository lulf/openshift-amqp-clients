# Provision Address Space

In the OpenShift Service Catalog overview, select either of "EnMasse (standard)" or "EnMasse
(brokered)". Select among the available plans. If you have an OpenShift project on this cluster
already, select it in the "Add to Project" field. If not, select the "Create Project" in the
drop-down box.

Use the same value for the "name" field. The address space will be provisioned and may take a few
minutes. In the meantime, you can go to the next step of deploying the example application.

# Deploying example application

This tutorial provides 2 java-based example applications (but any client compliant with the AMQP 1.0
standard should work). One client is based on Vert.x and the other on JMS. You can edit the examples
to change the messaging address to use (hardcoded to 'myqueue').

The examples are built using maven and deployed using the Fabric8 Maven Plugin.

*NOTE* First make sure you are logged in to the OpenShift cluster and have current-project set to your project.

To deploy the examples, first go to the top level directory `java` and build everything:

    mvn install

To deploy either vert.x or JMS example, go to `vertx` or `jms` respectively and run the following commands:

    mvn -Dfabric8.mode=openshift package fabric8:build
    mvn fabric8:resource fabric8:deploy

The example application will now be built and deployed in your OpenShift project. 

For now, the examples will restart since they cannot detect any credentials, which we will fix in
the next step.

# Bind address space to app

Go to your project where the app is deployed. You should see your messaging service to be
provisioned and ready (if not, wait a little).

Create a binding. Select 'consoleAccess' and 'consoleAdmin' so that you can use the same credentials
to create the addresses in the messaging console. Once the binding is created, you will see new
secret created in your project.

Click on the secret and `reveal` to find the URL to the messaging console and credentials. Go to the
messaging console and enter the credentials you've been given. Click on the "Addresses" menu item
and create an address named `myqueue` of type `queue`.

Once the queue has been created, go back to the OpenShift console. Go to the secret that was created
and click "Add to application". This will allow you modify your application deployment to mount the
secret so that the example application can use it. Select the option to mount it and enter
'/etc/app-credentials' as the mount point.

Once the secret has been added to the deployment, a new version of your app will be deployed, and
the clients should start to send and receive messages. You can confirm that it is working by looking
at the logs for the example application pod.

# Summary

In this tutorial, you've seen how to provision messaging using the OpenShift Service Catalog. You
have seen how to deploy an example messaging application and how to bind it to the provisioned
messaging service. You then used the messaging console to create an address before modifying the
application to use the secret for authenticating.
