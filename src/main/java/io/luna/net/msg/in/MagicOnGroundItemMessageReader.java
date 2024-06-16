package io.luna.net.msg.in;

import io.luna.game.event.impl.UseSpellEvent.MagicOnGroundItemEvent;
import io.luna.game.model.Position;
import io.luna.game.model.item.GroundItem;
import io.luna.game.model.mob.Player;
import io.luna.net.codec.ByteOrder;
import io.luna.net.codec.ValueType;
import io.luna.net.msg.GameMessage;
import io.luna.net.msg.GameMessageReader;

/**
 * A {@link GameMessageReader} that intercepts data sent for when a player uses magic on a ground item.
 *
 * @author lare96
 */
public final class MagicOnGroundItemMessageReader extends GameMessageReader<MagicOnGroundItemEvent> {

    @Override
    public MagicOnGroundItemEvent decode(Player player, GameMessage msg) {
        int itemId = msg.getPayload().getShort(false, ByteOrder.LITTLE);
        int itemY = msg.getPayload().getShort(false);
        int spellId = msg.getPayload().getShort(false, ByteOrder.LITTLE);
        int itemX = msg.getPayload().getShort(false, ValueType.ADD, ByteOrder.LITTLE);
        GroundItem groundItem = player.getWorld().getItems().findAll(new Position(itemX, itemY)).
                filter(item -> item.getId() == itemId && item.isVisibleTo(player)).
                findFirst().orElse(null);
        return new MagicOnGroundItemEvent(player, spellId, groundItem);
    }

    @Override
    public boolean validate(Player player, MagicOnGroundItemEvent event) {
        return event.getTargetItem() != null;
    }
}

