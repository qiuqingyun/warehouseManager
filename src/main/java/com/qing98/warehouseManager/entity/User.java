package com.qing98.warehouseManager.entity;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author QIU
 */
@Entity
public class User implements UserDetails {
    /**
     * 账户编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 账户名称
     */
    private String username;
    /**
     * 账户密码
     */
    private String password;
    /**
     * 账户是否没有过期
     */
    private boolean accountNonExpired;
    /**
     * 账户是否没有被锁定
     */
    private boolean accountNonLocked;
    /**
     * 密码是否没有过期
     */
    private boolean credentialsNonExpired;
    /**
     * 账户是否可用
     */
    private boolean enabled;
    /**
     * 账户身份
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Role role;

    /**
     * 返回获取用户权限
     */
    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(getRole().getName()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "\nUser{\n" +
                "  id: " + id + '\n' +
                "  username: '" + username + "'\n" +
                "  password: '" + password + "'\n" +
                "  accountNonExpired: " + accountNonExpired + '\n' +
                "  accountNonLocked: " + accountNonLocked + '\n' +
                "  credentialsNonExpired: " + credentialsNonExpired + '\n' +
                "  enabled: " + enabled + '\n' +
                "  roles: " + role + '\n' +
                '}';
    }

    /**
     * 判断两个用户对象是否相等，如果提供的对象是一个具有相同id值的用户实例，则返回true
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return this.id.equals(((User) obj).id);
        }
        return false;
    }

    /**
     * 返回用户编号的哈希
     */
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
