package org.seckill.exception;

import org.seckill.dto.SeckillExecution;

/**
 * 秒杀关闭异常
 * Created by TS on 2016/5/20.
 */
public class SeckillCloseException extends SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
