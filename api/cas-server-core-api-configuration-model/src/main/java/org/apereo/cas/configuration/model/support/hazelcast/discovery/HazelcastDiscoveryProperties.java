package org.apereo.cas.configuration.model.support.hazelcast.discovery;

import org.apereo.cas.configuration.support.RequiresModule;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * This is {@link HazelcastDiscoveryProperties}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RequiresModule(name = "cas-server-support-hazelcast-core")

@Getter
@Setter
public class HazelcastDiscoveryProperties implements Serializable {

    private static final long serialVersionUID = -8281223487171101795L;

    /**
     * Whether discovery should be enabled via the configured strategies below.
     */
    private boolean enabled;

    /**
     * Describe discovery strategy based on AWS.
     * The AWS config contains the configuration for AWS join mechanism.
     * What happens behind the scenes is that data about the running AWS instances in a specific region are downloaded using the
     * accesskey/secretkey and are potential Hazelcast members.
     * There are 2 mechanisms for filtering out AWS instances and these mechanisms can be combined (AND).
     * <ol>
     * <li>If a security group is configured, only instances within that security group are selected.</li>
     * <li>If a tag key/value is set, only instances with that tag key/value will be selected.</li>
     * </ol>
     * Once Hazelcast has figured out which instances are available, it will use the private IP addresses of these
     * instances to create a TCP/IP-cluster.
     */
    private HazelcastAwsDiscoveryProperties aws = new HazelcastAwsDiscoveryProperties();

    /**
     * Describe discovery strategy based on JClouds.
     */
    private HazelcastJCloudsDiscoveryProperties jclouds = new HazelcastJCloudsDiscoveryProperties();

    /**
     * Describe discovery strategy based on Azure.
     */
    private HazelcastAzureDiscoveryProperties azure = new HazelcastAzureDiscoveryProperties();
}
