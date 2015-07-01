package cc.vileda.rdrctr.redirecter.entity;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Redirect {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String fromHost;

    @Column(nullable = false)
    private String toHost;

    @Column(nullable = false)
    private long viewCount = 0L;

    @OneToMany(mappedBy = "redirect")
    private List<RedirectLog> redirectLogs = new ArrayList<>();
    
    private Date createdAt = new Date();

    private Date updatedAt = new Date();

    public Redirect() { }

    public Redirect(String fromHost, String toHost) {
        this.fromHost = fromHost;
        this.toHost = toHost;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFromHost() {
        return fromHost;
    }

    public void setFromHost(String pattern) {
        this.fromHost = pattern;
    }

    public String getToHost() {
        return toHost;
    }

    public void setToHost(String location) {
        this.toHost = location;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<RedirectLog> getRedirectLogs() {
        return redirectLogs;
    }

    public void setRedirectLogs(List<RedirectLog> redirectLogs) {
        this.redirectLogs = redirectLogs;
    }

    public JsonObject asJsonObject() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (RedirectLog redirectLog : redirectLogs) {
            arrayBuilder.add(redirectLog.asJsonObject());
        }
        return Json.createObjectBuilder()
                .add("id", id)
                .add("fromHost", fromHost)
                .add("toHost", toHost)
                .add("viewCount", viewCount)
                .add("createdAt", dateFormat.format(createdAt))
                .add("updatedAt", dateFormat.format(updatedAt))
                .add("redirectLogs", arrayBuilder.build())
                .build();
    }
}
