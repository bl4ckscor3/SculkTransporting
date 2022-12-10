package sculktransporting.misc;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationInfo;
import net.minecraft.world.level.gameevent.vibrations.VibrationListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationSelector;
import net.minecraft.world.phys.Vec3;

public class OneReceiverVibrationListener extends VibrationListener {
	public static Codec<OneReceiverVibrationListener> oneReceiverCodec(VibrationListener.VibrationListenerConfig config) {
		return RecordCodecBuilder.create(builder -> builder.group(PositionSource.CODEC.fieldOf("source").forGetter(listener -> {
			return listener.listenerSource;
		}), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("range").forGetter((listener) -> {
			return listener.listenerRange;
		}), VibrationInfo.CODEC.optionalFieldOf("event").forGetter((listener) -> {
			return Optional.ofNullable(listener.currentVibration);
		}), VibrationSelector.CODEC.fieldOf("selector").forGetter((listener) -> {
			return listener.selectionStrategy;
		}), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("event_delay").orElse(0).forGetter((listener) -> {
			return listener.travelTimeInTicks;
		})).apply(builder, (pos, range, currentVibration, selector, travelTime) -> {
			return new OneReceiverVibrationListener(pos, range, config, currentVibration.orElse(null), selector, travelTime);
		}));
	}

	public OneReceiverVibrationListener(PositionSource pos, int range, VibrationListener.VibrationListenerConfig config, VibrationInfo currentVibration, VibrationSelector selector, int travelTime) {
		super(pos, range, config);
		this.currentVibration = currentVibration;
		this.travelTimeInTicks = travelTime;
		this.selectionStrategy = selector;
	}

	public OneReceiverVibrationListener(PositionSource pos, int range, VibrationListenerConfig config) {
		this(pos, range, config, null, new VibrationSelector(), 0);
	}

	@Override
	public void scheduleVibration(ServerLevel level, GameEvent gameEvent, GameEvent.Context ctx, Vec3 from, Vec3 to) {
		super.scheduleVibration(level, gameEvent, ctx, from, to);

		if (ctx.sourceEntity() instanceof ItemEntity item && selectionStrategy.currentVibrationData.isPresent() && ctx.sourceEntity().equals(selectionStrategy.currentVibrationData.get().getLeft().entity()))
			item.discard(); //If this item signal is scheduled for one receiver, mark it as such to prevent it from getting sent to another one
	}
}
