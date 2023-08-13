package com.myorg;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.rds.*;
import software.constructs.Construct;

import java.util.Collections;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class RdsStack extends Stack {

    private final int PORT_MYSQL = 3306;
    public RdsStack(final Construct scope, final String id, Vpc vpc) {
        this(scope, id, null, vpc);
    }

    public RdsStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        super(scope, id, props);

        CfnParameter databasePassword = CfnParameter.Builder.create(this, "databasePassword")
                .type("String")
                .description("The RDS instance password")
                .build();
        ISecurityGroup iSecurityGroup = SecurityGroup.fromSecurityGroupId(this, id, vpc.getVpcDefaultSecurityGroup());
        iSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(PORT_MYSQL));

        DatabaseInstance.Builder databaseInstance = DatabaseInstance.Builder
                .create(this, "Rds01")
                .instanceIdentifier("aws-project01-db")
                .engine(DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder().version(MysqlEngineVersion.VER_5_7).build()))
                .vpc(vpc)
                .credentials(Credentials.fromUsername("admin",
                        CredentialsFromUsernameOptions.builder().password(SecretValue.plainText(databasePassword.getValueAsString())).build()))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .multiAz(false)
                .allocatedStorage(10)
                .securityGroups(Collections.singletonList(iSecurityGroup))
                .vpcSubnets(SubnetSelection.builder()
                        .subnets(vpc.getPublicSubnets())
                        .build());

        CfnOutput.Builder.create(this, "rds-endpoint")
                .exportName("rds-endpoint")
                .value(databaseInstance.build().getDbInstanceEndpointAddress())
                .build();

        CfnOutput.Builder.create(this, "rds-password")
                .exportName("rds-password")
                .value(databasePassword.getValueAsString())
                .build();
    }
}
