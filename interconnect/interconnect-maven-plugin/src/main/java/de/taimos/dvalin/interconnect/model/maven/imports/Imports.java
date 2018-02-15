package de.taimos.dvalin.interconnect.model.maven.imports;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import de.taimos.dvalin.interconnect.model.ToBeRemoved;
import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import org.joda.time.DateTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * @author psigloch
 */
public abstract class Imports<T extends IGeneratorDefinition> extends TreeSet<String> {

    private static final long serialVersionUID = -4267239585429637931L;

    private String ivoPackageName;
    private Set<String> internalSet = new HashSet<>();

    /**
     * initial default imports
     */
    public abstract void initDefaults();

    /**
     * @param definition init imports from the given definition
     */
    public abstract void initFromDefintion(T definition);


    /**
     * adds jsonDeserialize
     */
    public void withJsonDeserialize() {
        this.with(JsonDeserialize.class);
    }

    /**
     * add nullable
     */
    public void withNullable() {
        this.with(Nullable.class);
    }

    /**
     * add nonnull
     */
    public void withNunnull() {
        this.with(Nonnull.class);
    }

    /**
     * adds tobereomoced
     */
    public void withToBeReomoved() {
        this.with(ToBeRemoved.class);
    }

    /**
     * adds json ignore
     */
    public void withJsonIgnore() {
        this.with(JsonIgnore.class);
    }

    /**
     * adds json pojo builder
     */
    public void withJsonPOJOBuilder() {
        this.with(JsonPOJOBuilder.class);
    }

    /**
     * adds jsontypeinfo
     */
    public void withJsonTypeInfo() {
        this.with(JsonTypeInfo.class);
    }

    /**
     * adds bgidecimal
     */
    public void withBigDecimal() {
        this.with(BigDecimal.class.getCanonicalName());
    }

    /**
     * adds joda time
     */
    public void withDateTime() {
        this.with(DateTime.class.getCanonicalName());
    }

    /**
     * adds uuid
     */
    public void withUUID() {
        this.with(UUID.class.getCanonicalName());
    }


    /**
     * @param value the import to add as string
     */
    public void with(String value) {
        if(!this.internalSet.contains(value)) {
            this.add(value);
            this.internalSet.add(value);
        }
    }

    /**
     * @param clazz the import to add as class
     */
    public void with(Class<?> clazz) {
        this.with(clazz.getCanonicalName());
    }

    /**
     * @return the ivoPackageName
     */
    public String getIvoPackageName() {
        return this.ivoPackageName;
    }

    /**
     * @param ivoPackageName the ivoPackageName to set
     */
    public void setIvoPackageName(String ivoPackageName) {
        this.ivoPackageName = ivoPackageName;
    }
}
