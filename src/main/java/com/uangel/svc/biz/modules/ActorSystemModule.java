package com.uangel.svc.biz.modules;

import akka.actor.ActorSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class ActorSystemModule {

    @Bean(destroyMethod = "terminate")
    @Lazy
    public ActorSystem actorSystem() {
        return ActorSystem.create();
    }
}
