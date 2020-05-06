package com.lemon.oauth2.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author lemon
 * @description redis 工具类
 * @date 2020-05-05 21:36
 */
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * @param key
     * @param time
     * @return boolean
     * @description 指定缓存失效时间
     * @author lemon
     * @date 2020-05-05 21:36
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:expire() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * @param key
     * @return long
     * @description 根据key 获取过期时间
     * @author lemon
     * @date 2020-05-05 21:36
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * @param key
     * @return boolean
     * @description 判断key是否存在
     * @author lemon
     * @date 2020-05-05 21:36
     */
    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("class:RedisUtils method:exists() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * @param key
     * @return void
     * @description 删除缓存
     * @author lemon
     * @date 2020-05-05 21:36
     */
    @SuppressWarnings("unchecked")
    public void deleteCache(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    public <T> T get(String key, Class<T> clazz, long expire) {

        Object value = redisTemplate.opsForValue().get(key);
        if (expire != -1L) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
        return value == null ? null : fromJson(value, clazz);
    }

    public <T> T get(String key, Class<T> clazz) {

        return get(key, clazz, -1L);
    }

    /**
     * @param key
     * @return java.lang.Object
     * @description 根据key获取value
     * @author lemon
     * @date 2020-05-05 21:36
     */
    public Object getValueByKey(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key
     * @param value
     * @param expire 时间(秒) time要大于0 如果expire小于等于0 将设置无限期
     * @return void
     * @description
     * @author houjunttrue成功 false失败ao
     * @date 2020-05-05 21:36
     */
    public void set(String key, Object value, long expire) {
        redisTemplate.opsForValue().set(key, value);
        if (expire != -1L) {
            redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        }
    }

    /**
     * @param key
     * @param value
     * @return void
     * @description 普通缓存放入并设置时间
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public void set(String key, Object value) {
        set(key, value, 3600L);
    }

    /**
     * @param key
     * @param delta
     * @return long
     * @description
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public long increasing(String key, long delta) {
        if (delta < 0) {
            log.error("class:RedisUtils method:increasing() => delta:{} delta must more than 0", delta);
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * @param key
     * @param delta
     * @return long
     * @description
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            log.error("class:RedisUtils method:decr() => delta:{} delta must more than 0", delta);
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================

    /**
     * @param key
     * @param item
     * @return java.lang.Object
     * @description
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * @param key
     * @return java.util.Map<java.lang.Object, java.lang.Object>
     * @description 获取hashKey对应的所有键值
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * @param key
     * @param map
     * @return boolean
     * @description
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:hmset() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * @param key
     * @param map
     * @param time
     * @return boolean
     * @description
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:hmset() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * @param key
     * @param item
     * @param value
     * @return boolean
     * @description 向一张hash表中放入数据, 如果不存在将创建
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:hset() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * @param key
     * @param item
     * @param value
     * @param time
     * @return boolean
     * @description 向一张hash表中放入数据, 如果不存在将创建
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:hset() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * @param key
     * @param item
     * @return void
     * @description 删除hash表中的值
     * @author lemon
     * @date 2020-05-05 21:37
     */
    public void deleteValueByKey(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * @param key
     * @param item
     * @return boolean
     * @description 判断hash表中是否有该项的值
     * @author lemon
     * @date 2020-05-05 21:38
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * @param key
     * @param item
     * @param by
     * @return double
     * @description hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @author lemon
     * @date 2020-05-05 21:38
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * @param key
     * @param item
     * @param by
     * @return double
     * @description
     * @author lemon
     * @date 2020-05-05 21:38
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    //============================set=============================

    /**
     * @param key
     * @return java.util.Set<java.lang.Object>
     * @description 根据key获取Set中的所有值
     * @author lemon
     * @date 2020-05-05 21:38
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("class:RedisUtils method:sGet() => Exception {}", e.getMessage());
        }
        return null;
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("class:RedisUtils method:sHasKey() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("class:RedisUtils method:sSet() => Exception {}", e.getMessage());
        }
        return 0L;
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("class:RedisUtils method:sSetAndTime() => Exception {}", e.getMessage());
        }
        return 0L;
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("class:RedisUtils method:sGetSetSize() => Exception {}", e.getMessage());
        }
        return 0L;
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("class:RedisUtils method:setRemove() => Exception {}", e.getMessage());
        }
        return 0L;
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("class:RedisUtils method:lGet() => Exception {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("class:RedisUtils method:lGetListSize() => Exception {}", e.getMessage());
        }
        return 0L;
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("class:RedisUtils method:lGetIndex() => Exception {}", e.getMessage());
        }
        return null;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:lSet() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:lSet() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:lSet() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:lSet() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("class:RedisUtils method:lUpdateIndex() => Exception {}", e.getMessage());
        }
        return false;
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error("class:RedisUtils method:lRemove() => Exception {}", e.getMessage());
        }
        return 0L;
    }

    /**
     * JSON数据，转成Object
     */
    private <T> T fromJson(Object json, Class<T> clazz) {

        return JSONObject.parseObject(JSON.toJSONString(json), clazz);
    }
}