package world.controller.wilderness

import api.attr.Attr
import api.predef.*
import com.google.common.collect.ImmutableSet
import io.luna.game.event.impl.CommandEvent
import io.luna.game.model.Area
import io.luna.game.model.Location
import io.luna.game.model.Position
import io.luna.game.model.mob.Player
import io.luna.game.model.mob.controller.ControllableEvent
import io.luna.game.model.mob.controller.PlayerLocationController
import io.luna.game.model.mob.inter.WalkableInterface
import kotlin.reflect.full.cast

/**
 * A [PlayerLocationController] implementation for wilderness areas.
 *
 * @author lare96
 */
object WildernessAreaController : PlayerLocationController() {

    /**
     * The player's current wilderness level. Will be `0` if not in the wilderness.
     */
    var Player.wildernessLevel by Attr.int()

    /**
     * A list of teleportation commands that don't work above level 20 wilderness.
     */
    val TELEPORT_COMMANDS = setOf("home")

    override fun canEnter(plr: Player): Boolean {
        setWildernessLevel(plr)
        plr.interfaces.open(WalkableInterface(197))
        plr.interactions.show(INTERACTION_ATTACK)
        return true
    }

    override fun canExit(plr: Player): Boolean {
        plr.interactions.hide(INTERACTION_ATTACK)
        plr.interfaces.closeWalkable()
        plr.wildernessLevel = 0
        plr.clearText(199)
        return true
    }

    override fun canMove(plr: Player, newPos: Position): Boolean {
        setWildernessLevel(plr)
        return true
    }

    override fun computeLocations(): ImmutableSet<Location> = ImmutableSet.of(
            Area.of(2041, 3519, 3392, 3966)
    )

    override fun onPlayerEvent(player: Player, event: ControllableEvent): Boolean {
        if (event is CommandEvent) {
            if (player.wildernessLevel >= 20 && TELEPORT_COMMANDS.contains(event.name)) {
                player.sendMessage("You cannot use this when above wilderness level 20!") // TODO actual message
                // TODO test and disable for admin+
                return false
            }
        }
        return true
    }

    /**
     * Sets and displays the wilderness level for [plr].
     */
    private fun setWildernessLevel(plr: Player): Int {
        var newLevel = if (plr.position.y > 6400) plr.position.y - 6400 else plr.position.y
        newLevel = ((newLevel - 3520) / 8) + 1
        plr.wildernessLevel = newLevel
        plr.sendText("@yel@Level: $newLevel", 199)
        return newLevel
    }
}