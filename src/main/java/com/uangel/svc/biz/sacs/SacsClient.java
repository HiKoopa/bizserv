package com.uangel.svc.biz.sacs;

import java.util.concurrent.CompletableFuture;

public interface SacsClient {
    CompletableFuture<JobResp> CreateJob(JobReq jobReq);
}
