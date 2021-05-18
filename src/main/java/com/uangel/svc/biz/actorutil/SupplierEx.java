package com.uangel.svc.biz.actorutil;

@FunctionalInterface
public interface SupplierEx<T> {
    T get() throws Throwable;
}
