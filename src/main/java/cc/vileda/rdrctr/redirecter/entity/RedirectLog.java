package cc.vileda.rdrctr.redirecter.entity;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class RedirectLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Redirect redirect;

    @Column(nullable = false)
    private Date createdAt = new Date();

    private String referer = "";
    private String fromHost = "";
    private String toHost = "";
    private String ip = "";

    public RedirectLog() {
    }

    public RedirectLog(Redirect redirect, String referer, String fromHost, String toHost, String ip) {
        this.referer = referer;
        this.fromHost = fromHost;
        this.toHost = toHost;
        this.ip = ip;
        this.redirect = redirect;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Redirect getRedirect() {
        return redirect;
    }

    public void setRedirect(Redirect redirect) {
        this.redirect = redirect;
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

    public JsonObject asJsonObject() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        return Json.createObjectBuilder()
                .add("id", id)
                .add("fromHost", fromHost == null ? "" : fromHost)
                .add("toHost", toHost == null ? "" : toHost)
                .add("referer", referer == null ? "" : referer)
                .add("ip", ip == null ? "" : ip)
                .add("createdAt", dateFormat.format(createdAt))
                .build();
    }
}
