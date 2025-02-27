package dev.hikari.SimpleSpotifyController.client.utils

enum class ColorHelper(val code: String) {
    BLACK("§0"),
    DARK_BLUE("§1"),
    DARK_GREEN("§2"),
    DARK_AQUA("§3"),
    DARK_RED("§4"),
    DARK_PURPLE("§5"),
    GOLD("§6"),
    GRAY("§7"),
    DARK_GRAY("§8"),
    BLUE("§9"),
    GREEN("§a"),
    AQUA("§b"),
    RED("§c"),
    LIGHT_PURPLE("§d"),
    YELLOW("§e"),
    WHITE("§f");

    override fun toString(): String {
        return code
    }

    companion object {
        fun formatText(color: ColorHelper, text: String): String {
            return "${color.code}$text§r"
        }
    }
}