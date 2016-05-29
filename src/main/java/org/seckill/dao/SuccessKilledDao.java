package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Created by TS on 2016/5/11.
 */
public interface SuccessKilledDao {
    //插入购买明细，可过滤重复
   int insertSuccessKilled(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);
    //根据id查询successKilled并携带秒杀对象实体
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);
}
