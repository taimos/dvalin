package de.taimos.dvalin.interconnect.model.maven.imports;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import de.taimos.dvalin.interconnect.model.ToBeRemoved;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import org.joda.time.DateTime;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

/**
 * @author psigloch
 */
public abstract class Imports<T extends IGeneratorDefinition> extends TreeSet<String> {

    private static final long serialVersionUID = -4267239585429637931L;

    private String ivoPackageName;
    private final Set<String> internalSet = new HashSet<>();

    /**
     * initial default imports
     */
    public abstract void initDefaults();

    /**
     * @param definition init imports from the given definition
     * @param model      the model
     * @param <K>        the model
     */
    public abstract <K extends AbstractInterconnectModel<T, ? extends Imports<T>>> void initFromDefinition(T definition, K model);


    /**
     * adds jsonDeserialize
     */
    public void withJsonDeserialize() {
        this.add(JsonDeserialize.class);
    }

    /**
     * add nullable
     */
    public void withNullable() {
        this.add(Nullable.class);
    }

    /**
     * add nonnull
     */
    public void withNonnull() {
        this.add(Nonnull.class);
    }

    /**
     * adds tobereomoced
     */
    public void withToBeRemoved() {
        this.add(ToBeRemoved.class);
    }

    /**
     * adds json ignore
     */
    public void withJsonIgnore() {
        this.add(JsonIgnore.class);
    }

    /**
     * adds json pojo builder
     */
    public void withJsonPOJOBuilder() {
        this.add(JsonPOJOBuilder.class);
    }

    /**
     * adds jsontypeinfo
     */
    public void withJsonTypeInfo() {
        this.add(JsonTypeInfo.class);
    }

    /**
     * adds jsontypeinfo
     */
    public void withJsonIgnoreProperties() {
        this.add(JsonIgnoreProperties.class);
    }

    /**
     * adds bgidecimal
     */
    public void withBigDecimal() {
        this.add(BigDecimal.class.getCanonicalName());
    }

    /**
     * adds joda time
     */
    public void withDateTime() {
        this.add(DateTime.class.getCanonicalName());
    }

    /**
     * adds uuid
     */
    public void withUUID() {
        this.add(UUID.class.getCanonicalName());
    }


    /**
     * @param value the import to add as string
     * @return true if this set did not already contain the specified element
     */
    @Override
    public boolean add(String value) {
        if (!this.internalSet.contains(value)) {
            super.add(value);
            return this.internalSet.add(value);
        }
        return false;
    }

    /**
     * @param clazz the import to add as class
     */
    public void add(Class<?> clazz) {
        this.add(clazz.getCanonicalName());
    }

    @Override
    public boolean remove(Object o) {
        if (this.internalSet.remove(o)) {
            return super.remove(o);
        }
        return false;
    }

    /**
     * @param clazz the clazz to remove
     * @return true if this set did contain the specified element
     */
    public boolean remove(Class<?> clazz) {
        if (this.internalSet.remove(clazz.getCanonicalName())) {
            return super.remove(clazz.getCanonicalName());
        }
        return false;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Imports)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Imports<?> imports = (Imports<?>) o;
        return Objects.equals(this.getIvoPackageName(), imports.getIvoPackageName()) && Objects.equals(this.internalSet, imports.internalSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.getIvoPackageName(), this.internalSet);
    }
}
