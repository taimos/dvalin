package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.CollectionMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MapMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;

import java.util.List;

/**
 * implements most helper methods used within velocity templates for ivos
 *
 * @author psigloch
 */
public abstract class TemplateIVOModel extends AbstractIVOModel {

    /**
     * @return the package name
     */
    public String getPackageName() {
        return this.definition.getPackageName();
    }

    /**
     * @return the imports
     */
    public Imports getImports() {
        return this.imports;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return this.definition.getAuthor();
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return this.definition.getComment();
    }

    /**
     * @return if ivo is deprecated
     */
    public boolean isDeprecated() {
        return this.definition.isDeprecated();
    }

    /**
     * @return the removedate as string
     */
    public String getRemoveDate() {
        return this.definition.getRemovalDate();
    }

    /**
     * @return the class name of the ivo
     */
    public String getClazzName() {
        return this.definition.getIVOClazzName(false);
    }

    /**
     * @return the interface class name of the ivo
     */
    public String getInterfaceClazzName() {
        return this.definition.getIVOClazzName(true);
    }

    /**
     * @return wheteher the ivo has a parent object or not
     */
    public boolean hasParentClazz() {
        return this.definition.getParentName() != null;
    }

    /**
     * @return the clazz name of the parent object, or null
     */
    public String getParentClazzName() {
        return !this.hasParentClazz() ? null : this.definition.getParentClazzName(false);
    }

    /**
     * @return the interface clazz name of the parent object, or null
     */
    public String getParentInterfaceName() {
        return !this.hasParentClazz() ? null : this.definition.getParentClazzName(true);
    }

    /**
     * @return the parent builder extends, or null
     */
    public String getParentBuilder() {
        return !this.hasParentClazz() ? "" : " extends Abstract" + this.getParentClazzName() + "Builder<E>";
    }

    /**
     * @return the serial version from the defintion
     */
    public Integer getSerialVersion() {
        return this.definition.getVersion();
    }

    /**
     * @return all fields used within the ivo definition
     */
    public List<MemberDef> getAllFields() {
        return this.allMemberDefs;
    }

    /**
     * @return all fields used within the ivo definition which are of type Collection
     */
    public List<CollectionMemberDef> getCollectionFields() {
        return this.collectionMemberDefs;
    }

    /**
     * @return all fields used within the ivo definition which are neither of type Collection or Map
     */
    public List<MemberDef> getNoCollectionFields() {
        return this.noCollectionMemberDefs;
    }

    /**
     * @return all fields used within the ivo definition which are of type Map
     */
    public List<MapMemberDef> getMapFields() {
        return this.mapMemberDefs;
    }

    /**
     * @return whether ivo is audited or not
     */
    public boolean isAudited() {
        return this.definition.getAuditing();
    }

    /**
     * @return whether ivo has a identity field or not
     */
    public boolean isIdentity() {
        return this.definition.getIdentity();
    }


}
