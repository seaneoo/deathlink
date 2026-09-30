package dev.seano.deathlink.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Unique
    private boolean deathLink = false;

    @Shadow
    public abstract @NonNull ServerLevel level();

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "die", at = @At("TAIL"))
    private void $die(DamageSource source, CallbackInfo ci) {
        if (deathLink) return;
        deathLink = true;

        try {
            @SuppressWarnings("resource") var level = level();
            var server = level.getServer();

            server.getPlayerList().getPlayers().forEach(player -> {
                if (!player.getUUID().equals(this.uuid)) player.kill(level);
            });
        } finally {
            deathLink = false;
        }
    }
}
