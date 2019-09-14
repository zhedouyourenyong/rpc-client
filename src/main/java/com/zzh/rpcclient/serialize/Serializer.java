package com.zzh.rpcclient.serialize;

import com.zzh.rpcclient.serialize.impl.ProtostuffSerializer;

public interface Serializer
{
    //默认使用的序列化算法
    Serializer DEFAULT = new ProtostuffSerializer();

    byte getSerializerAlgorithm ();
    <T> byte[] serialize (T obj);
    <T> T deserialize (Class<T> clazz, byte[] data);
}
