package org.seckill.enums;

/**
 * 使用枚举表示数据字段
 * Created by TS on 2016/5/21.
 */
public enum SeckillStatEnum{
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROE(-2,"系统异常"),
    DATA_REWRITE(-3,"数据篡改");
    private int state;
    private String stateInfo;

    SeckillStatEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }
    public static SeckillStatEnum stateof(int index){
        for(SeckillStatEnum state : values()){
            if(state.getState()==index){
                return state;
            }
        }
        return null;
    }

}
