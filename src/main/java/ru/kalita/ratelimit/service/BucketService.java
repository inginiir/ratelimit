package ru.kalita.ratelimit.service;

import io.github.bucket4j.Bucket;

public interface BucketService {

    Bucket getBucketByIp(String ip);
}
