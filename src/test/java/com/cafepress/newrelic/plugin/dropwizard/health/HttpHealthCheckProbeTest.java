package com.cafepress.newrelic.plugin.dropwizard.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpHealthCheckProbeTest {
    @Mock private WebTarget adminTarget;
    @Mock private WebTarget healthCheckTarget;
    @Mock private ObjectMapper objectMapper;
    @Mock private Invocation.Builder requestBuilder;
    @Mock private Response httpResponse;
    @Mock private com.cafepress.newrelic.plugin.dropwizard.health.HealthCheckResult healthCheckResult;
    private HttpHealthCheckProbe probe;

    @Before
    public void setUp() throws Exception {
        when(adminTarget.path("/healthcheck")).thenReturn(healthCheckTarget);
        when(healthCheckTarget.getUri()).thenReturn(new URI("http://someserver:8081/healthcheck"));
        when(healthCheckTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(requestBuilder);
        when(requestBuilder.get()).thenReturn(httpResponse);
        when(httpResponse.readEntity(String.class)).thenReturn("{}");
        when(objectMapper.readValue("{}", HealthCheckResult.class)).thenReturn(healthCheckResult);

        probe = new HttpHealthCheckProbe(adminTarget, objectMapper);
    }

    @Test
    public void responseShouldContainHealthCheckResult() throws Exception {
        HealthCheckResponse response = probe.probe();
        assertThat(response.getResult(), is(healthCheckResult));
    }

    @Test
    public void responseShouldIncludeHttpCode() throws Exception {
        given(httpResponse.getStatus()).willReturn(123);
        HealthCheckResponse response = probe.probe();
        assertThat(response.getHttpCode(), is(123));
    }

    @Test
    public void shouldHandleExceptionWhileMakingHttpRequest() throws Exception {
        given(requestBuilder.get()).willThrow(new RuntimeException("test-generated-exception"));
        HealthCheckResponse response = probe.probe();
        assertThat(response.isHealthy(), is(false));
        assertThat(response.serverResponded(), is(false));
    }

    @Test
    public void shouldCloseJerseyResponse() throws Exception {
        probe.probe();
        verify(httpResponse).close();
    }
}