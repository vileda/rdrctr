package cc.vileda.rdrctr.redirecter.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class RedirectLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private Date createdAt = new Date();

    private String referer;
    private String fromHost;
    private String toHost;
    private String ip;

    public RedirectLog() {
    }

    public RedirectLog(String referer, String fromHost, String toHost, String ip) {
        this.referer = referer;
        this.fromHost = fromHost;
        this.toHost = toHost;
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getFromHost() {
        return fromHost;
    }

    public void setFromHost(String fromHost) {
        this.fromHost = fromHost;
    }

    public String getToHost() {
        return toHost;
    }

    public void setToHost(String toHost) {
        this.toHost = toHost;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
