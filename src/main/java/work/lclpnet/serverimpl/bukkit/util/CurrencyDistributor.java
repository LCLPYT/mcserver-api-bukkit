/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import org.bukkit.entity.Player;
import work.lclpnet.lclpnetwork.api.APIResponse;
import work.lclpnet.lclpnetwork.api.ResponseEvaluationException;
import work.lclpnet.serverapi.api.CurrencyMassIncrementTransaction;
import work.lclpnet.serverimpl.bukkit.Config;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;

import java.util.function.Consumer;

public class CurrencyDistributor {

    public static void givePoints(Iterable<Player> players, int amount) {
        giveCurrency(transaction -> players.forEach(p -> transaction.addPoints(p.getUniqueId().toString(), amount)), "Could not give points.");
    }

    public static void giveCurrency(Consumer<CurrencyMassIncrementTransaction> transformer) {
        giveCurrency(transformer, "Could not give currency.");
    }

    public static void giveCurrency(Consumer<CurrencyMassIncrementTransaction> transformer, String msgWhenError) {
        if (MCServerBukkit.isTestMode()) return;

        final CurrencyMassIncrementTransaction massTransaction = new CurrencyMassIncrementTransaction();
        if(transformer != null) transformer.accept(massTransaction);

        MCServerBukkit.getAPI().incrementStat(massTransaction)
                .exceptionally(ex -> {
                    if (Config.debug) {
                        ex.printStackTrace();

                        APIResponse resp = ResponseEvaluationException.getResponseFromCause(ex);
                        if(resp != null) System.err.println(resp);
                    }
                    return null;
                })
                .thenAccept(result -> {
                    if(result == null || !result.isSuccess())
                        System.err.println(msgWhenError);
                });
    }
}
