package com.uangel.svc.biz.actorutil;

import akka.actor.ActorRef;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.Patterns.ask;

public interface ResponseType<T> {
    // type safe 하게 response 를 보낼 수 있는 helper 함수
    default void sendResponse(ActorRef receiver , T response, ActorRef sender) {
        receiver.tell(CompletableFuture.completedFuture(response), sender);
    }

    default void sendResponse(ActorRef receiver , Throwable err, ActorRef sender) {
        receiver.tell(CompletableFuture.failedFuture(err), sender);
    }

    // type safe 하게  future response 를 보낼 수 있는 helper 함수
    default void sendFutureResponse(ActorRef receiver , CompletableFuture<T> future, ActorRef sender) {
        receiver.tell(future, sender);
    }


    // type safe 하게 request 를 보낼 수 있는 helper함수
    @SuppressWarnings("unchecked")
    static <T>CompletableFuture<T> askFor(ActorRef pid , ResponseType<T> request, Duration timeout) {
        return ask(pid , request, timeout).toCompletableFuture().thenCompose(x -> {
            return (CompletableFuture<T>)x;
        });
    }
}
