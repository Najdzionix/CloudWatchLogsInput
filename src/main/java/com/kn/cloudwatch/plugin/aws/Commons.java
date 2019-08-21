package com.kn.cloudwatch.plugin.aws;

import com.amazonaws.services.logs.model.AWSLogsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeExecutor;
import net.jodah.failsafe.RetryPolicy;

import java.io.IOException;
import java.time.Duration;

/**
 * Created by Kamil Nadłonek on 20-08-2019
 * email:kamilnadlonek@gmail.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Commons {

    private static final RetryPolicy<Object> retryPolicy = new RetryPolicy<>()
            .handle(IOException.class, AWSLogsException.class)
            .withDelay(Duration.ofSeconds(1))
            .withMaxRetries(3);

      static FailsafeExecutor<Object> retryPolicy() {
         return Failsafe.with(retryPolicy);
     }
}
