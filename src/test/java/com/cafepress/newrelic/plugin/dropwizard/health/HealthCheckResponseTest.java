package com.cafepress.newrelic.plugin.dropwizard.health;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckResponseTest {
    @Mock private HealthCheckResult result;

    @Test
    public void shouldReportUnhealthyForAllCodesOtherThan200() throws Exception {
        for (int httpStatusCode = 100; httpStatusCode < 600; httpStatusCode++) {
            if (httpStatusCode == 200) {
                assertThat(HealthCheckResponse.from(httpStatusCode, result).isHealthy(), is(true));
                continue;
            }

            assertThat(HealthCheckResponse.from(httpStatusCode, result).isHealthy(), is(false));
        }
    }

    @Test
    public void shouldReportServerResponseWhenStatusCodeGiven() {
        assertThat(HealthCheckResponse.from(200, result).serverResponded(), is(true));
        assertThat(HealthCheckResponse.from(500, result).serverResponded(), is(true));
    }

    @Test
    public void shouldReportNoServerResponseForErrorResponse() throws Exception {
        assertThat(HealthCheckResponse.error().serverResponded(), is(false));
    }

    @Test
    public void shouldReturnUnderlyingResult() throws Exception {
        assertThat(HealthCheckResponse.from(123, result).getResult(), is(result));
    }
}