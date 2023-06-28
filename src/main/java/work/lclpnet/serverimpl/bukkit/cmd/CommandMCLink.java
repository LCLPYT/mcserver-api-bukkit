/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.cmd;

import work.lclpnet.serverapi.cmd.MCLinkCommandScheme;
import work.lclpnet.serverapi.util.ServerContext;
import work.lclpnet.serverimpl.bukkit.cmd.util.PlatformCommandSchemeBase;

public class CommandMCLink extends PlatformCommandSchemeBase<Boolean> implements MCLinkCommandScheme {

    public CommandMCLink(ServerContext context) {
        super(context);
    }
}
