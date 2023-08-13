package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class CursoAwsCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        VpcStack vpc = new VpcStack(app, "VpcStack");
        ClusterStack cluster = new ClusterStack(app, "Cluster", vpc.getVpc());
        cluster.addDependency(vpc);

        RdsStack rdsStack = new RdsStack(app, "Rds", vpc.getVpc());
        rdsStack.addDependency(vpc);

        SnsStack snsStack = new SnsStack(app, "Sns");

        InvoiceAppStack invoiceAppStack = new InvoiceAppStack(app, "InvoiceApp");

        Service01Stack service01Stack = new Service01Stack(app, "Service01", cluster.getCluster(),
                snsStack.getProductEventsTopic(), invoiceAppStack.getBucket(), invoiceAppStack.getS3InvoiceQueue());
        service01Stack.addDependency(cluster);
        service01Stack.addDependency(rdsStack);
        service01Stack.addDependency(snsStack);
        service01Stack.addDependency(invoiceAppStack);

        DdbStack ddbStack = new DdbStack(app, "Ddb");

        Service02Stack service02Stack = new Service02Stack(app, "Service02", cluster.getCluster(), snsStack.getProductEventsTopic(), ddbStack.getProductEventsDb());
        service02Stack.addDependency(cluster);
        service02Stack.addDependency(snsStack);
        service02Stack.addDependency(ddbStack);

        app.synth();
    }
}

