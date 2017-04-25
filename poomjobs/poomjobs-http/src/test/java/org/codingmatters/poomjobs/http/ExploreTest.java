package org.codingmatters.poomjobs.http;

import org.codingmatters.poomjobs.types.api.JobResourceGetRequest;
import org.codingmatters.poomjobs.types.api.JobResourceGetResponse;
import org.codingmatters.poomjobs.types.api.jobresourcegetresponse.Status200;
import org.codingmatters.poomjobs.types.types.Job;
import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by nelt on 4/25/17.
 */
public class ExploreTest {

    @Test
    public void handlerFunction() throws Exception {
        JobResourceGetRequest req = JobResourceGetRequest.Builder.builder()
                .jobId("123456789")
                .build();
        JobResourceGetResponse.Builder.builder()
                .status200(Status200.Builder.builder()
                        .payload(Job.Builder.builder()
                                .version(12)
                                .build())
                        .build())
                .build();

        Function<JobResourceGetRequest, JobResourceGetResponse> requestHandler =
                request -> JobResourceGetResponse.Builder.builder()
                        .status200(Status200.Builder.builder()
                                .payload(Job.Builder.builder()
                                        .version(12)
                                        .result(request.jobId())
                                        .build())
                                .build())
                        .build();

        JobResourceGetResponse response = requestHandler.apply(req);

        assertThat(response.status200().payload().result(), is("123456789"));
        assertThat(response.status404(), is(nullValue()));
        assertThat(response.status500(), is(nullValue()));
    }

}
