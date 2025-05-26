package org.acme.ruleEngine.interfaces;

public interface IRule <I>{
    boolean matches(I module);
    Object apply(I request);
}
