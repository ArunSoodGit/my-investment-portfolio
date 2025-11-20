package com.sood.transaction;

import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.core.SingleEmitter;

public class GrpcSingleObserver<T> implements StreamObserver<T> {

    private final SingleEmitter<T> emitter;

    public GrpcSingleObserver(final SingleEmitter<T> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onNext(final T value) {
        if (!emitter.isDisposed()) {
            emitter.onSuccess(value);
        }
    }

    @Override
    public void onError(final Throwable t) {
        if (!emitter.isDisposed()) {
            emitter.onError(t);
        }
    }

    @Override
    public void onCompleted() {
    }
}
