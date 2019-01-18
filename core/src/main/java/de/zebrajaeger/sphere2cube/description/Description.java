package de.zebrajaeger.sphere2cube.description;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Lars Brandt, Silpion IT Solutions GmbH
 */
public class Description {
    private String title;
    private String description;
    private Date created;
    private List<String> tags;

    public Description merge(Description o) {
        if (o != null) {
            if (o.title != null) {
                this.title = o.title;
            }
            if (o.description != null) {
                this.description = o.description;
            }
            if (o.created != null) {
                this.created = o.created;
            }
            if (o.tags != null && !o.tags.isEmpty()) {
                if (this.tags == null) {
                    this.tags = new LinkedList<>(o.tags);
                } else {
                    o.tags.forEach(tag -> {
                        if (this.tags.contains(tags)) {
                            this.tags.add(tag);
                        }
                    });
                }
                this.created = o.created;
            }
        }

        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
