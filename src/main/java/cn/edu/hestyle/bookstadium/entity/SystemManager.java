package cn.edu.hestyle.bookstadium.entity;

/**
 * 系统管理员 实体类
 * @author hestyle
 */
public class SystemManager {
    public final static String SYSTEM_MANAGER_ROLE = "SYSTEM_MANAGER_ROLE";

    /**id*/
    private Integer id;
    /**用户名*/
    private String username;
    /**密码*/
    private String password;
    /**盐值*/
    private String saltValue;
    /**头像*/
    private String avatarPath;
    /**电话号码*/
    private String phoneNumber;
    /**是否删除，0未删除，1已删除*/
    private Integer isDelete;
    /**token*/
    private String token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSaltValue() {
        return saltValue;
    }

    public void setSaltValue(String saltValue) {
        this.saltValue = saltValue;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "SystemManager{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", saltValue='" + saltValue + '\'' +
                ", avatarPath='" + avatarPath + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", isDelete=" + isDelete +
                ", token='" + token + '\'' +
                '}';
    }
}
