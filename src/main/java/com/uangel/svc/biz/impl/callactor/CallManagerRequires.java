package com.uangel.svc.biz.impl.callactor;

import akka.actor.ActorSystem;
import com.uangel.svc.biz.cti.CtiClient;

import javax.inject.Inject;

public class CallManagerRequires {
    CtiClient ctiClient;

    @Inject
    public CallManagerRequires(CtiClient ctiClient) {
        this.ctiClient = ctiClient;
    }
}
