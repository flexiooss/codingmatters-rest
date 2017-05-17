package org.codingmatters.poomjobs.types.api;

import java.util.function.Function;

/**
 * Created by nelt on 4/25/17.
 */
public interface PoomjobsAPIHandlers {
    Function<JobCollectionPostRequest, JobCollectionPostResponse> jobCollectionPostHandler();
    Function<JobResourceGetRequest, JobResourceGetResponse> jobResourceGetHandler();
    Function<JobResourcePutRequest, JobResourcePutResponse> jobResourcePutHandler();

    class Builder {
        static public Builder builder() {
            return new Builder();
        }

        private Function<JobCollectionPostRequest, JobCollectionPostResponse> jobCollectionPostHandler;
        private Function<JobResourceGetRequest, JobResourceGetResponse> jobResourceGetHandler;
        private Function<JobResourcePutRequest, JobResourcePutResponse> jobResourcePutHandler;

        public Builder jobCollectionPostHandler(Function<JobCollectionPostRequest, JobCollectionPostResponse> handler) {
            this.jobCollectionPostHandler = handler;
            return this;
        }
        public Builder jobResourceGetHandler(Function<JobResourceGetRequest, JobResourceGetResponse> handler) {
            this.jobResourceGetHandler = handler;
            return this;
        }
        public Builder jobResourcePutHandler(Function<JobResourcePutRequest, JobResourcePutResponse> handler) {
            this.jobResourcePutHandler = handler;
            return this;
        }

        public PoomjobsAPIHandlers build() {
            return new DefaultImpl(
                    this.jobCollectionPostHandler,
                    this.jobResourceGetHandler,
                    this.jobResourcePutHandler
            );
        }

        static private class DefaultImpl implements PoomjobsAPIHandlers {
            private final Function<JobCollectionPostRequest, JobCollectionPostResponse> jobCollectionPostHandler;
            private final Function<JobResourceGetRequest, JobResourceGetResponse> jobResourceGetHandler;
            private final Function<JobResourcePutRequest, JobResourcePutResponse> jobResourcePutHandler;

            private DefaultImpl(Function<JobCollectionPostRequest, JobCollectionPostResponse> jobCollectionPostHandler, Function<JobResourceGetRequest, JobResourceGetResponse> jobResourceGetHandler, Function<JobResourcePutRequest, JobResourcePutResponse> jobResourcePutHandler) {
                this.jobCollectionPostHandler = jobCollectionPostHandler;
                this.jobResourceGetHandler = jobResourceGetHandler;
                this.jobResourcePutHandler = jobResourcePutHandler;
            }

            @Override
            public Function<JobCollectionPostRequest, JobCollectionPostResponse> jobCollectionPostHandler() {
                return this.jobCollectionPostHandler;
            }

            @Override
            public Function<JobResourceGetRequest, JobResourceGetResponse> jobResourceGetHandler() {
                return this.jobResourceGetHandler;
            }

            @Override
            public Function<JobResourcePutRequest, JobResourcePutResponse> jobResourcePutHandler() {
                return this.jobResourcePutHandler;
            }
        }
    }
}
