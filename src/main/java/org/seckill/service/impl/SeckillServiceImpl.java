package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TS on 2016/5/20.
 */
//spring 常用注解 @Component @Service @Dao @controller
    @Service
public class SeckillServiceImpl implements SeckillService{
    private Logger logger= LoggerFactory.getLogger(this.getClass());
    //注入service依赖
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private SuccessKilledDao successKilledDao;
    //MD5盐值字符串，用户混淆MD5
    private final String slat="sfsa=32q4r23234215ERWERT^**%^SDF";

    public List<Seckill> getSerkillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill =seckillDao.queryById(seckillId);
        if(seckill==null){
            return new Exposer(false,seckillId);
        }
        Date startTime=seckill.getStartTime();
        Date endTime=seckill.getEndTime();
        //系统当前时间
        Date nowTime=new Date();
        if(nowTime.getTime()<startTime.getTime()||nowTime.getTime()>endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }
        String md5=getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }
    private String getMD5(long seckillId){
        String base=seckillId+"/"+slat;
        String md5= DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
    @Transactional
    /**
     *使用注解控制事务方法的优点
     * 1：开发团队达成一致约定，明确标注事务方法的编程风格
     * 2：保证事务方法的执行时间尽可能短，不要穿插其他网络请求，RPC/HTTP请求或者剥离到事务方法外
     * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5==null ||!md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存+记录购买行为
        Date nowTime=new Date();
        try {
            //记录购买行为
            int insertCount=successKilledDao.insertSuccessKilled(seckillId,userPhone);
            //唯一：seckillId，userphone
            if(insertCount<=0){
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            }else{
                //减库存，热点商品竞争
                int updateCount=seckillDao.reduceNumber(seckillId,nowTime);
                if(updateCount<=0){
                    //没有更新到记录,秒杀结束 rollback
                    throw new SeckillCloseException("seckill is closed");
                }else{
                    //秒杀成功 commit
                    SuccessKilled successKilled=successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }
            }

        }catch(SeckillCloseException e1){
            throw e1;
        } catch (RepeatKillException e2){
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            //所有的编译期异常，转化为运行期异常(运行时异常，spring可以做rollback操作)
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }

    }
    //抛出异常是为了告诉spring是否rollback，此处使用存储过程的话，就不需要抛异常了
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if(md5 ==null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime=new Date();
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        //执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            int result=(Integer) map.get("result");
            if(result==1){
                SuccessKilled successKilled=successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
            }else{
                return new SeckillExecution(seckillId,SeckillStatEnum.stateof(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROE);
        }
    }
}
