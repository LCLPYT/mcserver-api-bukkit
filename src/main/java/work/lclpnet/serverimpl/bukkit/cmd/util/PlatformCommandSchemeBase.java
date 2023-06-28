/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd.util;

import work.lclpnet.serverapi.MCServerAPI;
import work.lclpnet.serverapi.cmd.ICommandScheme;
import work.lclpnet.serverapi.cmd.IDebuggable;
import work.lclpnet.serverapi.util.IPlatformBridge;
import work.lclpnet.serverapi.util.ServerContext;
import work.lclpnet.serverimpl.bukkit.Config;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;
import work.lclpnet.serverimpl.bukkit.util.BukkitPlatformBridge;

public abstract class PlatformCommandSchemeBase<T> extends CommandSchemeBase<T> implements ICommandScheme.IPlatformCommandScheme<T>, IDebuggable {

    private final ServerContext context;

    protected PlatformCommandSchemeBase(ServerContext context) {
        this.context = context;
    }

    @Override
    public MCServerAPI getAPI() {
        return MCServerBukkit.getAPI();
    }

    @Override
    public IPlatformBridge getPlatformBridge() {
        return BukkitPlatformBridge.getInstance();
    }

    @Override
    public boolean shouldDebug() {
        return Config.debug;
    }

    @Override
    public ServerContext getContext() {
        return context;
    }
}
