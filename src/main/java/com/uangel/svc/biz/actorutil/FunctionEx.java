package com.uangel.svc.biz.actorutil;

@FunctionalInterface
public interface FunctionEx<T, R> {
    R apply(T t);
}
