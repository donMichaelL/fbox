package org.fbox.core.registry;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.fbox.common.application.configuration.ApplicationInfo;
import org.fbox.common.registry.AbstractRegistry;
import org.fbox.common.registry.IRegistry;

/**
 * Session Bean implementation class ApplicationStatusRegistry
 */
@Singleton
@Remote ( { IRegistry.class })
public class ApplicationInfoRegistry extends AbstractRegistry<ApplicationInfo> {

}
