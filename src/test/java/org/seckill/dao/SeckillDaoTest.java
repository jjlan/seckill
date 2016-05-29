package org.seckill.dao;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合，junit启动时加载springIOC容器
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:spring/spring-dao.xml"})
public class SeckillDaoTest {


    @Test
    public void reduceNumber() throws Exception {
        Date killTIme=new Date();
        int updateCount =seckillDao.reduceNumber(1000L,killTIme);
        System.out.println(updateCount);


    }
    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;
    @Test
    public void queryById() throws Exception {
        long id=1001;
        Seckill seckill=seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void queryAll() throws Exception {
        //java没有保存形参的记录，queryAll(int offeset,int limit)->queryAll(arg0,arg1),通过@param注解来解决

        List<Seckill> seckills=seckillDao.queryAll(0,100);
        for(Seckill seckill:seckills){
            System.out.println(seckill);
        }
    }

}