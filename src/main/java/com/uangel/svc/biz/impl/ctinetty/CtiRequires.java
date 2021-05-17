package com.uangel.svc.biz.impl.ctinetty;

import akka.actor.ActorSystem;

import javax.inject.Inject;

public class CtiRequires {
    private ActorSystem actorSystem;

    @Inject
    public CtiRequires(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }
}
