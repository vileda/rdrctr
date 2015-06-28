package cc.vileda.rdrctr.redirecter.entity;


import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Redirect {

    public Redirect() { }

    public Redirect(String fromHost, String toHost) {
        this.fromHost = fromHost;
        this.toHost = toHost;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String fromHost;

    @Column(nullable = false)
    private String toHost;

    @Column(nullable = false)
    private long viewCount = 0L;
    
    private Date createdAt = new Date();

    private Date updatedAt = new Date();

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

    public JsonObject asJsonObject() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return Json.createObjectBuilder()
                .add("id", id)
                .add("fromHost", fromHost)
                .add("toHost", toHost)
                .add("viewCount", viewCount)
                .add("createdAt", dateFormat.format(createdAt))
                .add("updatedAt", dateFormat.format(updatedAt))
                .build();
    }
}
