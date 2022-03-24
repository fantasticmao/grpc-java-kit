package cn.fantasticmao.grpckit.support;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Keep a value reference like {@link java.util.concurrent.atomic.AtomicReference}, and it is <b>not thread safe</b>.
 *
 * @author fantasticmao
 * @version 1.39.0
 * @since 2022-03-24
 */
@NotThreadSafe
public class ValRef<T> {
    @Nonnull
    public T value;

    public ValRef(@Nonnull T value) {
        this.value = value;
    }
}
