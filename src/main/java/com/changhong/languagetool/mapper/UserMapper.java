package com.changhong.languagetool.mapper;

import com.changhong.languagetool.bean.User;
import org.apache.catalina.LifecycleState;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserMapper {


    @Select( "select * from user")
    List<User> getAllUser();

    @Select("select * from user where username = #{_parameter}")
    User getPasswordByUsername(String  username);

    @Update("update user set permission = #{permission} where username = #{username}")
    int updateUserPermisson(User user);

    @Insert("insert  into user(username,password,role,permission) " +
            "values(#{username},#{password},#{role},#{permission})")
    int insertUser(User user);

    @Delete("delete user where username = #{username}")
    int deleteUser(User user);
}
