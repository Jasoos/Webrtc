package com.backend.callingapp.jingleExtension.ice.elements;

import org.jivesoftware.smack.packet.ExtensionElement;

import java.util.ArrayList;
import java.util.List;

public class Group implements ExtensionElement {
    public static final String NAMESPACE = "urn:xmpp:jingle:apps:grouping:0";

    public static final String ELEMENT = "group";

    public static final String ATTRIBUTE_NAME = "semantics";

    String semantic = null;

    List<String> contents = new ArrayList<>();

    public String getSemantic() {
        return semantic;
    }

    public void setSemantic(String semantic) {
        this.semantic = semantic;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public void addContentValue(String value){
        if (this.contents == null) this.contents = new ArrayList<>();
        this.contents.add(value);
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        return null;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
