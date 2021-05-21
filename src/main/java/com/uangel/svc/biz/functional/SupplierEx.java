package com.uangel.svc.biz.functional;

@FunctionalInterface
public interface SupplierEx<T> {
    T get() throws Throwable;
}
