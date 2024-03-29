package cn.edu.hestyle.bookstadium.service;

import cn.edu.hestyle.bookstadium.entity.StadiumManager;
import cn.edu.hestyle.bookstadium.service.exception.*;

import java.util.HashMap;
import java.util.List;

/**
 * StadiumManager 业务层接口
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/3 9:55 上午
 */
public interface IStadiumManagerService {
    /**
     * StadiumManager 通过username、password登录
     * @param username                  用户名
     * @param password                  密码
     * @return                          StadiumManager
     * @throws LoginFailedException     登录失败异常
     */
    StadiumManager login(String username, String password) throws LoginFailedException;

    /**
     * StadiumManager账号注册
     * @param stadiumManager            待注册的账号
     * @throws RegisterFailedException  注册失败异常
     */
    void register(StadiumManager stadiumManager) throws RegisterFailedException;

    /**
     * StadiumManager 通过username查找账号信息
     * @param username                  用户名
     * @return                          StadiumManager
     */
    StadiumManager findByUsername(String username) throws AccountNotFoundException;

    /**
     * 通过id查找
     * @param stadiumManagerId          stadiumManagerId
     * @return                          stadiumManager
     * @throws FindFailedException      查找失败异常
     */
    StadiumManager findById(Integer stadiumManagerId) throws FindFailedException;

    /**
     * （系统）通过id查找(controller不能直接调用，会泄露盐值、token)
     * @param id    id
     * @return      Stadium
     */
    StadiumManager systemFindById(Integer id) throws FindFailedException;

    /**
     * 更新StadiumManager账号信息
     * @param stadiumManagerId          StadiumManager id
     * @param modifyDataMap             key value
     * @throws ModifyFailedException    更新字段数字非法
     */
    void modifyInfo(Integer stadiumManagerId, HashMap<String, Object> modifyDataMap) throws ModifyFailedException;

    /**
     * 修改StadiumManager账号密码
     * @param stadiumManagerId          stadiumManagerId
     * @param beforePassword            原密码
     * @param newPassword               新密码
     * @throws ModifyFailedException    更新错误
     */
    void changePassword(Integer stadiumManagerId, String beforePassword, String newPassword) throws ModifyFailedException;

    /**
     * systemManager分页查询StadiumManager
     * @param pageIndex             pageIndex
     * @param pageSize              pageSize
     * @return                      List StadiumManager
     */
    List<StadiumManager> systemManagerFindByPage(Integer pageIndex, Integer pageSize, String usernameKey);

    /**
     * 获取StadiumManager数量
     * @return                      User数量
     */
    Integer getCount(String usernameKey);

    /**
     * systemManager修改stadiumManager
     * @param systemManagerId       systemManagerId
     * @param stadiumManager        stadiumManager
     */
    void systemManagerModify(Integer systemManagerId, StadiumManager stadiumManager);

    /**
     * systemManager重置stadiumManager密码
     * @param systemManagerId       systemManagerId
     * @param stadiumManagerId      stadiumManagerId
     * @param newPassword           newPassword
     */
    void systemManagerResetPassword(Integer systemManagerId, Integer stadiumManagerId, String newPassword);

    /**
     * systemManager将stadiumManager拉黑
     * @param systemManagerId       systemManagerId
     * @param stadiumManagerId      stadiumManagerId
     */
    void systemManagerAddToBlack(Integer systemManagerId, Integer stadiumManagerId);

    /**
     * systemManager解除stadiumManager拉黑状态
     * @param systemManagerId       systemManagerId
     * @param stadiumManagerId      stadiumManagerId
     */
    void systemManagerRemoveFromBlack(Integer systemManagerId, Integer stadiumManagerId);

    /**
     * systemManager通过stadiumManagerId删除stadiumManager
     * @param systemManagerId       systemManagerId
     * @param stadiumManagerId      stadiumManagerId
     */
    void systemManagerDeleteById(Integer systemManagerId, Integer stadiumManagerId);
}
