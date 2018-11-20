package org.apereo.cas.support.saml.metadata.resolver;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import org.apache.http.client.methods.HttpGet;
import org.apereo.cas.configuration.model.support.saml.idp.SamlIdPProperties;
import org.apereo.cas.support.saml.OpenSamlConfigBean;
import org.apereo.cas.support.saml.services.SamlRegisteredService;
import org.apereo.cas.util.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link AmazonS3SamlRegisteredServiceMetadataResolverTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RefreshAutoConfiguration.class)
public class AmazonS3SamlRegisteredServiceMetadataResolverTests {

    @Test
    public void verifyAction() throws Exception {
        final AmazonS3Client client = mock(AmazonS3Client.class);
        final ListObjectsV2Result result = new ListObjectsV2Result();
        final S3ObjectSummary summary = new S3ObjectSummary();
        summary.setBucketName("CAS");
        summary.setSize(1000);
        summary.setKey("SAML-Document.xml");
        result.getObjectSummaries().add(summary);
        result.setBucketName("CAS");
        when(client.listObjectsV2(anyString())).thenReturn(result);

        final S3Object object = new S3Object();
        object.setBucketName("CAS");
        object.setKey("SAML-Document.xml");

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setUserMetadata(CollectionUtils.wrap("signature",
            "MIICNTCCAZ6gAwIBAgIES343gjANBgkqhkiG9w0BAQUFADBVMQswCQYDVQQGEwJVUzELMAkGA1UE"
                + "CAwCQ0ExFjAUBgNVBAcMDU1vdW50YWluIFZpZXcxDTALBgNVBAoMBFdTTzIxEjAQBgNVBAMMCWxv"
                + "Y2FsaG9zdDAeFw0xMDAyMTkwNzAyMjZaFw0zNTAyMTMwNzAyMjZaMFUxCzAJBgNVBAYTAlVTMQsw"
                + "CQYDVQQIDAJDQTEWMBQGA1UEBwwNTW91bnRhaW4gVmlldzENMAsGA1UECgwEV1NPMjESMBAGA1UE"
                + "AwwJbG9jYWxob3N0MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUp/oV1vWc8/TkQSiAvTou"
                + "sMzOM4asB2iltr2QKozni5aVFu818MpOLZIr8LMnTzWllJvvaA5RAAdpbECb+48FjbBe0hseUdN5"
                + "HpwvnH/DW8ZccGvk53I6Orq7hLCv1ZHtuOCokghz/ATrhyPq+QktMfXnRS4HrKGJTzxaCcU7OQID"
                + "AQABoxIwEDAOBgNVHQ8BAf8EBAMCBPAwDQYJKoZIhvcNAQEFBQADgYEAW5wPR7cr1LAdq+IrR44i"
                + "QlRG5ITCZXY9hI0PygLP2rHANh+PYfTmxbuOnykNGyhM6FjFLbW2uZHQTY1jMrPprjOrmyK5sjJR"
                + "O4d1DeGHT/YnIjs9JogRKv4XHECwLtIVdAbIdWHEtVZJyMSktcyysFcvuhPQK8Qc/E/Wq8uHSCo="));
        object.setObjectMetadata(metadata);

        object.setObjectContent(new S3ObjectInputStream(new ClassPathResource("sp-metadata.xml").getInputStream(), new HttpGet()));
        when(client.getObject(anyString(), anyString())).thenReturn(object);

        final SamlIdPProperties properties = new SamlIdPProperties();
        properties.getMetadata().getAmazonS3().setBucketName("CAS");

        final BasicParserPool parserPool = new BasicParserPool();
        parserPool.initialize();
        final OpenSamlConfigBean configBean = new OpenSamlConfigBean(parserPool);
        assertNotNull(configBean.getUnmarshallerFactory());
        assertNotNull(configBean.getBuilderFactory());
        assertNotNull(configBean.getMarshallerFactory());
        assertNotNull(configBean.getParserPool());

        final AmazonS3SamlRegisteredServiceMetadataResolver r = new AmazonS3SamlRegisteredServiceMetadataResolver(
            properties, configBean, client);

        final SamlRegisteredService service = new SamlRegisteredService();
        service.setName("SAML");
        service.setId(100);
        assertFalse(r.resolve(service).isEmpty());
    }
}
