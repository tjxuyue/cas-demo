package org.apereo.cas.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * This is {@link ChainingAWSCredentialsProviderTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
public class ChainingAWSCredentialsProviderTests {

    static {
        System.setProperty("aws.accessKeyId", "AKIAIPPIGGUNIO74C63Z");
        System.setProperty("aws.secretKey", "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");
    }

    @Test
    public void verifyInstance() {
        final ChainingAWSCredentialsProvider p = (ChainingAWSCredentialsProvider) ChainingAWSCredentialsProvider.getInstance("accesskey", "secretKey",
            new FileSystemResource("credentials.properties"), "profilePath", "profileName");
        assertFalse(p.getChain().isEmpty());
        final AWSCredentials credentials = p.getCredentials();
        assertNotNull(credentials);
        assertTrue(credentials instanceof BasicAWSCredentials);
    }
}
