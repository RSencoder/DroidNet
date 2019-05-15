package com.mingxiangChen.droidnet.table;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * A litepal table contains the intercept rules 
 * used by {@link com.mingxiangChen.droidnet.interceptor.FirewallInterceptor}.
 * 
 * @author RSmxchen
 * @since 2019-04-29 15:40
 */
public class InterceptRules extends LitePalSupport {
    
    @Column(nullable = false)
    private int priority;
    
    @Column(nullable = false, defaultValue = "N/A")
    private String session;

    @Column(nullable = false, defaultValue = "N/A")
    private String packageName;

    @Column(nullable = false, defaultValue = "N/A")
    private String pattern;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
