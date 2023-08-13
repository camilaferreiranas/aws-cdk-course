package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.dynamodb.*;
import software.constructs.Construct;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class DdbStack extends Stack {

    private final Table productEventsDb;
    public DdbStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public DdbStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);


        productEventsDb = Table.Builder.create(this, "ProductEventDb")
                .tableName("product-events")
                .billingMode(BillingMode.PROVISIONED)
                .readCapacity(1)
                .writeCapacity(1)
                .partitionKey(
                        Attribute.builder()
                                .name("pk")
                                .type(AttributeType.STRING)
                                .build()
                )
                .sortKey(Attribute.builder()
                        .name("ak")
                        .type(AttributeType.STRING)
                        .build())
                .timeToLiveAttribute("ttl")
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        productEventsDb.autoScaleReadCapacity(EnableScalingProps.builder()
                        .minCapacity(1)
                        .maxCapacity(4)
                .build()
        ).scaleOnUtilization(UtilizationScalingProps.builder()
                        .targetUtilizationPercent(50)
                        .scaleInCooldown(Duration.seconds(30))
                        .scaleOutCooldown(Duration.seconds(30))
                .build());

    }

    public Table getProductEventsDb() {
        return productEventsDb;
    }
}
