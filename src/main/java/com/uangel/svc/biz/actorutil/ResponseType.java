package com.uangel.svc.biz.actorutil;

import akka.actor.ActorRef;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.Patterns.ask;

public interface ResponseType<T> {
    // type safe 하게 response 를 보낼 수 있는 helper 함수
    default void sendResponse(ActorRef pid , T response, ActorRef sender) {
        pid.tell(CompletableFuture.completedFuture(response), sender);
    }

    default void sendResponse(ActorRef pid , Throwable err, ActorRef sender) {
        pid.tell(CompletableFuture.failedFuture(err), sender);
    }

    // type safe 하게  future response 를 보낼 수 있는 helper 함수
    default void sendFutureResponse(ActorRef pid , CompletableFuture<T> future, ActorRef sender) {
        pid.tell(future, sender);
    }


    // type safe 하게 request 를 보낼 수 있는 helper함수
    static <T>CompletableFuture<T> askFor(ActorRef pid , ResponseType<T> request, Duration timeout) {
        return ask(pid , request, timeout).toCompletableFuture().thenCompose(x -> {
            return (CompletableFuture<T>)x;
        });
    }
}
