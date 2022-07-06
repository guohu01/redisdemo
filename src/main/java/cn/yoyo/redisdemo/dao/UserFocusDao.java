package cn.yoyo.redisdemo.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface UserFocusDao {
    //创建关注关系
    public void insert(@Param("id") Integer id, @Param("focusid") Integer focusid, @Param("createTime") Date createTime);

    /**
     * 判断当前登入的用户与目标用户的直接关系
     * @param id
     * @param focusUserId
     * @return
     */
    public List<Integer> getFollowStat(@Param("id") Integer id, @Param("focusUserId") Integer focusUserId);

    /**
     * 删除关注关系
     * @param id
     * @param focusUserId
     */
    public void delete(Integer id, Integer focusUserId);

    /**
     * 获取粉丝数
     * @param id
     * @return
     */
    public int getFs(Integer id);

    /**
     * 获取关注数
     * @param id
     * @return
     */
    public int getFollows(Integer id);
}
