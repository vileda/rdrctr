package cc.vileda.rdrctr.redirecter.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.persistence.*;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class RedirectLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "redirectlog_seq_gen")
    @SequenceGenerator(name = "redirectlog_seq_gen", sequenceName = "redirectlog_id_seq")
    private long id;

    @ManyToOne
    private Redirect redirect;

    @Column(nullable = false)
    private final Date createdAt = new Date();

    private String referer;
    private String fromHost;
    private String toHost;
    private String ip;

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

    public Redirect getRedirect() {
        return redirect;
    }

    public JsonObject asJsonObject() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        return Json.createObjectBuilder()
                .add("id", id)
                .add("fromHost", fromHost)
                .add("toHost", jsonSafe(toHost))
                .add("referer", jsonSafe(referer))
                .add("ip", ip)
                .add("createdAt", dateFormat.format(createdAt))
                .build();
    }

    private JsonValue jsonSafe(String value) {
        return value == null
                ? JsonValue.NULL
                : Json.createObjectBuilder().add(value, value).build().getJsonString(value);
    }
}
