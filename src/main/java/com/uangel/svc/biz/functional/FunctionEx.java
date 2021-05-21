package com.uangel.svc.biz.functional;

@FunctionalInterface
public interface FunctionEx<T, R> {
    R apply(T t);
}
