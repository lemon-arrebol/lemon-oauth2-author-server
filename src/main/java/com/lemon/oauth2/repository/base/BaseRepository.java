package com.lemon.oauth2.repository.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * @author lemon
 * @description 抽取持久层通用方法
 * @date 2020-05-05 21:33
 */
@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {

    /**
     * @param id
     * @return java.util.Optional<T>
     * @description 根据id 获取信息
     * @author lemon
     * @date 2020-05-05 21:33
     */
    @Override
    Optional<T> findById(Integer id);

    /**
     * @param
     * @return java.util.List<T>
     * @description 获取所有的信息
     * @author lemon
     * @date 2020-05-05 21:33
     */
    @Override
    List<T> findAll();

    /**
     * @param entity
     * @return void
     * @description 删除指定的信息
     * @author lemon
     * @date 2020-05-05 21:33
     */
    @Override
    void delete(T entity);

    /**
     * @param id
     * @return void
     * @description 根据id 删除信息
     * @author lemon
     * @date 2020-05-05 21:33
     */
    @Override
    void deleteById(Integer id);

}
