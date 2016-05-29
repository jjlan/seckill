package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by TS on 2016/5/22.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillServiceTest {
    private final Logger logger=LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSerkillList() throws Exception {
        List<Seckill> list=seckillService.getSerkillList();
       System.out.println(list);

    }

    @Test
    public void getById() throws Exception {
        long id=1000;
        Seckill seckill=seckillService.getById(id);
        System.out.println(seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id=1000;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        System.out.println(exposer);

    }

    @Test
    public void executeSeckill() throws Exception {
        long id=1000;
        long phone=13502171122L;
        String md5="e83eef2cc6b033ca0848878afc20e80d";
        SeckillExecution execution=seckillService.executeSeckill(id,phone,md5);
        System.out.println(execution);
    }
    //测试代码完整逻辑，注意可重复执行
    @Test
    public void testSeckillLogic() throws Exception{
        long id=1001;
        Exposer exposer=seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
           // logger.info("exposer={}",exposer);
            long phone=13502171122L;
            String md5="e83eef2cc6b033ca0848878afc20e80d";
            try{
                SeckillExecution execution=seckillService.executeSeckill(id,phone,md5);
             //   logger.info("result={}",execution);
            }catch (RepeatKillException e){
              //  logger.error(e.getMessage());
            }catch (SeckillCloseException e){
               // logger.error(e.getMessage());
            }
        }else {
            //秒杀未开启
          //  logger.warn("exposer={}",exposer);
        }

    }
}