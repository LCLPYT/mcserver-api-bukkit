/*
 * Copyright (c) 2022 LCLP.
 *
 * Licensed under the MIT License. For more information, consider the LICENSE file in the project's root directory.
 */

package work.lclpnet.serverimpl.bukkit.util;

import work.lclpnet.serverapi.api.MassIncrementTransaction;
import work.lclpnet.serverapi.util.IntStatMap;
import work.lclpnet.serverimpl.bukkit.Config;
import work.lclpnet.serverimpl.bukkit.MCServerBukkit;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class StatsHelper {

    public static CompletableFuture<Void> commit(IntStatMap statMap, Supplier<MassIncrementTransaction> transactionSupplier) {
        MassIncrementTransaction transaction = transactionSupplier.get();
        statMap.getAll().forEach(
                (type, stats) -> stats.forEach(
                        (uuid, amount) -> transaction.add(uuid, type, amount)
                ));

        statMap.reset();

        return MCServerBukkit.getAPI().incrementStat(transaction)
                .exceptionally(ex -> {
                    if (Config.debug) ex.printStackTrace();
                    return null;
                })
                .thenAccept(result -> {
                    if (result == null) System.err.println("There was an error updating the stats.");
                    else if (!result.isSuccess()) System.err.println("The stats could not be updated properly.");
                    else System.out.println("Stats updated successfully.");
                });
    }

    public static CompletableFuture<Void> updateLastPlayed(String statType, Iterable<String> playerUuids) {
        return MCServerBukkit.getAPI().updateLastPlayed(statType, playerUuids)
                .exceptionally(ex -> {
                    if (Config.debug) ex.printStackTrace();
                    return null;
                }).thenAccept(result -> {
                    if (result == null) System.err.println("There was an error updating last seen.");
                    else if (!result.isSuccess()) System.err.println("Last seen could not be updated properly.");
                    else System.out.println("Last seen updated.");
                });
    }
}
