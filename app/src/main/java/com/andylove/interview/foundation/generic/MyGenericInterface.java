package com.andylove.interview.foundation.generic;

import java.util.Set;

/**
 * 接口类型参数化类
 * @param <T, E>
 */
public interface MyGenericInterface<T, E> {

    public void addT(T t);
    public void addE(E e);
    public Set<T> makeSet();

}
