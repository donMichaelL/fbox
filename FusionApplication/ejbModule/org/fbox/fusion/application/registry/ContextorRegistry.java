package org.fbox.fusion.application.registry;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import org.fbox.common.data.IContextorContext;
import org.fbox.common.registry.AbstractRegistry;



/**
 * Session Bean implementation class ContextRegistry
 */
@Singleton
@LocalBean
public class ContextorRegistry extends AbstractRegistry<IContextorContext>{
    
}
